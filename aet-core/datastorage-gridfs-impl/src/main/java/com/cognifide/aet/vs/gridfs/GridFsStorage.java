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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.communication.api.RebaseOperationStatus;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.vs.VersionStorageException;
import com.cognifide.aet.vs.gridfs.version.BasicArtifactsVersionManager;
import com.cognifide.aet.vs.gridfs.version.PatternsVersionManager;
import com.cognifide.aet.vs.gridfs.version.ReportsArtifactsVersionManager;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * Handles all operations that can be performed on storage, like reading and writing data.
 */
@Service(GridFsStorage.class)
@Component(label = "AET GridFs Storage", description = "AET GridFs Storage", immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
public class GridFsStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(GridFsStorage.class);

	private static final String SERVER_URL = "url";

	private static final String LOGGER_MODULE_NAME = "VS";

	private static final Set<String> LOCK_SET = new HashSet<>();

	private static final Set<String> ARTIFACTS_COMPARE_KEY_SET = Sets.newHashSet(
			CollectMetadata.COMPANY_ATTRIBUTE_NAME, CollectMetadata.PROJECT_ATTRIBUTE_NAME,
			CollectMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME, CollectMetadata.ENVIRONMENT_ATTRIBUTE_NAME,
			CollectMetadata.TEST_NAME_ATTRIBUTE_NAME, CollectMetadata.URL_NAME_ATTRIBUTE_NAME,
			CollectMetadata.COLLECTOR_ATTRIBUTE_TYPE, CollectMetadata.COLLECTOR_ATTRIBUTE_NAME);

	private static final String DEFAULT_SERVER_URL = "http://localhost:8181/cxf/aet";

	@Property(name = SERVER_URL, label = "Url", description = "Url of the REST server", value = DEFAULT_SERVER_URL)
	private String serverUrl;

	@Reference
	private MongoDBClient client;

	@Activate
	public void activate(Map properties) {
		this.serverUrl = PropertiesUtil.toString(properties.get(SERVER_URL), DEFAULT_SERVER_URL);
	}

	/**
	 * Gets collection of versions
	 *
	 * @param nodeMetadata key
	 * @return collection of versions
	 * @see NodeMetadata
	 */
	public Collection getPatternModuleVersions(NodeMetadata nodeMetadata) {
		return getSubNodesNames(ArtifactType.PATTERNS, nodeMetadata, NodeMetadata.VERSION_ATTRIBUTE_NAME);
	}

	/**
	 * Gets collection of existing project names
	 *
	 * @param nodeMetadata key
	 * @return collection of project names
	 * @see NodeMetadata
	 */
	public Collection<String> getProjects(NodeMetadata nodeMetadata) {
		Collection<String> projects = new LinkedList<String>();
		
		String companyName = nodeMetadata.getCompany();
		for (String dbName : client.getAetsDBNames()) {
			if (StringUtils.startsWith(dbName, companyName)){
				projects.add(StringUtils.substringAfter(dbName, "_"));
			}
		}
		
		return projects;
	}

	/**
	 * Gets most recent version number of pattern
	 *
	 * @param nodeMetadata key
	 * @param gfs GridFs connection
	 * @return version number
	 * @see GridFS
	 * @see NodeMetadata
	 */
	public Long getCurrentPatternVersion(NodeMetadata nodeMetadata, GridFS gfs) {
		Long version = 1L;
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		builder = GridFsHelper.addQueryCriteria(builder, nodeMetadata, ArtifactType.PATTERNS,
				GridFsHelper.METADATA_PREFIX);

		builder.add(GridFsHelper.inMetadata(PatternMetadata.CURRENT_PATTERN), true);

		DBObject patterNodeKeyMetadata = builder.get();

		GridFSDBFile patternArtifact = gfs.findOne(patterNodeKeyMetadata);
		if (patternArtifact != null) {
			version = Long.parseLong(
					patternArtifact.getMetaData().get(NodeMetadata.VERSION_ATTRIBUTE_NAME).toString());
		}

		return version;
	}

	/**
	 * @param nodeMetadata
	 * @return test suite run correlationId or null if no runs of this suite configuration before. When
	 * searching for correlationId following criteria are taken into account: company, project, testSuiteName,
	 * environment, domain (even if not set), version.
	 */
	public String getCorrelationId(NodeMetadata nodeMetadata) throws AETException {
		String result = null;
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		if (gfs != null) {

			NodeMetadataQueryBuilder queryBuilder = new NodeMetadataQueryBuilder(nodeMetadata);
			Set<String> keysToRetain = Sets.newHashSet();
			keysToRetain.add(UrlNodeMetadata.COMPANY_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.PROJECT_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.DOMAIN_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.VERSION_ATTRIBUTE_NAME);
			queryBuilder.retainAllKeys(keysToRetain);

			BasicDBObject notEquals = new BasicDBObject("$ne", "PATTERNS");
			queryBuilder.add(UrlNodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME, notEquals);

			queryBuilder.setPrefix(GridFsHelper.METADATA_PREFIX);
			GridFSDBFile file = gfs.findOne(queryBuilder.get());
			if (file != null) {
				result = file.getMetaData().get(UrlNodeMetadata.CORRELATION_ID_ATTRIBUTE_NAME).toString();
			}
		}
		return result;
	}

	/**
	 * @param nodeMetadata
	 * @return last (max) test suite run version or 0 if no runs of this suite configuration before. When
	 * searching for version following criteria are taken into account: company, project, testSuiteName,
	 * environment, domain (even if not set).
	 */
	public Long getMaxTestSuiteRunVersion(NodeMetadata nodeMetadata) throws AETException {
		BasicDBObject notEquals = new BasicDBObject("$ne", "PATTERNS");
		return getMaxVersion(nodeMetadata, notEquals);
	}

	/**
	 * @param nodeMetadata
	 * @return correlationId of last (with greatest version) generated report or null if no reports generated
	 * for this suite configuration before. When searching for greatest version following criteria are taken
	 * into account: company, project, testSuiteName, environment, domain (even if not set).
	 */
	public String getLastReportCorrelationId(NodeMetadata nodeMetadata) throws AETException {
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		String correlationId = null;
		if (gfs != null) {
			NodeMetadataQueryBuilder queryBuilder = new NodeMetadataQueryBuilder(nodeMetadata);
			Set<String> keysToRetain = Sets.newHashSet();
			keysToRetain.add(UrlNodeMetadata.COMPANY_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.PROJECT_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.DOMAIN_ATTRIBUTE_NAME);
			queryBuilder.retainAllKeys(keysToRetain);
			queryBuilder.add(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME, ArtifactType.REPORTS.toString());
			queryBuilder.setPrefix(GridFsHelper.METADATA_PREFIX);

			List<GridFSDBFile> result = gfs.find(queryBuilder.get());
			correlationId = findLastCorrelationId(result);
		}
		return correlationId;
	}

	private String findLastCorrelationId(List<GridFSDBFile> result) {
		Long maxVersion = 0L;
		String correlationId = null;
		for (GridFSDBFile r : result) {
			DBObject metaData = r.getMetaData();
			Object versionProperty = metaData.get(NodeMetadata.VERSION_ATTRIBUTE_NAME);
			Object correlationIdProperty = metaData.get(UrlNodeMetadata.CORRELATION_ID_ATTRIBUTE_NAME);
			if (versionProperty != null && correlationIdProperty != null) {
				Long version = Long.parseLong(versionProperty.toString());
				if (version > maxVersion) {
					correlationId = correlationIdProperty.toString();
					maxVersion = version;
				}
			}
		}
		return correlationId;
	}

	private Long getMaxVersion(NodeMetadata nodeMetadata, Object artifactTypeVal) throws AETException {
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		Long maxVersion = 0L;
		if (gfs != null) {
			NodeMetadataQueryBuilder queryBuilder = new NodeMetadataQueryBuilder(nodeMetadata);
			Set<String> keysToRetain = Sets.newHashSet();
			keysToRetain.add(UrlNodeMetadata.COMPANY_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.PROJECT_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME);
			keysToRetain.add(UrlNodeMetadata.DOMAIN_ATTRIBUTE_NAME);
			queryBuilder.retainAllKeys(keysToRetain);
			queryBuilder.add(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME, artifactTypeVal);
			queryBuilder.setPrefix(GridFsHelper.METADATA_PREFIX);

			List<GridFSDBFile> result = gfs.find(queryBuilder.get());
			for (GridFSDBFile r : result) {
				DBObject metaData = r.getMetaData();
				Object versionProperty = metaData.get(NodeMetadata.VERSION_ATTRIBUTE_NAME);
				if (versionProperty != null) {
					Long version = Long.parseLong(versionProperty.toString());
					maxVersion = Math.max(version, maxVersion);
				}
			}
		}
		return maxVersion;
	}

	/**
	 * Removes all pattern nodes from database which fulfill condition:
	 * <ul>
	 * <li>version is less or equal to difference between current max version and keepNVersions,</li>
	 * <li>artifact was created before keepAllAfter date.</li>
	 * </ul>
	 *
	 * @param nodeMetadata contains basic metadata - company and project name
	 * @param keepAllAfter defines threshold above which no data will be deleted
	 * @param keepNVersions defines number of versions that we wish to keep from wiping out
	 * @param dryRun defines if we wish to perform dry run or finally
	 * @return operation status with message
	 * @see NodeMetadata
	 */
	public OperationStatus removePatterns(NodeMetadata nodeMetadata, Date keepAllAfter, Integer keepNVersions,
			boolean dryRun) {
		PatternsVersionManager versionManager = new PatternsVersionManager(
				GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject()),
				GridFsHelper.METADATA_PREFIX);
		return versionManager.removeArtifacts(dryRun, keepAllAfter, keepNVersions);
	}

	public void setPatternCandidate(NodeMetadata nodeMetadata) {
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		builder = GridFsHelper.addQueryCriteria(builder, nodeMetadata, ArtifactType.DATA,
				GridFsHelper.METADATA_PREFIX);
		DBObject query = builder.get();
		for (GridFSDBFile dataArtifact : gfs.find(query)) {
			dataArtifact.getMetaData().put(GridFsHelper.PATTERN_CANDIDATE, true);
			dataArtifact.save();
		}
	}

	/**
	 * Performs rebase operation that sets node as a new pattern
	 *
	 * @param nodeMetadata key
	 * @return operation status with message
	 * @throws VersionStorageException
	 * @see NodeMetadata
	 */
	public RebaseOperationStatus rebasePatterns(NodeMetadata nodeMetadata) throws VersionStorageException {

		RebaseOperationStatus operationStatus = null;

		if (isRebaseLocked(nodeMetadata)) {
			LOGGER.warn("Pattern for {} has been already scheduled to be rebased.", nodeMetadata);
			operationStatus = new RebaseOperationStatus(false, "Test suite is being rebased by another user",
					true);
		} else {
			String lockName = getLock(nodeMetadata);
			LOCK_SET.add(lockName);
			LOGGER.info("Rebase for {} started. Locking: {}.", nodeMetadata, lockName);
			try {
				GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(),
						nodeMetadata.getProject());
				List<GridFSDBFile> patternArtifacts = findCurrentArtifacts(gfs,
						PatternMetadata.fromNodeMetadata(nodeMetadata), ArtifactType.PATTERNS,
						GridFsHelper.METADATA_PREFIX);
				List<GridFSDBFile> dataArtifacts = findCurrentArtifacts(gfs, nodeMetadata, ArtifactType.DATA,
						GridFsHelper.METADATA_PREFIX);

				if (!patternArtifacts.isEmpty() && !dataArtifacts.isEmpty()) {
					operationStatus = matchAndRebase(patternArtifacts, dataArtifacts);
				} else {
					operationStatus = new RebaseOperationStatus(false, "Already rebased", true);
				}
			} finally {
				LOGGER.info("Rebase for {} finished with status {}. Unlocking: {}.", nodeMetadata,
						operationStatus, lockName);
				LOCK_SET.remove(lockName);
			}
		}

		return operationStatus;

	}

	private String getLock(NodeMetadata nodeMetadata) {
		return nodeMetadata.getCompany() + GridFsHelper.SEMICOLON_SEPARATOR + nodeMetadata.getProject()
				+ GridFsHelper.SEMICOLON_SEPARATOR + nodeMetadata.getTestSuiteName();
	}

	private boolean isRebaseLocked(NodeMetadata nodeMetadata) {
		return LOCK_SET.contains(getLock(nodeMetadata));
	}

	private List<GridFSDBFile> findCurrentArtifacts(GridFS gfs, NodeMetadata nodeMetadata,
			ArtifactType artifactType, String prefix) {

		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		builder = GridFsHelper.addQueryCriteria(builder, nodeMetadata, artifactType, prefix);
		if (artifactType == ArtifactType.PATTERNS) {
			builder.add(GridFsHelper.inMetadata(PatternMetadata.CURRENT_PATTERN), true);
		}
		return gfs.find(builder.get());
	}

	private RebaseOperationStatus matchAndRebase(List<GridFSDBFile> patternArtifacts,
			List<GridFSDBFile> dataArtifacts) throws VersionStorageException {

		Map<String, GridFSDBFile> patternMap = prepareCompareMap(patternArtifacts);
		Map<String, GridFSDBFile> dataMap = prepareCompareMap(dataArtifacts);

		RebaseOperationStatus operationStatus;
		try {
			int dataArtifactCounter = countDataArtifacts(patternMap, dataMap);
			if (dataArtifactCounter > 0) {
				operationStatus = new RebaseOperationStatus(true, "Success: artifacts rebased: "
						+ dataArtifactCounter + ", all artifacts: " + patternArtifacts.size(), true);
			} else {
				operationStatus = new RebaseOperationStatus(false, "Already rebased", true);
			}
		} catch (Exception e) {
			throw new VersionStorageException(e.getMessage(), e);
		}
		return operationStatus;
	}

	private int countDataArtifacts(Map<String, GridFSDBFile> patternMap, Map<String, GridFSDBFile> dataMap) {
		int dataArtifactCounter = 0;
		for (Map.Entry<String, GridFSDBFile> patternArtifact : patternMap.entrySet()) {
			GridFSDBFile artifact = dataMap.get(patternArtifact.getKey());
			if (artifact != null && artifactDetected(artifact.getMetaData().toMap())) {
				rebase(artifact, patternArtifact.getValue());
				dataArtifactCounter++;
			}
		}
		return dataArtifactCounter;
	}

	private boolean artifactDetected(final Map artifactMetaData) {
		ArtifactType artifactType = ArtifactType
				.valueOf(artifactMetaData.get(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME).toString());
		Boolean patternCandidate = (Boolean) artifactMetaData.get(GridFsHelper.PATTERN_CANDIDATE);
		return artifactType != ArtifactType.PATTERNS && BooleanUtils.isTrue(patternCandidate);
	}

	private Map<String, GridFSDBFile> prepareCompareMap(List<GridFSDBFile> artifactsList) {
		Map<String, GridFSDBFile> compareMap = new HashMap<>();

		for (GridFSDBFile artifact : artifactsList) {
			Map compareKey = Maps.newTreeMap();
			compareKey.putAll(Maps.filterKeys(artifact.getMetaData().toMap(),
					Predicates.in(ARTIFACTS_COMPARE_KEY_SET)));
			compareKey.put(GridFsHelper.FILENAME_ATTRIBUTE, artifact.getFilename());

			compareMap.put(compareKey.toString(), artifact);
		}
		return compareMap;

	}

	private void rebase(GridFSDBFile dataArtifact, GridFSDBFile patternArtifact) {

		Map patternArtifactMetadata = patternArtifact.getMetaData().toMap();
		Map dataArtifactMetadata = dataArtifact.getMetaData().toMap();

		Long currentVersion = (Long) patternArtifactMetadata.get(PatternMetadata.VERSION_ATTRIBUTE_NAME);
		currentVersion++;
		LOGGER.info("Rebasing pattern for {} to version {}.", patternArtifactMetadata, currentVersion);

		dataArtifact.setMetaData(new BasicDBObject(patternArtifactMetadata));
		dataArtifact.getMetaData().put(PatternMetadata.VERSION_ATTRIBUTE_NAME, currentVersion);
		dataArtifact.getMetaData().put(GridFsHelper.REBASE_SOURCE, dataArtifactMetadata);
		dataArtifact.save();

		patternArtifact.getMetaData().put(PatternMetadata.CURRENT_PATTERN, false);
		patternArtifact.save();
	}

	/**
	 * Returns REST endpoint URL
	 *
	 * @return REST endpoint URL
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Gets sub nodes of node that is found with provided criteria
	 *
	 * @param artifactType type
	 * @param nodeMetadata key
	 * @param searchType field by which that we wish to perform distinct operation on
	 * @return collection of nodes
	 * @see ArtifactType
	 * @see NodeMetadata
	 */
	public Collection getSubNodesNames(ArtifactType artifactType, NodeMetadata nodeMetadata,
			String searchType) {
		Collection result = null;
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		if (gfs != null) {
			result = GridFsHelper.distinct(gfs, nodeMetadata, artifactType, searchType);
		}
		return result;
	}

	/**
	 * Gets collection of artifact types
	 *
	 * @return collection of artifact types
	 */
	public Collection getArtifactTypes() {
		return GridFsHelper.getArtifactTypes();
	}

	/**
	 * Get collection of companies in AET system
	 *
	 * @return collection of companies names
	 */
	public Collection<String> getCompanies() {
		return client.getCompanies();
	}

	/**
	 * Gets collection of files that match provided criteria
	 *
	 * @param artifactType type
	 * @param nodeMetadata key
	 * @return collection of GridFSDBFiles
	 * @see NodeMetadata
	 * @see ArtifactType
	 */
	public Collection getArtifacts(ArtifactType artifactType, NodeMetadata nodeMetadata) {
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());

		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		builder = GridFsHelper.addQueryCriteria(builder, nodeMetadata, artifactType,
				GridFsHelper.METADATA_PREFIX);
		DBObject query = builder.get();

		if (artifactType == ArtifactType.PATTERNS) {
			query.put(GridFsHelper.inMetadata(PatternMetadata.CURRENT_PATTERN), true);
		}
		List<GridFSDBFile> fileList = gfs.find(query);
		List<String> result = new ArrayList<>();
		for (GridFSDBFile file : fileList) {
			result.add(file.getFilename());
		}
		return result;
	}

	/**
	 * Removes named data artifact of certain atrifact type
	 *
	 * * @param nodeMetadata key*
	 * 
	 * @param fileName
	 * @param artifactType
	 * @param fileName
	 */
	public boolean removeNodeArtifact(NodeMetadata nodeMetadata, ArtifactType artifactType, String fileName) {
		boolean isSuccess = false;
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		builder = GridFsHelper.addQueryCriteria(builder, nodeMetadata, artifactType,
				GridFsHelper.METADATA_PREFIX);
		builder = GridFsHelper.addIfNotEmpty(builder, GridFsHelper.FILENAME_ATTRIBUTE, fileName);
		DBObject query = builder.get();
		GridFSDBFile file = gfs.findOne(query);
		if (file != null) {
			gfs.remove(new ObjectId(file.getId().toString()));
			isSuccess = true;
		}
		return isSuccess;
	}

	/**
	 * Gets test case urlNames for defined key
	 *
	 * @param nodeMetadata
	 * @return collection of urls
	 * @see NodeMetadata
	 */
	public Collection getTestCaseUrls(NodeMetadata nodeMetadata) {
		Set<String> result = new HashSet<>();
		Collection subNodesNames = getSubNodesNames(null, nodeMetadata, "urlName");
		if (subNodesNames != null) {
			Iterator<String> it = subNodesNames.iterator();
			Escaper escaper = UrlEscapers.urlFormParameterEscaper();
			while (it.hasNext()) {
				result.add(escaper.escape(it.next()));
			}
		}
		return result;
	}

	/**
	 * Restores pattern version to provided version number
	 *
	 * @param nodeMetadata key
	 * @param versionToRestore version number
	 * @return operation status with message
	 * @see NodeMetadata
	 * @see OperationStatus
	 */
	public OperationStatus restorePatterns(NodeMetadata nodeMetadata, Long versionToRestore) {
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		long highestVersion = getCurrentPatternVersion(nodeMetadata, gfs);
		if (highestVersion <= versionToRestore) {
			return new OperationStatus(false,
					"Version To Restore can't be equal or grater that highest version");
		}
		List<GridFSDBFile> artifactList = getPatternArtifactsForVersion(nodeMetadata, versionToRestore);
		if (artifactList.isEmpty()) {
			return new OperationStatus(false, "Can not find patterns with given version to Restore");
		}
		highestVersion++;
		for (GridFSDBFile artifact : artifactList) {
			artifact.getMetaData().put(PatternMetadata.VERSION_ATTRIBUTE_NAME, highestVersion);
			artifact.save();
		}
		return new OperationStatus(true, "Restored pattern with " + artifactList.size() + " artifacts.");
	}

	/**
	 * Saves provided input stream under given name and node key
	 *
	 * @param artifactType type
	 * @param nodeMetadata key
	 * @param fileName target filename
	 * @param fileToSave binary data
	 * @return path to saved data
	 * @see NodeMetadata
	 */
	public String saveNode(ArtifactType artifactType, NodeMetadata nodeMetadata, String fileName,
			InputStream fileToSave) {
		String artifactPath;
		ExecutionTimer timer = ExecutionTimer.createAndRun(LOGGER_MODULE_NAME);
		LOGGER.debug("Saving node with key: {} and file name: {}", nodeMetadata, fileName);
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject(),
				true);
		GridFSInputFile fileIn = gfs.createFile(fileToSave, fileName, true);
		BasicDBObject metadata = GridFsHelper.getMetadata(nodeMetadata, artifactType);
		if (artifactType == ArtifactType.PATTERNS) {
			metadata.append(NodeMetadata.VERSION_ATTRIBUTE_NAME, 1L);
			metadata.append(PatternMetadata.CURRENT_PATTERN, true);
		}
		fileIn.setMetaData(metadata);
		fileIn.save();

		timer.finishAndLog("saveResult");
		try {
			artifactPath = GridFsHelper.getArtifactPath(serverUrl, nodeMetadata, fileName);
		} catch (VersionStorageException e) {
			LOGGER.error(e.getMessage(), e);
			artifactPath = "";
		}
		return artifactPath;
	}

	private GridFSDBFile getGridFile(NodeMetadata nodeMetadata, String artifactName,
			ArtifactType artifactType) {
		GridFSDBFile result = null;
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		if (gfs != null) {
			BasicDBObjectBuilder builder = BasicDBObjectBuilder.start("filename", artifactName);

			builder = GridFsHelper.addQueryCriteria(builder, nodeMetadata, artifactType,
					GridFsHelper.METADATA_PREFIX);
			DBObject query = builder.get();
			if (artifactType == ArtifactType.PATTERNS) {
				query.put(GridFsHelper.inMetadata(PatternMetadata.CURRENT_PATTERN), true);
			}
			result = gfs.findOne(query);
			if (result == null && artifactType != ArtifactType.PATTERNS) {
				LOGGER.warn("Resource not found " + artifactType.toString() + query.toString() + " -  "
						+ artifactName);
			}
		}
		return result;
	}

	/**
	 * Gets input stream of file stored in GridFS
	 *
	 * @param nodeMetadata key
	 * @param artifactName source file name
	 * @param artifactType type
	 * @return input stream
	 * @see NodeMetadata
	 * @see ArtifactType
	 */
	public InputStream getStream(NodeMetadata nodeMetadata, String artifactName, ArtifactType artifactType) {
		InputStream result = null;
		GridFSDBFile gridFile = getGridFile(nodeMetadata, artifactName, artifactType);
		if (gridFile != null) {
			result = gridFile.getInputStream();
		}
		return result;
	}

	/**
	 * Gets MD5 of file stored in GridFS
	 *
	 * @param nodeMetadata key
	 * @param artifactName source file name
	 * @param artifactType type
	 * @return md5
	 * @see NodeMetadata
	 * @see ArtifactType
	 */
	public String getDataMD5(NodeMetadata nodeMetadata, String artifactName, ArtifactType artifactType) {
		String result = null;
		GridFSDBFile gridFile = getGridFile(nodeMetadata, artifactName, artifactType);
		if (gridFile != null) {
			result = gridFile.getMD5();
		}
		return result;
	}

	private List<GridFSDBFile> getPatternArtifactsForVersion(NodeMetadata nodeMetadata, long version) {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		builder = GridFsHelper.addQueryCriteria(builder, nodeMetadata, ArtifactType.PATTERNS,
				GridFsHelper.METADATA_PREFIX);
		builder.add(GridFsHelper.inMetadata(PatternMetadata.VERSION_ATTRIBUTE_NAME), version);
		DBObject patterNodeKeyMetadata = builder.get();
		GridFS gfs = GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject());
		return gfs.find(patterNodeKeyMetadata);
	}

	/**
	 * Removes all non reports nodes from database which fulfill condition:
	 * <ul>
	 * <li>version is less or equal to difference between current max version and keepNVersions,</li>
	 * <li>artifact was created before keepAllAfter date.</li>
	 * </ul>
	 *
	 * @param nodeMetadata contains basic metadata - company and project name
	 * @param keepAllAfter defines threshold above which no data will be deleted
	 * @param keepNVersions defines number of versions that we wish to keep from wiping out
	 * @param dryRun defines if we wish to perform dry run or finally
	 * @return operation status with message
	 */
	public OperationStatus removeReports(NodeMetadata nodeMetadata, Date keepAllAfter, Integer keepNVersions,
			Boolean dryRun) {
		ReportsArtifactsVersionManager versionManager = new ReportsArtifactsVersionManager(
				GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject()),
				GridFsHelper.METADATA_PREFIX);
		return versionManager.removeArtifacts(dryRun, keepAllAfter, keepNVersions);
	}

	/**
	 * Removes all basic (DATA, RESULTS) nodes from database which fulfill condition:
	 * <ul>
	 * <li>version is less or equal to difference between current max version and keepNVersions,</li>
	 * <li>artifact was created before keepAllAfter date.</li>
	 * </ul>
	 *
	 * @param nodeMetadata contains basic metadata - company and project name
	 * @param keepAllAfter defines threshold above which no data will be deleted
	 * @param keepNVersions defines number of versions that we wish to keep from wiping out
	 * @param dryRun defines if we wish to perform dry run or finally
	 * @return operation status with message
	 */
	public OperationStatus removeBasicArtifacts(NodeMetadata nodeMetadata, Date keepAllAfter,
			Integer keepNVersions, Set<ArtifactType> artifactTypes, Boolean dryRun) {
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(
				GridFsHelper.getGridFS(client, nodeMetadata.getCompany(), nodeMetadata.getProject()),
				GridFsHelper.METADATA_PREFIX, artifactTypes);
		return versionManager.removeArtifacts(dryRun, keepAllAfter, keepNVersions);
	}

}
