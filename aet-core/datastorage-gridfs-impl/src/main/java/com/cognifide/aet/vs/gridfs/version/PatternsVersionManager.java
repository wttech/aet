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

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;

/**
 * PatternsVersionManager
 * 
 * @Author: Maciej Laskowski
 * @Date: 25.02.15
 */
public class PatternsVersionManager extends ArtifactsVersionManager<PatternVersionKey, PatternMetadata> {

	public PatternsVersionManager(GridFS gridFS, String metadataPrefix) {
		super(gridFS, metadataPrefix);
	}

	@Override
	protected PatternVersionKey keyFromDBObject(DBObject metaDataObject) {
		return PatternVersionKey.patternFromDBObject(metaDataObject);
	}

	@Override
	protected BasicDBObjectBuilder getQueryBuilderWithDefaultParameters() {
		BasicDBObjectBuilder basicDBObjectBuilder = BasicDBObjectBuilder.start();

		basicDBObjectBuilder.add(inMetadata(PatternMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME),
				ArtifactType.PATTERNS.name());
		basicDBObjectBuilder.add(inMetadata(PatternMetadata.CURRENT_PATTERN), true);

		return basicDBObjectBuilder;
	}

}
