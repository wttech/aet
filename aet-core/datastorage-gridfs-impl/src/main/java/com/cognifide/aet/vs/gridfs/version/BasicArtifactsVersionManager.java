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
import java.util.List;
import java.util.Set;

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import org.apache.commons.collections.CollectionUtils;

/**
 * BasicArtifactsVersionManager
 * 
 * @Author: Maciej Laskowski
 * @Date: 25.02.15
 */
public class BasicArtifactsVersionManager extends
		ArtifactsVersionManager<ArtifactVersionKey<NodeMetadata>, NodeMetadata> {

	private final Set<ArtifactType> artifactTypes;

	public BasicArtifactsVersionManager(GridFS gridFS, String metadataPrefix, Set<ArtifactType> artifactTypes) {
		super(gridFS, metadataPrefix);
		this.artifactTypes = artifactTypes;
	}

	@Override
	protected ArtifactVersionKey<NodeMetadata> keyFromDBObject(DBObject metaDataObject) {
		return ArtifactVersionKey.fromDBObject(metaDataObject);
	}

	@Override
	protected BasicDBObjectBuilder getQueryBuilderWithDefaultParameters() {
		BasicDBObjectBuilder basicDBObjectBuilder = BasicDBObjectBuilder.start();
		if (!artifactTypes.isEmpty()) {
			List<String> types = Lists.newArrayList();
			for (ArtifactType artifactType : artifactTypes) {
				types.add(artifactType.name());
			}
			BasicDBObject inQuery = new BasicDBObject("$in", types);
			basicDBObjectBuilder.add(
					inMetadata(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME), inQuery);
		}
		return basicDBObjectBuilder;
	}

	@Override
	protected List<String> validateParameters(Date keepAllAfter, Integer keepNVersions) {
		List<String> errorsList = super.validateParameters(keepAllAfter, keepNVersions);
		if (CollectionUtils.isEmpty(artifactTypes)) {
			errorsList.add("At least one data type must be defined! [DATA,RESULTS,REPORTS]");
		} else if (artifactTypes.contains(ArtifactType.PATTERNS)) {
			errorsList.add("Can't use method removeNonPatternArtifacts for patterns!");
		}
		return errorsList;
	}
}
