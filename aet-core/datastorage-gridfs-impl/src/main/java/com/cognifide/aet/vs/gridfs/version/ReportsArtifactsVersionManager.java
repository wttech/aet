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

import com.cognifide.aet.comments.api.manager.CommentsManager;
import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.communication.api.node.builders.UrlNodeMetadataBuilder;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.apache.felix.scr.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ReportsArtifactsVersionManager
 *
 */
public class ReportsArtifactsVersionManager extends
		ArtifactsVersionManager<ArtifactVersionKey<NodeMetadata>, NodeMetadata> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportsArtifactsVersionManager.class);

	@Reference
	private CommentsManager commentsManager;

	public ReportsArtifactsVersionManager(GridFS gridFS, String metadataPrefix) {
		super(gridFS, metadataPrefix);
	}

	@Override
	protected ArtifactVersionKey<NodeMetadata> keyFromDBObject(DBObject metaDataObject) {
		return ArtifactVersionKey.fromDBObject(metaDataObject);
	}

	@Override
	protected BasicDBObjectBuilder getQueryBuilderWithDefaultParameters() {
		BasicDBObjectBuilder basicDBObjectBuilder = BasicDBObjectBuilder.start();
		basicDBObjectBuilder.add(inMetadata(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME),
				ArtifactType.REPORTS.name());
		return basicDBObjectBuilder;
	}

	@Override
	protected int removeNodes(boolean dryRun, ArtifactVersionKey<NodeMetadata> versionMetadata,
			DBObject query) {
		List<GridFSDBFile> gridFSDBFiles = gridFS.find(query);
		int removedNodes = gridFSDBFiles.size();
		LOGGER.info("{} nodes will be removed.", removedNodes);
		if (!dryRun) {
			if (removedNodes > 0) {
				for (GridFSFile file : gridFSDBFiles) {
					removeComments(file);
				}
				gridFS.remove(query);
				LOGGER.info("{} nodes removed for {}", removedNodes, versionMetadata);
			} else {
				LOGGER.info("No nodes removed for {}", versionMetadata);
			}
		}
		return removedNodes;
	}

	private void removeComments(GridFSFile file) {
		DBObject metadataObject = file.getMetaData();
		UrlNodeMetadataBuilder urlNodeMetadataBuilder = UrlNodeMetadataBuilder.getUrlNodeMetadata()
				.withCompany(metadataObject.get(UrlNodeMetadata.COMPANY_ATTRIBUTE_NAME).toString())
				.withProject(metadataObject.get(UrlNodeMetadata.PROJECT_ATTRIBUTE_NAME).toString())
				.withTestSuiteName(metadataObject.get(UrlNodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME).toString())
				.withEnvironment(metadataObject.get(UrlNodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME).toString())
				.withCorrelationId(metadataObject.get(UrlNodeMetadata.CORRELATION_ID_ATTRIBUTE_NAME).toString());
		UrlNodeMetadata nodeMetadata = urlNodeMetadataBuilder.build();
		if (nodeMetadata.getCorrelationId() != null) {
			commentsManager.deleteComments(nodeMetadata);
		}
	}
}
