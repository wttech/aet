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
package com.cognifide.aet.communication.api.messages;

import com.cognifide.aet.communication.api.config.TestSuiteRun;

import java.util.Collections;
import java.util.List;

/**
 * ReportMessageBuilder - builder for ReportMessage.
 */
public class ReportMessageBuilder {

	private final String company;

	private final String project;

	private final String testSuiteName;

	private final String environment;
	
	private final String domain;

	private final String correlationId;
	
	private String fullReportUrl;
	
	private String saveAsFileName;

	private ReportMessage.Status status = ReportMessage.Status.OK;

	private List<String> errors = Collections.emptyList();

	private ReportMessageBuilder(String company, String project, String testSuiteName, String environment,
	                             String domain, String correlationId) {
		this.company = company;
		this.project = project;
		this.testSuiteName = testSuiteName;
		this.environment = environment;
		this.domain = domain;
		this.correlationId = correlationId;
	}

	/**
	 * @param testSuiteRun - test suite run which report will be generated from.
	 * @return new builder instance.
	 */
	public static ReportMessageBuilder fromTestSuiteRun(TestSuiteRun testSuiteRun) {
		return new ReportMessageBuilder(testSuiteRun.getCompany(), testSuiteRun.getProject(),
				testSuiteRun.getName(), testSuiteRun.getEnvironment(), testSuiteRun.getDomain(), testSuiteRun.getCorrelationId());
	}

	public ReportMessageBuilder withStatus(ReportMessage.Status status) {
		this.status = status;
		return this;
	}

	/**
	 * @param errors adds errors that occurred during report generation.
	 */
	public ReportMessageBuilder withErrors(List<String> errors) {
		this.errors = errors;
		return this;
	}

	public ReportMessageBuilder withFullReportUrl(String fullReportUrl) {
		this.fullReportUrl = fullReportUrl;
		return this;
	}
	
	public ReportMessageBuilder withSaveAsFileName(String saveAsFileName){
		this.saveAsFileName = saveAsFileName;
		return this;
	}

	public ReportMessage build() {
		return new ReportMessage(company, project, testSuiteName, status, errors, fullReportUrl, saveAsFileName,
				environment, domain, correlationId);
	}
}
