/*
 * Cognifide AET :: Communication API
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
package com.cognifide.aet.communication.api.node;

import org.apache.commons.lang3.StringUtils;

import com.cognifide.aet.communication.api.exceptions.AETException;

/**
 * CollectMetadata - keeps metadata of Collect phase artifacts (@see ArtifactType.DATA).
 */
public class CollectMetadata extends UrlNodeMetadata {

	private static final long serialVersionUID = 3029848425873279706L;

	public static final String COLLECTOR_ATTRIBUTE_NAME = "collectorModuleName";

	public static final String COLLECTOR_ATTRIBUTE_TYPE = "collectorModule";


	/**
	 * [Optional] description of node.
	 */
	private String description;

	public CollectMetadata(String company, String project, String testSuiteName, String environment,
			String correlationId, String testName, String url, String urlName, String collectorModule,
			String collectorModuleName) {
		this(company, project, testSuiteName, environment, correlationId, testName, url, urlName,
				collectorModule, collectorModuleName, ArtifactType.DATA);
	}

	protected CollectMetadata(String company, String project, String testSuiteName, String environment,
			String correlationId, String testName, String url, String urlName, String collectorModule,
			String collectorModuleName, ArtifactType artifactType) {
		super(company, project, testSuiteName, environment, artifactType, correlationId, testName, url,
				urlName, collectorModule, collectorModuleName);
		this.collectorModuleName = StringUtils.defaultIfBlank(collectorModuleName, collectorModule);
	}

	public static CollectMetadata fromUrlNodeMetadata(UrlNodeMetadata urlNodeMetadata,
			String collectorModule, String collectorModuleName) {
		CollectMetadata collectMetadata = new CollectMetadata(urlNodeMetadata.getCompany(),
				urlNodeMetadata.getProject(), urlNodeMetadata.getTestSuiteName(),
				urlNodeMetadata.getEnvironment(), urlNodeMetadata.getCorrelationId(),
				urlNodeMetadata.getTestName(), urlNodeMetadata.getUrl(), urlNodeMetadata.getUrlName(),
				collectorModule, collectorModuleName);
		collectMetadata.setDomain(urlNodeMetadata.getDomain());
		collectMetadata.setVersion(urlNodeMetadata.getVersion());
		return collectMetadata;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void accept(MetadataVisitor visitor) throws AETException {
		visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj == this) {
			result = true;
		} else if (obj != null && obj.getClass() == this.getClass()) {
			result = matches(this.getClass().cast(obj));
		}
		return result;
	}
}
