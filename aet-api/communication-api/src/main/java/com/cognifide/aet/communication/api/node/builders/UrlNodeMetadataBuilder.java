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
package com.cognifide.aet.communication.api.node.builders;

import com.cognifide.aet.communication.api.node.UrlNodeMetadata;

/**
 * UrlNodeMetadataBuilder - builder for UrlNodeMetadata class.
 * 
 * @Author: Maciej Laskowski
 * @Date: 13.02.15
 */
public class UrlNodeMetadataBuilder extends AMetadataBuilder<UrlNodeMetadata, UrlNodeMetadataBuilder> {

	protected String collectorModuleName;

	protected String correlationId;

	protected String testName;

	protected String url;

	protected String urlName;

	protected String collectorModule;

	private UrlNodeMetadataBuilder() {
	}

	public static UrlNodeMetadataBuilder getUrlNodeMetadata() {
		return new UrlNodeMetadataBuilder();
	}

	public UrlNodeMetadataBuilder withCollectorModuleName(String collectorModuleName) {
		this.collectorModuleName = collectorModuleName;
		return this;
	}

	public UrlNodeMetadataBuilder withCorrelationId(String correlationId) {
		this.correlationId = correlationId;
		return this;
	}

	public UrlNodeMetadataBuilder withTestName(String testName) {
		this.testName = testName;
		return this;
	}

	public UrlNodeMetadataBuilder withUrl(String url) {
		this.url = url;
		return this;
	}

	public UrlNodeMetadataBuilder withUrlName(String urlName) {
		this.urlName = urlName;
		return this;
	}

	public UrlNodeMetadataBuilder withCollectorModule(String collectorModule) {
		this.collectorModule = collectorModule;
		return this;
	}

	@Override
	public UrlNodeMetadata build() {
		UrlNodeMetadata urlNodeMetadata = new UrlNodeMetadata(company, project, testSuiteName, environment,
				artifactType, correlationId, testName, url, urlName, collectorModule, collectorModuleName);
		urlNodeMetadata.setDomain(domain);
		urlNodeMetadata.setVersion(version);
		return urlNodeMetadata;
	}

	@Override
	protected UrlNodeMetadataBuilder self() {
		return this;
	}
}
