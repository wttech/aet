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

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.NodeMetadata;

/**
 * AMetadataBuilder - abstract builder for Metadata builders.
 * 
 * @Author: Maciej Laskowski
 * @Date: 20.02.15
 */
abstract class AMetadataBuilder<T extends NodeMetadata, B extends AMetadataBuilder<T, B>> {

	protected String domain;

	protected Long version;

	protected String company;

	protected String project;

	protected String testSuiteName;

	protected String environment;

	protected ArtifactType artifactType;

	public B withDomain(String domain) {
		this.domain = domain;
		return self();
	}

	public B withVersion(Long version) {
		this.version = version;
		return self();
	}

	public B withCompany(String company) {
		this.company = company;
		return self();
	}

	public B withProject(String project) {
		this.project = project;
		return self();
	}

	public B withTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
		return self();
	}

	public B withEnvironment(String environment) {
		this.environment = environment;
		return self();
	}

	public B withArtifactType(ArtifactType artifactType) {
		this.artifactType = artifactType;
		return self();
	}

	protected abstract B self();

	public abstract T build();

}
