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
package com.cognifide.aet.communication.api.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;

/**
 * Represents whole test suite. Consists of tests and list of reports that will be generated after tests
 * execution.
 */
public class TestSuiteRun implements Serializable {

	private static final long serialVersionUID = 219968983400540885L;

	private final String name;

	private final String company;

	private final String project;

	private final String environment;

	private final String domain;

	private final List<ReporterStep> reporterSteps;

	private final Map<String, TestRun> testRunMap;

	private final String correlationId;

	private Long version;

	/**
	 * Parameters: name, company, project are part of identifier of test suite.
	 * 
	 * @param name - name of the test suite.
	 * @param company - company name that test belongs to.
	 * @param project - project name.
	 * @param environment - environment name that tests are performed at.
	 * @param domain - common urls domain.
	 * @param testRunList - list of tests that will be executed during this test suite.
	 * @param reporterSteps - list of reports that will be generated as result of this test suite tests.
	 * 
	 */
	public TestSuiteRun(String name, String company, String project, String environment, String domain,
			List<TestRun> testRunList, List<ReporterStep> reporterSteps) {
		this.name = name;
		this.company = company;
		this.project = project;
		this.environment = environment;
		this.reporterSteps = reporterSteps;
		this.domain = domain;
		this.correlationId = CorrelationIdGenerator.generateCorrelationId(company, project, name);
		testRunMap = getMap(testRunList);
	}

	public TestSuiteRun(TestSuiteRun testSuiteRun, String domain, List<TestRun> tests) {
		this.name = testSuiteRun.getName();
		this.company = testSuiteRun.getCompany();
		this.project = testSuiteRun.getProject();
		this.environment = testSuiteRun.getEnvironment();
		this.reporterSteps = testSuiteRun.getReporterSteps();
		this.domain = domain;
		this.correlationId = testSuiteRun.getCorrelationId();
		testRunMap = getMap(tests);
	}

	private Map<String, TestRun> getMap(List<TestRun> testRunList) {
		Map<String, TestRun> result = new HashMap<String, TestRun>();
		for (TestRun testRun : testRunList) {
			result.put(testRun.getName(), testRun);
		}
		return result;
	}

	/**
	 * @return name of the test suite.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return company name that test suite belongs to.
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @return project name.
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @return environment name that tests are performed at.
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @return common urls domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @return list of reports that will be generated as result of this test suite tests.
	 */
	public List<ReporterStep> getReporterSteps() {
		return reporterSteps;
	}

	/**
	 * @return map of tests that will be executed during this test suite (test name -> test definition).
	 */
	public Map<String, TestRun> getTestRunMap() {
		return testRunMap;
	}

	/**
	 * @return number of urls.
	 */
	public int getUrlsCount() {
		int total = 0;
		for (TestRun testRun : testRunMap.values()) {
			total += testRun.getUrls().size();
		}
		return total;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("name", name).add("company", company).add("project", project)
				.add("environment", environment).add("domain", domain).add("correlationId", correlationId)
				.add("version", version).toString();
	}
}
