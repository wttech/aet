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
package com.cognifide.aet.job.common.reporters.accessibility;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.StepResult;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue.IssueType;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReport;
import com.cognifide.aet.job.common.reporters.Bug;
import com.cognifide.aet.job.common.reporters.ReportIssue;
import com.cognifide.aet.job.common.reporters.accessibility.format.AccessibilityFormatter;
import com.cognifide.aet.job.common.reporters.dto.ReporterDto;
import com.cognifide.aet.job.common.reporters.factory.Reporter;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class AccessibilityReporter implements Reporter {

  static final String ACCESSIBILITY_TYPE_NAME = "accessibility";

  private static final Type ACCESSIBILITY_REPORT = new TypeToken<AccessibilityReport>() {
  }.getType();

  private static final Predicate<AccessibilityIssue> ERROR_OR_WARN_ISSUE = issue ->
      issue.getType() == IssueType.ERROR || issue.getType() == IssueType.WARN;

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Reference
  private MetadataDAO metadataDAO;

  @Reference
  private ReporterDto reporterDto;

  @Reference
  private AccessibilityFormatter formatter;

  @Override
  public List<ReportIssue> report(DBKey dbKey, String suiteCorrelationId) {
    Suite suite = getSuite(dbKey, suiteCorrelationId);
    List<AccessibilityIssue> issues = getAccessibilityIssuesForSuite(dbKey, suite);
    List<Bug> bugs = formatter.format(issues);

    return report(bugs);
  }

  private List<ReportIssue> report(List<Bug> bugs) {
    if (CollectionUtils.isEmpty(bugs)) {
      return Collections.emptyList();
    }
    return reporterDto.report(bugs);
  }

  private Suite getSuite(DBKey dbKey, String suiteCorrelationId) {
    try {
      return metadataDAO.getSuite(dbKey, suiteCorrelationId);
    } catch (StorageException e) {
      throw new IllegalStateException("Error getting suite from db!", e);
    }
  }

  private List<AccessibilityIssue> getAccessibilityIssuesForSuite(DBKey dbKey, Suite suite) {
    return suite.getTests().stream()
        .flatMap(test -> test.getUrls().stream())
        .flatMap(url -> url.getSteps().stream())
        .filter(step -> ACCESSIBILITY_TYPE_NAME.equals(step.getName()))
        .flatMap(step -> step.getComparators().stream())
        .filter(comparator -> ACCESSIBILITY_TYPE_NAME.equals(comparator.getName()))
        .map(Comparator::getStepResult)
        .map(StepResult::getArtifactId)
        .map(issueArtifactId -> getNotExcludedAccessibilityIssues(dbKey, issueArtifactId))
        .flatMap(Collection::stream)
        .filter(ERROR_OR_WARN_ISSUE)
        .collect(Collectors.toList());
  }

  private List<AccessibilityIssue> getNotExcludedAccessibilityIssues(DBKey dbKey, String issueId) {
    try {
      AccessibilityReport report = artifactsDAO
          .getJsonFormatArtifact(dbKey, issueId, ACCESSIBILITY_REPORT);
      List<AccessibilityIssue> nonExcludedIssues = report.getNonExcludedIssues();
      return nonExcludedIssues;

    } catch (IOException e) {
      throw new IllegalStateException("Error while accessing database!", e);
    }
  }
}
