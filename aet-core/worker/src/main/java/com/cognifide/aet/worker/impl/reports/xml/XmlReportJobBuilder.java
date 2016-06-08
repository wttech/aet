/*
 * Cognifide AET :: Worker
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
package com.cognifide.aet.worker.impl.reports.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.job.api.report.ReportsProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ReportResult;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import com.cognifide.aet.worker.impl.reports.xml.model.Testcase;
import com.cognifide.aet.worker.impl.reports.xml.model.Testsuite;
import com.cognifide.aet.worker.impl.reports.xml.model.XmlReportJob;
import com.google.common.collect.Maps;

public class XmlReportJobBuilder {
	
	private static final String REPORT_FILENAME = "xunit-report.xml";

	private List<Node> resultsNodes;

	private ReportsProperties reportsProperties;

	private Map<String, TestRun> expectedResults;

	private final Map<String, Testsuite> testSuites = Maps.newHashMap();

	private Map<String, List<String>> testCasesUrls = Maps.newHashMap();
	
	private String serviceUrl;
	
	public XmlReportJobBuilder withExpectedResults(Map<String, TestRun> expectedResults) {
		this.expectedResults = new HashMap<>(expectedResults);
		return this;
	}

	public XmlReportJobBuilder withReportsProperties(ReportsProperties reportsProperties) {
		this.reportsProperties = reportsProperties;
		return this;
	}

	public XmlReportJobBuilder withResultsNodes(List<Node> resultsNodes) {
		this.resultsNodes = new ArrayList<>(resultsNodes);
		return this;
	}
	
	public XmlReportJobBuilder withServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
		return this;
	}
	
	public static XmlReportJobBuilder create(){
		return new XmlReportJobBuilder();
	}

	public ReportResult build() throws VersionStorageException {
		Validate.notNull(resultsNodes, "Results nodes can't be null");
		Validate.notNull(reportsProperties, "Report properties can't be null");
		Validate.notNull(expectedResults, "Expected results can't be null");
		Validate.notNull(serviceUrl, "Service url not provided");
		
		XmlReportJob job = new XmlReportJob(reportsProperties.getRebasePatternsUrl().get(
				"testSuiteName"));

		for (Node resultNode : resultsNodes) {
			Map<String, Object> result = resultNode.getResult(Map.class);
			Map<String, String> properties = (Map<String, String>) result.get("properties");
			String testName = properties.get("testName");
			String testCaseName = properties.get("comparatorTitle") + " " + properties.get("urlName");
			String statusString = result.get("status").toString();
			ResultStatus status = ResultStatus.valueOf(statusString);

			Testsuite testSuite = getOrCreateTestSuite(testName);
			testCasesUrls.get(testName).add(properties.get("urlName"));
			
			List<Testcase> testCases = testSuite.getTestcase();
			testCases.add(new Testcase(testCaseName, status == ResultStatus.FAILED));
			
		}
		checkResultsMapAndFillMissingResults(testSuites);

		job.getTestsuite().addAll(testSuites.values());
		job.setFullReportUrl(serviceUrl);
		job.setSaveAsFileName(REPORT_FILENAME);
		return job;
	}

	private Testsuite getOrCreateTestSuite(String testName) {
		Testsuite testSuite = testSuites.get(testName);
		if (testSuite == null) {
			testSuite = new Testsuite(testName);
			testSuites.put(testName, testSuite);
			testCasesUrls.put(testName, new ArrayList<String>());
		}
		return testSuite;
	}

	private void checkResultsMapAndFillMissingResults(Map<String, Testsuite> testSuites) {
		for (Map.Entry<String, TestRun> entry : expectedResults.entrySet()) {
			String testName = entry.getKey();

			getOrCreateTestSuite(testName);
			checkTestGroupAndFillMissingResults(testName, entry.getValue().getUrls());
		}
	}

	private void checkTestGroupAndFillMissingResults(String testName, List<ExtendedUrl> urls) {
		for (ExtendedUrl extendedUrl : urls) {
			if (!testCasesUrls.get(testName).contains(extendedUrl.getName())) {
				testCasesUrls.get(testName).add(extendedUrl.getName());
				testSuites.get(testName).getTestcase().add(new Testcase(extendedUrl.getName(), false));
			}
		}
	}

}
