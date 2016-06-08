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
package com.cognifide.aet.vs.gridfs.version;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.communication.api.OperationStatus;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * ArtifactsVersionManager - abstract artifacts version manager. Enables managing artifacts versions in
 * MongoDB via GridFs storage.
 * 
 * @Author: Maciej Laskowski
 * @Date: 25.02.15
 */
abstract class ArtifactsVersionManager<T extends ArtifactVersionKey<N>, N extends NodeMetadata> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactsVersionManager.class);

	private static final String UPLOAD_DATE_PARAMETER = "uploadDate";

	protected final String metadataPrefix;

	protected final GridFS gridFS;

	protected ArtifactsVersionManager(GridFS gridFS, String metadataPrefix) {
		this.gridFS = gridFS;
		this.metadataPrefix = metadataPrefix;
	}

	/**
	 * @param dryRun - flag which says if remove operation should be performed. When set to true, operation
	 * will be logged only.
	 * @param keepAllAfter - date after which all artifacts will remain
	 * @param keepNVersions - version over which all artifacts will remain
	 */
	public OperationStatus removeArtifacts(boolean dryRun, Date keepAllAfter, Integer keepNVersions) {
		OperationStatus operationStatus;
		LOGGER.info(
				"Start removing artifacts with parameters: [keepAllAfter: {}], [keepNVersions: {}], [dryRun: {}]",
				keepAllAfter, keepNVersions, dryRun);
		ExecutionTimer timer = ExecutionTimer.createAndRun("CLEANER");
		long totalRecordsRemoved = 0L;
		List<String> validationErrors = validateParameters(keepAllAfter, keepNVersions);
		if (validationErrors.isEmpty()) {
			Map<T, Long> artifactMaxVersions = getMaxVersionArtifacts(keepNVersions);
			for (Map.Entry<T, Long> entry : artifactMaxVersions.entrySet()) {
				T versionMetadata = entry.getKey();
				Long maxVersion = entry.getValue();
				LOGGER.info("All artifacts for {} older than version {} will be removed (dryRun: {}).",
						versionMetadata, maxVersion, dryRun);
				BasicDBObjectBuilder queryBuilder = getQueryBuilderWithDefaultParameters();
				addVersionMetadataQueryParameters(versionMetadata, queryBuilder);
				addVersionsLessOrEqualQueryParameter(keepNVersions, maxVersion, queryBuilder);
				addAllBeforeQueryParameter(keepAllAfter, queryBuilder);

				DBObject query = queryBuilder.get();
				LOGGER.debug("Find Nodes To Remove Query: {}", query.toString());
				totalRecordsRemoved += removeNodes(dryRun, versionMetadata, query);
			}
			operationStatus = new OperationStatus(true, String.format("%d nodes %s!", totalRecordsRemoved,
					dryRun ? "found" : "removed"));
		} else {
			operationStatus = new OperationStatus(false, StringUtils.join(validationErrors, ", "));
		}

		timer.finish();
		LOGGER.info("Removing artifacts finished after {} ms ({}). Removed totally {} nodes.",
				timer.getExecutionTimeInMillis(), timer.getExecutionTimeInMMSS(), totalRecordsRemoved);
		return operationStatus;
	}

	protected String inMetadata(String property) {
		return metadataPrefix + property;
	}

	protected List<String> validateParameters(Date keepAllAfter, Integer keepNVersions) {
		List<String> validationErrors = new LinkedList<>();
		if (keepNVersions == null && keepAllAfter == null) {
			validationErrors.add("At least on parameter: keepNVersions or keepAllAfter must be defined!");
		}
		if (keepNVersions != null && keepNVersions < 1) {
			validationErrors.add(String.format("keepNVersions (%d) must be greater than 0!", keepNVersions));
		}
		return validationErrors;
	}

	protected Map<T, Long> getMaxVersionArtifacts(Integer keepNVersions) {
		BasicDBObjectBuilder queryBuilder = getQueryBuilderWithDefaultParameters();
		addVersionsGreaterQueryParameter(keepNVersions, queryBuilder);
		DBObject query = queryBuilder.get();
		LOGGER.debug("Max Version Query: {}", query.toString());
		List<GridFSDBFile> gridFSDBFiles = gridFS.find(query);

		Map<T, Long> artifactMaxVersions = new HashMap<>();
		for (GridFSDBFile gridFSDBFile : gridFSDBFiles) {
			DBObject metaDataObject = gridFSDBFile.getMetaData();
			T artifactVersionKey = keyFromDBObject(metaDataObject);
			Long maxVersion = artifactMaxVersions.get(artifactVersionKey);
			Long version = (Long) metaDataObject.get(NodeMetadata.VERSION_ATTRIBUTE_NAME);
			if (ObjectUtils.compare(maxVersion, version) < 0) {
				artifactMaxVersions.put(artifactVersionKey, version);
			}
		}
		return artifactMaxVersions;
	}

	protected int removeNodes(boolean dryRun, T versionMetadata, DBObject query) {
		List<GridFSDBFile> gridFSDBFiles = gridFS.find(query);
		int removedNodes = gridFSDBFiles.size();
		LOGGER.info("{} nodes will be removed.", removedNodes);
		if (!dryRun) {
			if (removedNodes > 0) {
				gridFS.remove(query);
				LOGGER.info("{} nodes removed for {}", removedNodes, versionMetadata);
			} else {
				LOGGER.info("No nodes removed for {}", versionMetadata);
			}
		}
		return removedNodes;
	}

	private void addVersionMetadataQueryParameters(T versionMetadata, BasicDBObjectBuilder queryBuilder) {
		for (Map.Entry<String, Object> paramEntry : versionMetadata.toMap().entrySet()) {
			queryBuilder.add(inMetadata(paramEntry.getKey()), paramEntry.getValue());
		}
	}

	private void addAllBeforeQueryParameter(Date keepAllAfter, BasicDBObjectBuilder queryBuilder) {
		addUploadDateQueryParameter("$lt", keepAllAfter, queryBuilder);
	}

	private void addUploadDateQueryParameter(String operand, Date uploadDate,
			BasicDBObjectBuilder queryBuilder) {
		if (uploadDate != null) {
			DBObject dateEquation = new BasicDBObject(operand, uploadDate);
			queryBuilder.add(UPLOAD_DATE_PARAMETER, dateEquation);
		}
	}

	private void addVersionsLessOrEqualQueryParameter(Integer keepNVersions, Long maxVersion,
			BasicDBObjectBuilder queryBuilder) {
		addVersionsQueryParameter("$lte", maxVersion - keepNVersions, queryBuilder);
	}

	private void addVersionsGreaterQueryParameter(Integer keepNVersions, BasicDBObjectBuilder queryBuilder) {
		addVersionsQueryParameter("$gt", Long.valueOf(keepNVersions), queryBuilder);
	}

	private void addVersionsQueryParameter(String operand, Long version, BasicDBObjectBuilder queryBuilder) {
		if (version != null && version > 0) {
			DBObject thresholdEquation = new BasicDBObject(operand, version);
			queryBuilder
					.add(inMetadata(NodeMetadata.VERSION_ATTRIBUTE_NAME), thresholdEquation);
		}
	}

	protected abstract T keyFromDBObject(DBObject metaDataObject);

	protected abstract BasicDBObjectBuilder getQueryBuilderWithDefaultParameters();

}
