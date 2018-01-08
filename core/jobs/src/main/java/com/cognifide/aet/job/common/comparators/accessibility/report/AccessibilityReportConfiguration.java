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

import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue.IssueType;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class AccessibilityReportConfiguration {

  private static final String PARAM_SHOW_EXCLUDED = "showExcluded";

  private static final String PARAM_IGNORE_NOTICE = "ignore-notice";

  private static final String PARAM_REPORT_LEVEL = "report-level";

  private static final String DEFAULT_REPORT_LEVEL = "error";

  private boolean ignoreNotice = true;

  private boolean showExcluded = true;

  private boolean showNotice;

  private boolean showWarning;

  public AccessibilityReportConfiguration(Map<String, String> params) {
    if (params.containsKey(PARAM_SHOW_EXCLUDED)) {
      showExcluded = BooleanUtils.toBoolean(params.get(PARAM_SHOW_EXCLUDED));
    }

    if (params.containsKey(PARAM_IGNORE_NOTICE)) {
      ignoreNotice = BooleanUtils.toBoolean(params.get(PARAM_IGNORE_NOTICE));
    }

    String reportLevelString = StringUtils
        .defaultString(params.get(PARAM_REPORT_LEVEL), DEFAULT_REPORT_LEVEL);
    if (!ignoreNotice) {
      reportLevelString = IssueType.NOTICE.toString();
    }

    IssueType reportLevel;
    reportLevel = IssueType.valueOf(reportLevelString.toUpperCase());

    showNotice = IssueType.NOTICE.compareTo(reportLevel) <= 0;
    showWarning = IssueType.WARN.compareTo(reportLevel) <= 0;
  }

  public boolean isIgnoreNotice() {
    return ignoreNotice;
  }

  public boolean isShowExcluded() {
    return showExcluded;
  }

  public boolean isShowWarning() {
    return showWarning;
  }

  public boolean isShowNotice() {
    return showNotice;
  }
}
