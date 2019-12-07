/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.models;

import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReport;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReportConfiguration;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class AccessibilityErrorWrapper extends AccessibilityReport {

  public static final String ERROR_TYPE = "accessibility";
  public static final Type ARTIFACT_TYPE = new TypeToken<AccessibilityErrorWrapper>() {
  }.getType();

  private String urlName;

  public AccessibilityErrorWrapper(
      List<AccessibilityIssue> nonExcludedIssues,
      List<AccessibilityIssue> excludedIssues,
      int errorCount, int warningCount, int noticeCount,
      AccessibilityReportConfiguration configuration) {
    super(nonExcludedIssues, excludedIssues, errorCount, warningCount, noticeCount, configuration);
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
}
