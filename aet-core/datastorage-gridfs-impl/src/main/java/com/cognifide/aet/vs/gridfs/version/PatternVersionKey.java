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

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.google.common.base.Objects;
import com.mongodb.DBObject;

/**
 * ArtifactVersionKey - defines unique version key for PatternMetadata (ArtifactType.PATTERNS)
 * 
 * @Author: Maciej Laskowski
 * @Date: 24.02.15
 */
public class PatternVersionKey extends ArtifactVersionKey<PatternMetadata> {

	public PatternVersionKey(PatternMetadata metadata) {
		super(metadata);
	}

	public static PatternVersionKey patternFromDBObject(DBObject dbObject) {
		String company = dbObject.get(PatternMetadata.COMPANY_ATTRIBUTE_NAME).toString();
		String project = dbObject.get(PatternMetadata.PROJECT_ATTRIBUTE_NAME).toString();
		String testSuiteName = dbObject.get(PatternMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME).toString();
		String environment = dbObject.get(PatternMetadata.ENVIRONMENT_ATTRIBUTE_NAME).toString();
		String testName = dbObject.get(PatternMetadata.TEST_NAME_ATTRIBUTE_NAME).toString();
		String urlName = dbObject.get(PatternMetadata.URL_NAME_ATTRIBUTE_NAME).toString();
		String collectorModule = dbObject.get(PatternMetadata.COLLECTOR_ATTRIBUTE_TYPE).toString();
		String collectorModuleName = dbObject.get(PatternMetadata.COLLECTOR_ATTRIBUTE_NAME).toString();
		PatternMetadata patternMetadata = new PatternMetadata(company, project, testSuiteName, environment,
				testName, urlName, collectorModule, collectorModuleName, null);
		return new PatternVersionKey(patternMetadata);
	}

	@Override
	public boolean equals(Object o) {
		boolean isEqual;
		if (this == o) {
			isEqual = true;
		} else if (o == null || getClass() != o.getClass()) {
			isEqual = false;
		} else {
			PatternVersionKey that = (PatternVersionKey) o;
			isEqual = Objects.equal(this.metadata.getArtifactType(), that.metadata.getArtifactType())
					&& Objects.equal(this.metadata.getCompany(), that.metadata.getCompany())
					&& Objects.equal(this.metadata.getProject(), that.metadata.getProject())
					&& Objects.equal(this.metadata.getTestSuiteName(), that.metadata.getTestSuiteName())
					&& Objects.equal(this.metadata.getEnvironment(), that.metadata.getEnvironment())
					&& Objects.equal(this.metadata.getUrlName(), that.metadata.getUrlName())
					&& Objects.equal(this.metadata.getTestName(), that.metadata.getTestName())
					&& Objects.equal(this.metadata.getCollectorModule(), that.metadata.getCollectorModule())
					&& Objects.equal(this.metadata.getCollectorModuleName(),
							that.metadata.getCollectorModuleName());
		}
		return isEqual;

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(metadata.getArtifactType(), metadata.getCompany(), metadata.getProject(),
				metadata.getEnvironment(), metadata.getDomain(), metadata.getTestSuiteName(),
				metadata.getUrlName(), metadata.getTestName(), metadata.getCollectorModule(),
				metadata.getCollectorModuleName());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add(PatternMetadata.COMPANY_ATTRIBUTE_NAME, metadata.getCompany())
				.add(PatternMetadata.PROJECT_ATTRIBUTE_NAME, metadata.getProject())
				.add(PatternMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME, metadata.getTestSuiteName())
				.add(PatternMetadata.ENVIRONMENT_ATTRIBUTE_NAME, metadata.getEnvironment())
				.add(PatternMetadata.TEST_NAME_ATTRIBUTE_NAME, metadata.getTestName())
				.add(PatternMetadata.URL_NAME_ATTRIBUTE_NAME, metadata.getUrlName())
				.add(PatternMetadata.COLLECTOR_ATTRIBUTE_TYPE, metadata.getCollectorModule())
				.add(PatternMetadata.COLLECTOR_ATTRIBUTE_NAME, metadata.getCollectorModuleName()).toString();
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> queryParamsMap = super.toMap();
		queryParamsMap.put(PatternMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME, ArtifactType.PATTERNS.name());
		queryParamsMap.put(PatternMetadata.TEST_NAME_ATTRIBUTE_NAME, metadata.getTestName());
		queryParamsMap.put(PatternMetadata.URL_NAME_ATTRIBUTE_NAME, metadata.getUrlName());
		queryParamsMap.put(PatternMetadata.COLLECTOR_ATTRIBUTE_TYPE, metadata.getCollectorModule());
		queryParamsMap.put(PatternMetadata.COLLECTOR_ATTRIBUTE_NAME, metadata.getCollectorModuleName());
		queryParamsMap.put(PatternMetadata.CURRENT_PATTERN, false);
		return queryParamsMap;
	}
}
