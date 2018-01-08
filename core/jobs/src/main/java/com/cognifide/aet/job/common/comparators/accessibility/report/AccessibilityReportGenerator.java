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
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue.IssueType;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AccessibilityReportGenerator {

  private final AccessibilityReportConfiguration configuration;

  private List<AccessibilityIssue> nonExcludedIssues;

  private List<AccessibilityIssue> excludedIssues;

  private int errorCount;

  private int warningCount;

  private int noticeCount;

  public AccessibilityReportGenerator(AccessibilityReportConfiguration configuration) {
    this.configuration = configuration;
  }

  public AccessibilityReport generate(List<AccessibilityIssue> notExcludedIssues,
      List<AccessibilityIssue> excludedIssues) {

    this.nonExcludedIssues = notExcludedIssues;
    this.excludedIssues = excludedIssues;
    invoke();

    return new AccessibilityReport(notExcludedIssues, excludedIssues, errorCount, warningCount,
        noticeCount,
        configuration);
  }

  private void sortIssues(List<AccessibilityIssue> issues) {
    Collections.sort(issues, new Comparator<AccessibilityIssue>() {
      @Override
      public int compare(AccessibilityIssue ai1, AccessibilityIssue ai2) {
        return ai1.getType().compareTo(ai2.getType());
      }
    });
  }

  private void invoke() {
    Iterable<AccessibilityIssue> errors = Iterables
        .filter(nonExcludedIssues, new IssueTypePredicate(IssueType.ERROR));
    errorCount = Iterables.size(errors);
    warningCount = filterAndCount(!configuration.isShowWarning(), IssueType.WARN);
    noticeCount = filterAndCount(!configuration.isShowNotice(), IssueType.NOTICE);

    sortIssues(nonExcludedIssues);
    sortIssues(excludedIssues);
  }

  private int filterAndCount(boolean filter, IssueType type) {
    int result = 0;
    Iterable<AccessibilityIssue> issues = Iterables
        .filter(nonExcludedIssues, new IssueTypePredicate(type));
    if (filter) {
      Iterables.removeAll(nonExcludedIssues, Lists.newArrayList(issues));
      Iterable<AccessibilityIssue> excluded = Iterables
          .filter(excludedIssues, new IssueTypePredicate(type));
      Iterables.removeAll(excludedIssues, Lists.newArrayList(excluded));
    } else {
      result = Iterables.size(issues);
    }
    return result;
  }

  private static final class IssueTypePredicate implements Predicate<AccessibilityIssue> {

    private final IssueType type;

    private IssueTypePredicate(IssueType type) {
      this.type = type;
    }

    @Override
    public boolean apply(AccessibilityIssue issue) {
      return type == issue.getType();
    }
  }
}
