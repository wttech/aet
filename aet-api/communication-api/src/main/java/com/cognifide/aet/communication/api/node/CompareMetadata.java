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
import com.google.common.base.Objects;

/**
 * CompareMetadata - keeps metadata of Compare phase artifacts (@see ArtifactType.RESULTS).
 */
public class CompareMetadata extends CollectMetadata {

	private static final long serialVersionUID = -585990018775331057L;

	public static final String COMPARATOR_ATTRIBUTE_NAME = "comparatorModuleName";

	public static final String COMPARATOR_ATTRIBUTE_TYPE = "comparatorModule";

	private final String comparatorModule;

	private String comparatorModuleName;

	public CompareMetadata(String company, String project, String testSuiteName, String environment,
			String correlationId, String testName, String url, String urlName, String collectorModule,
			String collectorModuleName, String comparatorModule, String comparatorModuleName) {
		super(company, project, testSuiteName, environment, correlationId, testName, url, urlName,
				collectorModule, collectorModuleName, ArtifactType.RESULTS);
		this.comparatorModule = comparatorModule;
		this.comparatorModuleName = StringUtils.defaultIfBlank(comparatorModuleName, comparatorModule);
	}

	public static CompareMetadata fromCollectMetadata(CollectMetadata collectMetadata,
			String comparatorModule, String collectorModuleName) {
		CompareMetadata compareMetadata = new CompareMetadata(collectMetadata.getCompany(),
				collectMetadata.getProject(), collectMetadata.getTestSuiteName(),
				collectMetadata.getEnvironment(), collectMetadata.getCorrelationId(),
				collectMetadata.getTestName(), collectMetadata.getUrl(), collectMetadata.getUrlName(),
				collectMetadata.getCollectorModule(), collectMetadata.getCollectorModuleName(),
				comparatorModule, collectorModuleName);
		compareMetadata.setDomain(collectMetadata.getDomain());
		compareMetadata.setVersion(collectMetadata.getVersion());
		compareMetadata.setDescription(collectMetadata.getDescription());
		return compareMetadata;
	}

	public String getComparatorModule() {
		return comparatorModule;
	}

	public String getComparatorModuleName() {
		return comparatorModuleName;
	}

	/**
	 * Clears comparatorModuleName value. This is used to transport CompareMetadata comparatorModuleName
	 * without value and should be changed!
	 */
	public void clearComparatorModuleName() {
		this.comparatorModuleName = null;
	}

	@Override
	public void accept(MetadataVisitor visitor) throws AETException {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), getComparatorModule(), getComparatorModuleName());
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

	protected boolean matches(CompareMetadata compareMetadata) {
		return super.matches(compareMetadata)
				&& Objects.equal(getComparatorModule(), compareMetadata.getComparatorModule())
				&& Objects.equal(getComparatorModuleName(), compareMetadata.getComparatorModuleName());
	}
}
