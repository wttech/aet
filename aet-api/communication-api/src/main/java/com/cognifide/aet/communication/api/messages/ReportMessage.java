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

import java.util.Collections;
import java.util.List;

/**
 * Implementation of BasicMessage that enables sending Report data.
 */
public class ReportMessage implements BasicMessage {

	private static final long serialVersionUID = -2384173430408723184L;

	private String company;

	private String project;

	private String testSuiteName;

	private Status status = Status.OK;

	private List<String> errors = Collections.emptyList();

	private String fullReportUrl;
	
	private String saveAsFileName;

	private String environment;

	private String domain;

	private String correlationId;

	ReportMessage(String company, String project, String testSuiteName, Status status,
			List<String> errors, String fullReportUrl, String saveAsFileName, String environment, String domain, String correlationId) {
		this.company = company;
		this.project = project;
		this.testSuiteName = testSuiteName;
		this.status = status;
		this.errors = errors;
		this.fullReportUrl = fullReportUrl;
		this.environment = environment;
		this.saveAsFileName = saveAsFileName;
		this.domain = domain;
		this.correlationId = correlationId;
	}

	/**
	 * @return company name report was created for.
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @return project name report was created for.
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @return test suite name from which report was created.
	 */
	public String getTestSuiteName() {
		return testSuiteName;
	}

	/**
	 * @return status of the report.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @return errors that occurred during report generation.
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * @return path to full report.
	 */
	public String getFullReportUrl() {
		return fullReportUrl;
	}

	/**
	 * @return environment name on which tests were performed.
	 */
	public String getEnvironment() {
		return environment;
	}
	
	
	/**
	 * @return file name where result will be saved
	 */
	public String getSaveAsFileName() {
		return saveAsFileName;
	}

	/**
	 * @return domain on which tests were performed.
	 */
	public String getDomain() {
		return domain;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("company", company)
				.add("project", project)
				.add("testSuiteName", testSuiteName)
				.add("status", status)
				.add("environment", environment)
				.add("domain", domain)
				.add("correlationId", correlationId)
				.toString();
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.REPORT;
	}

	/**
	 * Report generation status.
	 */
	public enum Status {
		OK, FAILED
	}

}
