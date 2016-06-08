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
package com.cognifide.aet.worker.impl.reports.html;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.job.api.report.ReportsProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ReportResult;
import com.cognifide.aet.vs.ReportResultStats;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;

public class HtmlReportJob extends ReportResult {

	private static final long serialVersionUID = -6855914965260803293L;

	private final Map<String, Object> results;

	private final ReportsProperties reportsProperties;

	private final Map<String, TestRun> testRunMap;

	private HtmlReportJob(Builder builder) {
		setStats(builder.stats);
		setFullReportUrl(builder.serviceUrl);
		setSaveAsFileName(Builder.REPORT_FILENAME);
		this.results = builder.results;
		this.testRunMap = builder.testRunMap;
		this.reportsProperties = builder.reportsProperties;
	}

	public Map<String, Object> getResults() {
		return results;
	}

	public ReportsProperties getReportsProperties() {
		return reportsProperties;
	}

	public Map<String, TestRun> getTestRunMap() {
		return testRunMap;
	}

	public static class Builder {
		
		private static final String REPORT_FILENAME = "report-full.html";
		
		private String serviceUrl;
		
		private ReportResultStats stats;

		private Map<String, Object> results;

		private Map<String, TestRun> testRunMap;

		private ReportsProperties reportsProperties;

		private int passedCounter = 0;

		private int failedCounter = 0;

		private int warningCounter = 0;

		private List<Node> resultsNodes;
		
		public static Builder create() {
			return new Builder();
		}

		public Builder withResultNodes(List<Node> resultsNodes) {
			this.resultsNodes = new ArrayList<Node>(resultsNodes);
			return this;
		}

		public Builder withTestRunMap(Map<String, TestRun> testRunMap) {
			this.testRunMap = new HashMap<String, TestRun>(testRunMap);
			return this;
		}

		public Builder withReportsProperties(ReportsProperties reportsProperties) {
			this.reportsProperties = reportsProperties;
			return this;
		}
		
		public Builder withServiceUrl(String serviceUrl){
			this.serviceUrl = serviceUrl;
			return this;
		}

		public HtmlReportJob build() throws VersionStorageException {
			Validate.notNull(resultsNodes, "Result nodes not provided");
			Validate.notNull(testRunMap, "Test run map provided");
			Validate.notNull(reportsProperties, "Report properties not provided");
			Validate.notNull(serviceUrl, "Service url not provided");
			prepareResults();
			prepareStats();

			return new HtmlReportJob(this);
		}
		
		private void prepareStats() throws VersionStorageException {
			stats = new ReportResultStats(passedCounter, failedCounter, warningCounter, resultsNodes.size(),
					Calendar.getInstance().getTime());
		}

		private void prepareResults() throws VersionStorageException {
			results = Maps.newHashMap();

			for (Node resultNode : resultsNodes) {
				Map<String, Object> result = resultNode.getResult(Map.class);
				Map<String, String> properties = (Map<String, String>) result.get("properties");
				String testName = properties.get("testName");
				String urlName = StringUtils.defaultString(properties.get("urlName"), properties.get("url"));
				String statusString = result.get("status").toString();
				ResultStatus status = ResultStatus.valueOf(statusString);
				incrementStatusCounter(status);

				Map<String, Object> testGroup = (Map<String, Object>) results.get(testName);
				if (testGroup == null) {
					testGroup = Maps.newHashMap();
					results.put(testName, testGroup);
				}

				List<Map<String, Object>> urlGroup = (List<Map<String, Object>>) testGroup.get(urlName);
				if (urlGroup == null) {
					urlGroup = Lists.newArrayList();
					testGroup.put(urlName, urlGroup);
				}
				urlGroup.add(result);
			}
			checkResultsMapAndFillMissingResults(results);
		}

		private void checkResultsMapAndFillMissingResults(Map<String, Object> resultsMap) {
			for (Map.Entry<String, TestRun> entry : testRunMap.entrySet()) {
				String testName = entry.getKey();
				if (!resultsMap.containsKey(testName)) {
					resultsMap.put(testName, Maps.newHashMap());
				}
				checkTestGroupAndFillMissingResults((Map<String, Object>) resultsMap.get(testName), entry
						.getValue().getUrls());
			}
		}

		private void checkTestGroupAndFillMissingResults(Map<String, Object> testGroup, List<ExtendedUrl> urls) {
			for (ExtendedUrl extendedUrl : urls) {
				if (!testGroup.containsKey(extendedUrl.getName())) {
					testGroup.put(extendedUrl.getName(), generateFailedUrl(extendedUrl));
				}
			}
		}

		/**
		 * Temporary solution for generating error placeholders on html report.
		 */
		private List<Map<String, Object>> generateFailedUrl(ExtendedUrl extendedUrl) {
			Map<String, String> propertiesMap = new LinkedTreeMap<String, String>();
			propertiesMap.put("comparatorModule", "failure");
			propertiesMap.put("comparatorTitle", "failure");
			propertiesMap.put("url", extendedUrl.getUrl());

			Map<String, Object> map = new LinkedTreeMap<String, Object>();
			map.put("status", "FAILED");
			map.put("properties", propertiesMap);
			map.put("hasPattern", false);
			return Collections.singletonList(map);
		}

		private void incrementStatusCounter(ResultStatus resultStatus) {
			switch (resultStatus) {
				case SUCCESS:
					passedCounter++;
					break;
				case WARNING:
					warningCounter++;
					break;
				case FAILED:
					failedCounter++;
					break;
				default:
					break;
			}
		}
	}
}
