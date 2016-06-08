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

import java.util.Map;

import com.cognifide.aet.communication.api.NoNullValueMap;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.builders.NodeMetadataBuilder;
import com.google.common.base.Objects;
import com.mongodb.DBObject;

/**
 * ArtifactVersionKey - defines unique version key for each type of ArtifactType.DATA, ArtifactType.RESULTS,
 * ArtifactType.REPORTS
 * 
 * @Author: Maciej Laskowski
 * @Date: 24.02.15
 */
public class ArtifactVersionKey<T extends NodeMetadata> {

	protected T metadata;

	public ArtifactVersionKey(T metadata) {
		this.metadata = metadata;
	}

	public static ArtifactVersionKey<NodeMetadata> fromDBObject(DBObject dbObject) {
		NodeMetadata nodeMetadata = NodeMetadataBuilder
				.getNodeMetadata()
				.withCompany(dbObject.get(NodeMetadata.COMPANY_ATTRIBUTE_NAME).toString())
				.withProject(dbObject.get(NodeMetadata.PROJECT_ATTRIBUTE_NAME).toString())
				.withTestSuiteName(dbObject.get(NodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME).toString())
				.withEnvironment(dbObject.get(NodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME).toString())
				.withDomain(
						dbObject.get(NodeMetadata.DOMAIN_ATTRIBUTE_NAME) != null ? dbObject.get(
								NodeMetadata.DOMAIN_ATTRIBUTE_NAME).toString() : null).build();
		return new ArtifactVersionKey<>(nodeMetadata);
	}

	@Override
	public boolean equals(Object o) {
		boolean isEqual;
		if (this == o) {
			isEqual = true;
		} else if (o == null || getClass() != o.getClass()) {
			isEqual = false;
		} else {
			ArtifactVersionKey that = (ArtifactVersionKey) o;
			isEqual = Objects.equal(this.metadata.getCompany(), that.metadata.getCompany())
					&& Objects.equal(this.metadata.getProject(), that.metadata.getProject())
					&& Objects.equal(this.metadata.getTestSuiteName(), that.metadata.getTestSuiteName())
					&& Objects.equal(this.metadata.getEnvironment(), that.metadata.getEnvironment())
					&& Objects.equal(this.metadata.getDomain(), that.metadata.getDomain());
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(metadata.getCompany(), metadata.getProject(), metadata.getEnvironment(),
				metadata.getDomain(), metadata.getTestSuiteName());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add(NodeMetadata.COMPANY_ATTRIBUTE_NAME, metadata.getCompany())
				.add(NodeMetadata.PROJECT_ATTRIBUTE_NAME, metadata.getProject())
				.add(NodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME, metadata.getTestSuiteName())
				.add(NodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME, metadata.getEnvironment())
				.add(NodeMetadata.DOMAIN_ATTRIBUTE_NAME, metadata.getDomain()).toString();
	}

	public Map<String, Object> toMap() {
		Map<String, Object> queryParamsMap = new NoNullValueMap<>();
		queryParamsMap.put(NodeMetadata.COMPANY_ATTRIBUTE_NAME, metadata.getCompany());
		queryParamsMap.put(NodeMetadata.PROJECT_ATTRIBUTE_NAME, metadata.getProject());
		queryParamsMap.put(NodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME, metadata.getTestSuiteName());
		queryParamsMap.put(NodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME, metadata.getEnvironment());
		queryParamsMap.put(NodeMetadata.DOMAIN_ATTRIBUTE_NAME, metadata.getDomain());
		return queryParamsMap;
	}
}
