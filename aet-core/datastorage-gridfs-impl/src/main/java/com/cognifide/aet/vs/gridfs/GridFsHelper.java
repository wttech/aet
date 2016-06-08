/*
 * Cognifide AET :: Data Storage GridFs Implementation
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.vs.gridfs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.vs.VersionStorageException;
import com.cognifide.aet.vs.visitors.MetadataArtifactPathVisitor;
import com.cognifide.aet.vs.visitors.MetadataArtifactQueryParametersVisitor;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;

/**
 * Contains helper methods for GridFS based storage
 */
public final class GridFsHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(GridFsHelper.class);

	public static final String PATH_SEPARATOR = "/";

	public static final String SEMICOLON_SEPARATOR = ";";

	public static final String METADATA_PREFIX = "metadata.";

	private static final String FILES_SUFFIX = ".files";

	private static final String CHUNKS_SUFFIX = ".chunks";

	public static final String REPORTER_ATTRIBUTE_TYPE = "reporterModule";

	public static final String FILENAME_ATTRIBUTE = "filename";

	public static final String PATTERN_CANDIDATE = "patternCandidate";

	public static final String REBASE_SOURCE = "rebaseSource";

	private static final int MAX_DB_NAME_LENGTH = 64;

	private static final int MAX_COLLECTION_NAME_LENGTH = 120;

	private GridFsHelper() {
		// private util constructor
	}

	public static String inMetadata(String property) {
		return GridFsHelper.METADATA_PREFIX + property;
	}

	/**
	 * Gets distinct collection of sub-nodes based on provided NodeMetadata and search type. Firstly it
	 * searches all sub-nodes by NodeMetadata and then it gets distinct subset.
	 * 
	 * @param gfs gridfs connection object
	 * @param nodeMetadata key used to find matching collection
	 * @param artifactType
	 * @param searchType
	 * @return collection of distinct nodes
	 * 
	 * @see com.cognifide.aet.vs.gridfs.GridFsHelper#addQueryCriteria
	 * @see NodeMetadata
	 * @see ArtifactType
	 */
	static Collection distinct(GridFS gfs, NodeMetadata nodeMetadata, ArtifactType artifactType,
			String searchType) {
		Collection result;
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		builder = addQueryCriteria(builder, nodeMetadata, artifactType, METADATA_PREFIX);
		DBObject metadata = builder.get();
		DBCollection coll = getFilesCollection(gfs);
		String distinct;
		if ("filename".equals(searchType)) {
			distinct = searchType;
			result = coll.distinct(distinct, metadata);
		} else if (artifactType == ArtifactType.DATA) {
			distinct = inMetadata(searchType);
			result = CollectionUtils.union(coll.distinct(distinct, metadata),
					coll.distinct(inMetadata(REBASE_SOURCE + "." + searchType), metadata));
		} else {
			distinct = inMetadata(searchType);
			result = coll.distinct(distinct, metadata);
		}
		result.remove(null);
		return result;
	}

	/**
	 * A convenient way to build MongoDB criteria search object based on provided NodeMetadata
	 * 
	 * @param basicDBObjectBuilder
	 * @param nodeMetadata
	 * @param artifactType
	 * @param prefix
	 * @return MongoDB criteria object
	 * 
	 * @see com.cognifide.aet.communication.api.node.NodeMetadata
	 * @see com.cognifide.aet.communication.api.node.ArtifactType
	 */
	public static BasicDBObjectBuilder addQueryCriteria(BasicDBObjectBuilder basicDBObjectBuilder,
			NodeMetadata nodeMetadata, ArtifactType artifactType, String prefix) {

		String keyPrefix = StringUtils.defaultString(prefix);
		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		try {
			nodeMetadata.accept(visitor);
		} catch (AETException e) {
			LOGGER.error("Error while building query criteria: ", e);
		}

		Map<String, Object> queryParameters = visitor.getQueryParameters();

		if (artifactType == ArtifactType.DATA && nodeMetadata instanceof UrlNodeMetadata) {
			UrlNodeMetadata urlNodeMetadata = (UrlNodeMetadata) nodeMetadata;
			BasicDBObjectBuilder data = new BasicDBObjectBuilder();
			BasicDBObjectBuilder pattern = new BasicDBObjectBuilder();

			queryParameters.remove(UrlNodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME);
			queryParameters.remove(UrlNodeMetadata.CORRELATION_ID_ATTRIBUTE_NAME);

			data = addIfNotEmpty(data, keyPrefix + NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME, ArtifactType.DATA.toString());
			data = addIfNotEmpty(data, keyPrefix + UrlNodeMetadata.CORRELATION_ID_ATTRIBUTE_NAME,
					urlNodeMetadata.getCorrelationId());

			pattern = addIfNotEmpty(pattern, keyPrefix + NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME,
					ArtifactType.PATTERNS.toString());
			pattern = addIfNotEmpty(pattern, keyPrefix + REBASE_SOURCE + "." + UrlNodeMetadata.CORRELATION_ID_ATTRIBUTE_NAME,
					urlNodeMetadata.getCorrelationId());

			BasicDBList or = new BasicDBList();
			or.add(data.get());
			or.add(pattern.get());
			basicDBObjectBuilder.add("$or", or);
		}

		for (Map.Entry<String, Object> entry : queryParameters.entrySet()) {
			basicDBObjectBuilder.add(keyPrefix + entry.getKey(), entry.getValue());
		}

		return basicDBObjectBuilder;
	}

	/**
	 * Gets MongoDB .files collection that contains metadata of files stored in chunks on GridFS
	 * 
	 * @param gfs
	 * @return MongoDB collection
	 * 
	 * @see GridFS
	 */
	public static DBCollection getFilesCollection(GridFS gfs) {
		DBCollection collection = null;
		try {
			DB db = gfs.getDB();
			collection = db.getCollectionFromString(gfs.getBucketName() + FILES_SUFFIX);
		} catch (RuntimeException e) {
			LOGGER.error("Bucket not found", e);
		}
		return collection;
	}

	/**
	 * Adds query criteria if provided value is not null
	 * 
	 * @param builder MongoDB query builder
	 * @param field field that we would like to add criteria to
	 * @param value value of the criteria
	 * @return MongoDB query builder
	 */
	public static BasicDBObjectBuilder addIfNotEmpty(BasicDBObjectBuilder builder, String field, Object value) {
		if (value != null) {
			builder.add(field, value);
		}
		return builder;
	}

	/**
	 * Creates string representation of metadata
	 * 
	 * @param metadata MongoDB object that stores metadata
	 * @return string representation of metadata
	 */
	public static String patternMetadataToString(DBObject metadata) {
		StringBuilder sb = new StringBuilder();
		/* @formatter:off */
		sb.append(metadata.get(PatternMetadata.COMPANY_ATTRIBUTE_NAME))
				.append(metadata.get(PatternMetadata.PROJECT_ATTRIBUTE_NAME))
				.append(metadata.get(PatternMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME))
				.append(metadata.get(PatternMetadata.TEST_NAME_ATTRIBUTE_NAME))
				.append(metadata.get(PatternMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME))
				.append(metadata.get(PatternMetadata.ENVIRONMENT_ATTRIBUTE_NAME))
				.append(metadata.get(PatternMetadata.URL_NAME_ATTRIBUTE_NAME))
				.append(metadata.get(PatternMetadata.COLLECTOR_ATTRIBUTE_TYPE))
				.append(metadata.get(PatternMetadata.COLLECTOR_ATTRIBUTE_NAME));
		/* @formatter:on */
		return sb.toString();
	}

	/**
	 * Gets list of files collections for given database.
	 * 
	 * @param dbName the name of database
	 * @return set of collection names or empty set if database does not exsists.
	 */
	public static Set<String> getBucketList(MongoDBClient client, String dbName) {
		Set<String> result = new HashSet<String>();
		DB db = client.getDB(dbName);
		if (db != null) {
			Set<String> colls = db.getCollectionNames();

			Collection<String> filteredColls = Collections2.filter(colls, new Predicate<String>() {
				public boolean apply(String input) {
					return StringUtils.isNotBlank(input) && input.endsWith(GridFsHelper.CHUNKS_SUFFIX);
				}
			});

			for (String collName : filteredColls) {
				String bucketName = collName.substring(0, collName.indexOf(CHUNKS_SUFFIX));
				if (colls.contains(bucketName + FILES_SUFFIX) && !"null".equals(bucketName)) {
					result.add(bucketName);
				}
			}
		}
		return result;
	}

	/**
	 * Gets metadata properties of stored data.
	 * 
	 * @param nodeMetadata key by which we are performing search
	 * @param artifactType type of artifact
	 * @return MongoDB criteria object
	 * 
	 * @see NodeMetadata
	 * @see ArtifactType
	 */
	public static BasicDBObject getMetadata(NodeMetadata nodeMetadata, ArtifactType artifactType) {
		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		try {
			nodeMetadata.accept(visitor);
		} catch (AETException e) {
			LOGGER.error("Error while building query criteria for node {}: ", nodeMetadata, e);
		}

		Map<String, Object> queryParameters = visitor.getQueryParameters();
		queryParameters.put(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME, artifactType.toString());

		return new BasicDBObject(queryParameters);
	}

	/**
	 * Gets set of available artifact types.
	 * 
	 * @return set of artifact types
	 */
	public static Collection getArtifactTypes() {
		Set artifactTypes = Sets.newHashSet();
		artifactTypes.add(ArtifactType.DATA.toString().toLowerCase());
		artifactTypes.add(ArtifactType.PATTERNS.toString().toLowerCase());
		artifactTypes.add(ArtifactType.RESULTS.toString().toLowerCase());
		return artifactTypes;
	}

	/**
	 * Gets full URL path to given artifact
	 * 
	 * @param serverUrl REST service entry point
	 * @param nodeMetadata key that we look for
	 * @param fileName name of file
	 * @return URL to file
	 * @throws VersionStorageException
	 * 
	 * @see NodeMetadata
	 * @see ArtifactType
	 */
	public static String getArtifactPath(String serverUrl, NodeMetadata nodeMetadata, String fileName)
			throws VersionStorageException {
		StringBuilder nodePath = new StringBuilder();
		try {
			appendNotBlank(nodePath, serverUrl);
			MetadataArtifactPathVisitor visitor = new MetadataArtifactPathVisitor(PATH_SEPARATOR);
			nodeMetadata.accept(visitor);
			nodePath.append(visitor.getPath());
			appendNotBlank(nodePath, "", fileName);
		} catch (AETException e) {
			throw new VersionStorageException("Exception while creating path to artifact!", e);
		}
		return nodePath.toString();
	}

	/**
	 * Gets GridFS connection object for provided comapny name and project name. It does not auto-create dbName
	 * and collection.
	 *
	 * @param companyName name of Company
	 * @param projectName name of Project
	 * @return GridFS connection object
	 * @see com.cognifide.aet.vs.gridfs.GridFsHelper#getGridFS(MongoDBClient, String, String, Boolean)
	 */
	public static GridFS getGridFS(MongoDBClient client, String companyName, String projectName) {
		return getGridFS(client, companyName, projectName, false);
	}

	/**
	 * Gets GridFS connection object for provided db name and collection name. It creates database and
	 * collection if they don't exists and the autoCreate flag is set to true.
	 *
	 * @param companyName name of company
	 * @param projectName name of project
	 * @param autoCreate  defines if database and collection auto-creation should occur
	 * @return GridFs connection
	 */
	public static GridFS getGridFS(MongoDBClient client, String companyName, String projectName, Boolean autoCreate) {
		GridFS gridFS = null;
		String dbName = getDbName(companyName, projectName);
		String bucketName = getBucketName(projectName);
		boolean isNewCollectionFlag = isNewCollection(client, dbName, autoCreate, bucketName);
		if (autoCreate || hasBucket(client, dbName, bucketName)) {
			gridFS = new GridFS(client.getDB(dbName, autoCreate), bucketName);
		}
		if (isNewCollectionFlag) {
			createIndexes(gridFS);
		}
		return gridFS;
	}

	/**
	 * Return dataBase name with applied db naming rules, based on project and company names
	 *
	 * @param companyName
	 * @param projectName
	 * @return
	 */
	private static String getDbName(String companyName, String projectName) {
		String result = companyName + "_" + projectName;
		return StringUtils.substring(result,0,MAX_DB_NAME_LENGTH);
	}

	/**
	 * Return bucket name with applied collection naming rules, based on project name
	 *
	 * @param projectName
	 * @return
	 */
	private static String getBucketName(String projectName) {
		String result = projectName;
		return StringUtils.substring(result,0,MAX_COLLECTION_NAME_LENGTH);
	}

	private static boolean isNewCollection(MongoDBClient client, String dbName, Boolean autoCreate, String bucketName) {
		return autoCreate && !hasBucket(client, dbName, bucketName);
	}

	private static boolean hasBucket(MongoDBClient client, String dbName, String bucketName) {
		return getBucketList(client, dbName).contains(bucketName);
	}

	private static void createIndexes(GridFS gridFS) {
		DBObject index = BasicDBObjectBuilder.start(inMetadata(UrlNodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME), 1)
				.add(inMetadata(UrlNodeMetadata.URL_NAME_ATTRIBUTE_NAME), 1).get();
		DB db = gridFS.getDB();
		String collName = gridFS.getBucketName() + FILES_SUFFIX;
		DBCollection coll = db.getCollection(collName);
		coll.createIndex(index);
		LOGGER.info("Index created; Database: " + db.getName() + "; Collection: " + collName);
	}

	private static void appendNotBlank(StringBuilder pathBuilder, String pathSeparator, String value) {
		if (StringUtils.isNotBlank(value)) {
			pathBuilder.append(value).append(pathSeparator);
		}
	}

	private static void appendNotBlank(StringBuilder pathBuilder, String value) {
		appendNotBlank(pathBuilder, PATH_SEPARATOR, value);
	}
}
