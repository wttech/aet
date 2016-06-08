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
 * ReportMetadata - keeps metadata of Report phase artifacts (@see ArtifactType.REPORTS).
 */
public class ReportMetadata extends NodeMetadata {

	private static final long serialVersionUID = 2515691363244747555L;

	public static final String CORRELATION_ID_ATTRIBUTE_NAME = "correlationId";

	protected final String correlationId;

	private final String reporterModule;

	public ReportMetadata(String company, String project, String testSuiteName, String environment,
			String correlationId, String reporterModule) {
		super(company, project, testSuiteName, environment, ArtifactType.REPORTS);
		this.correlationId = correlationId;
		this.reporterModule = reporterModule;
	}

	public static ReportMetadata fromUrlNodeMetadata(UrlNodeMetadata urlNodeMetadata, String reporterModule) {
		ReportMetadata reportMetadata = new ReportMetadata(urlNodeMetadata.getCompany(),
				urlNodeMetadata.getProject(), urlNodeMetadata.getTestSuiteName(),
				urlNodeMetadata.getEnvironment(), urlNodeMetadata.getCorrelationId(), reporterModule);
		reportMetadata.setDomain(urlNodeMetadata.getDomain());
		reportMetadata.setVersion(urlNodeMetadata.getVersion());
		return reportMetadata;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public String getReporterModule() {
		return reporterModule;
	}

	@Override
	public void accept(MetadataVisitor visitor) throws AETException {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), correlationId, reporterModule);
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

	protected boolean matches(ReportMetadata reportMetadata) {
		EqualsBuilder builder = new EqualsBuilder();
		return super.matches(reportMetadata)
				&& builder.append(getCorrelationId(), reportMetadata.getCorrelationId())
				.append(getReporterModule(), reportMetadata.getReporterModule()).build();
	}
}
