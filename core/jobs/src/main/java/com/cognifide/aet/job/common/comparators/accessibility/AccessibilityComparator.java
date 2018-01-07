/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.comparators.accessibility;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.Excludable;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReport;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReportConfiguration;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReportGenerator;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class AccessibilityComparator implements ComparatorJob {

  public static final String NAME = "accessibility";

  public static final String TYPE = "accessibility";

  private final ArtifactsDAO artifactsDAO;

  private final ComparatorProperties properties;

  private final List<DataFilterJob> dataFilterJobs;

  private static final Type ACCESSIBILITY_ISSUE_LIST = new TypeToken<List<AccessibilityIssue>>() {
  }.getType();

  private AccessibilityReportConfiguration configuration;

  public AccessibilityComparator(ArtifactsDAO artifactsDAO,
      ComparatorProperties comparatorProperties,
      List<DataFilterJob> dataFilterJobs) {
    this.artifactsDAO = artifactsDAO;
    this.properties = comparatorProperties;
    this.dataFilterJobs = dataFilterJobs;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    configuration = new AccessibilityReportConfiguration(params);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult result;
    try {
      List<AccessibilityIssue> issues = artifactsDAO.getJsonFormatArtifact(properties,
          properties.getCollectedId(), ACCESSIBILITY_ISSUE_LIST);

      for (DataFilterJob<List<AccessibilityIssue>> dataFilterJob : dataFilterJobs) {
        issues = dataFilterJob.modifyData(issues);
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
      AccessibilityReport comparatorReport = resultParser
          .generate(notExcludedIssues, excludedIssues);
      ComparatorStepResult.Status resultStatus = getStatus(comparatorReport);
      String resultId = artifactsDAO.saveArtifactInJsonFormat(properties, comparatorReport);
      result = new ComparatorStepResult(resultId, resultStatus);

    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
  }

  private ComparatorStepResult.Status getStatus(AccessibilityReport report) {
    ComparatorStepResult.Status result;
    if (report.getErrorCount() > 0) {
      result = ComparatorStepResult.Status.FAILED;
    } else if (report.getWarningCount() > 0 || (!configuration.isIgnoreNotice()
        && report.getNoticeCount() > 0)) {
      result = ComparatorStepResult.Status.WARNING;
    } else {
      result = ComparatorStepResult.Status.PASSED;
    }
    return result;
  }
}
