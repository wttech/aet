/*
 * Cognifide AET :: Job Common
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
package com.cognifide.aet.job.common.comparators.accessibility;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.Excludable;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityCollectorResult;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReport;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReportConfiguration;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReportGenerator;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class AccessibilityComparator implements ComparatorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccessibilityComparator.class);

	public static final String NAME = "accessibility";

	public static final String TYPE = "accessibility";

	private final Node dataNode;

	private final Node resultNode;

	private final ComparatorProperties comparatorProperties;

	private final List<DataModifierJob> dataModifierJobs;

	private AccessibilityReportConfiguration configuration;

	public AccessibilityComparator(Node dataNode, Node resultNode, ComparatorProperties comparatorProperties,
			List<DataModifierJob> dataModifierJobs) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
		this.dataModifierJobs = dataModifierJobs;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		configuration = new AccessibilityReportConfiguration(params);
	}

	@Override
	public Boolean compare() throws ProcessingException {
		try {
			AccessibilityCollectorResult dataResult = dataNode.getResult(AccessibilityCollectorResult.class);
			List<AccessibilityIssue> issues = dataResult.getAccessibilityIssues();
			for (DataModifierJob<AccessibilityCollectorResult> dataModifierJob : dataModifierJobs) {
				LOGGER.info(
						"Starting {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
				dataResult = dataModifierJob.modifyData(dataResult);
				LOGGER.info(
						"Successfully ended data modifications using {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
			}
			List<AccessibilityIssue> notExcludedIssues = Lists.newLinkedList();
			notExcludedIssues.addAll(Collections2.filter(issues,
					new Excludable.NonExcludedPredicate()));

			List<AccessibilityIssue> excludedIssues = Lists.newLinkedList();
			if (configuration.isShowExcluded()) {
				excludedIssues.addAll(Collections2.filter(issues,
						new Excludable.ExcludedPredicate()));
			}

			AccessibilityReportGenerator resultParser = new AccessibilityReportGenerator(configuration);
			AccessibilityReport comparatorReport = resultParser.generate(notExcludedIssues, excludedIssues);
			ResultStatus resultStatus = getStatus(comparatorReport);
			resultNode.saveResult(new AccessibilityComparatorResult(resultStatus, comparatorReport, comparatorProperties));
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return NO_COMPARISON_RESULT;
	}


	private ResultStatus getStatus(AccessibilityReport report) {
		ResultStatus result;
		if (report.getErrorCount()> 0) {
			result = ResultStatus.FAILED;
		} else if (report.getWarningCount() > 0 || (!configuration.isIgnoreNotice() && report.getNoticeCount() > 0)) {
			result = ResultStatus.WARNING;
		} else {
			result = ResultStatus.SUCCESS;
		}
		return result;
	}
}
