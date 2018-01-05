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
package com.cognifide.aet.job.common.comparators.accessibility.report;

import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import java.io.Serializable;
import java.util.List;

public class AccessibilityReport implements Serializable {

  private static final long serialVersionUID = -3950927262685618465L;

  private final List<AccessibilityIssue> nonExcludedIssues;

  private final List<AccessibilityIssue> excludedIssues;

  private final int errorCount;

  private final int warningCount;

  private final int noticeCount;

  private final boolean showWarning;

  private final boolean showNotice;

  public AccessibilityReport(List<AccessibilityIssue> nonExcludedIssues,
      List<AccessibilityIssue> excludedIssues, int errorCount, int warningCount, int noticeCount,
      AccessibilityReportConfiguration configuration) {

    this.nonExcludedIssues = nonExcludedIssues;
    this.excludedIssues = excludedIssues;
    this.errorCount = errorCount;
    this.warningCount = warningCount;
    this.noticeCount = noticeCount;
    this.showWarning = configuration.isShowWarning();
    this.showNotice = configuration.isShowNotice();
  }

  public List<AccessibilityIssue> getNonExcludedIssues() {
    return nonExcludedIssues;
  }

  public List<AccessibilityIssue> getExcludedIssues() {
    return excludedIssues;
  }

  public int getErrorCount() {
    return errorCount;
  }

  public int getWarningCount() {
    return warningCount;
  }

  public int getNoticeCount() {
    return noticeCount;
  }

  public boolean isShowWarning() {
    return showWarning;
  }

  public boolean isShowNotice() {
    return showNotice;
  }

}
