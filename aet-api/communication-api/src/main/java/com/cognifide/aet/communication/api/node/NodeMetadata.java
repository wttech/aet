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

import java.io.Serializable;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.google.common.base.Objects;

/**
 * NodeMetadata - contains basic and mandatory information about node.
 */
public class NodeMetadata implements Serializable, Visitable {

	private static final long serialVersionUID = 6929274613072547809L;

	public static final String COMPANY_ATTRIBUTE_NAME = "company";

	public static final String PROJECT_ATTRIBUTE_NAME = "project";

	public static final String TEST_SUITE_NAME_ATTRIBUTE_NAME = "testSuiteName";

	public static final String ENVIRONMENT_ATTRIBUTE_NAME = "environment";

	public static final String ARTIFACT_TYPE_ATTRIBUTE_NAME = "artifactType";

	public static final String DOMAIN_ATTRIBUTE_NAME = "domain";

	public static final String VERSION_ATTRIBUTE_NAME = "version";

	/**
	 * Name of company.
	 */
	private final String company;

	/**
	 * Name of project.
	 */
	private final String project;

	/**
	 * Name of test suite.
	 */
	private final String testSuiteName;

	/**
	 * Name of environment.
	 */
	private final String environment;

	/**
	 * Name of artifactType.
	 */
	private final String artifactType;

	/**
	 * [Optional] Domain on which all urls will be collected.
	 */
	private String domain;

	/**
	 * Version of artifact.
	 */
	private Long version;

	public NodeMetadata(String company, String project, String testSuiteName, String environment,
			ArtifactType artifactType) {
		this.company = company;
		this.project = project;
		this.testSuiteName = testSuiteName;
		this.environment = environment;
		this.artifactType = artifactType != null ? artifactType.name() : null;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getCompany() {
		return company;
	}

	public String getProject() {
		return project;
	}

	public String getTestSuiteName() {
		return testSuiteName;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getArtifactType() {
		return artifactType;
	}

	public String getDomain() {
		return domain;
	}

	public Long getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("company", company).add("project", project)
				.add("testSuiteName", testSuiteName).add("environment", environment)
				.add("artifactType", artifactType).add("domain", domain)
				.add("version", version).toString();
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

	@Override
	public int hashCode() {
		return Objects.hashCode(getCompany(), getProject(), getEnvironment(), getTestSuiteName());
	}

	protected boolean matches(NodeMetadata nodeMetadata) {
		return Objects.equal(getTestSuiteName(), nodeMetadata.getTestSuiteName())
				&& Objects.equal(getEnvironment(), nodeMetadata.getEnvironment())
				&& Objects.equal(getProject(), nodeMetadata.getProject())
				&& Objects.equal(getCompany(), nodeMetadata.getCompany());
	}
}
