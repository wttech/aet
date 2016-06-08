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

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.google.common.base.Objects;

/**
 * UrlNodeMetadata - contains common metadata properties for all artifacts that depends on specified url.
 */
public class UrlNodeMetadata extends NodeMetadata {

	private static final long serialVersionUID = 2947689455932765268L;

	public static final String CORRELATION_ID_ATTRIBUTE_NAME = "correlationId";

	public static final String TEST_NAME_ATTRIBUTE_NAME = "testName";

	public static final String URL_NAME_ATTRIBUTE_NAME = "urlName";

	protected String correlationId;

	protected final String testName;

	protected final String url;

	protected final String urlName;

	protected final String collectorModule;

	protected String collectorModuleName;

	public UrlNodeMetadata(String company, String project, String testSuiteName, String environment,
			ArtifactType artifactType, String correlationId, String testName, String url, String urlName,
			String collectorModule, String collectorModuleName) {
		super(company, project, testSuiteName, environment, artifactType);
		this.correlationId = correlationId;
		this.testName = testName;
		this.url = url;
		this.urlName = urlName;
		// Solution presented below should be refactored.
		// Properties collectorModule and collectorModuleName are here only because of logic legacy, where
		// sometimes collectorModuleName is set to be null and sometimes it can't be null and must be at least
		// the same as collectorModule. See descendant's constructors.
		this.collectorModule = collectorModule;
		this.collectorModuleName = collectorModuleName;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getTestName() {
		return testName;
	}

	public String getUrl() {
		return url;
	}

	public String getUrlName() {
		return urlName;
	}

	public String getCollectorModule() {
		return collectorModule;
	}

	public String getCollectorModuleName() {
		return collectorModuleName;
	}

	@Override
	public void accept(MetadataVisitor visitor) throws AETException {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), getTestName(), getUrlName(), getCollectorModule(),
				getCollectorModuleName(), getCorrelationId());
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

	protected boolean matches(UrlNodeMetadata urlNodeMetadata) {
		EqualsBuilder builder = new EqualsBuilder();
		return super.matches(urlNodeMetadata)
				&& builder.append(getCorrelationId(), urlNodeMetadata.getCorrelationId())
				.append(getCollectorModuleName(), urlNodeMetadata.getCollectorModuleName())
				.append(getCollectorModule(), urlNodeMetadata.getCollectorModule())
				.append(getUrlName(), urlNodeMetadata.getUrlName())
				.append(getTestName(), urlNodeMetadata.getTestName()).build();
	}
}
