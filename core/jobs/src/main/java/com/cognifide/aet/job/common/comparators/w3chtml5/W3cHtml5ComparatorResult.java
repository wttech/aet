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
package com.cognifide.aet.job.common.comparators.w3chtml5;


import java.io.Serializable;
import java.util.List;

public class W3cHtml5ComparatorResult implements Serializable {

  private static final long serialVersionUID = -9145540068666858078L;

  private final int errorCount;

  private final int warningCount;

  private final List<W3cHtml5Issue> issues;

  private final List<W3cHtml5Issue> excludedIssues;

  public W3cHtml5ComparatorResult(int errorCount, int warningCount, List<W3cHtml5Issue> issues,
      List<W3cHtml5Issue> excludedIssues) {
    this.errorCount = errorCount;
    this.warningCount = warningCount;
    this.issues = issues;
    this.excludedIssues = excludedIssues;
  }

  public int getErrorsCount() {
    return errorCount;
  }

  public int getWarningsCount() {
    return warningCount;
  }

  public List<W3cHtml5Issue> getIssues() {
    return issues;
  }

  public List<W3cHtml5Issue> getExcludedIssues() {
    return excludedIssues;
  }
}
