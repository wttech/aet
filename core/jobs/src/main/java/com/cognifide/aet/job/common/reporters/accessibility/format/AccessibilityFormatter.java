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
package com.cognifide.aet.job.common.reporters.accessibility.format;

import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.reporters.Bug;
import com.cognifide.aet.job.common.reporters.accessibility.AccessibilityCode;
import com.cognifide.aet.job.common.reporters.accessibility.format.bug.AccessibilityBug;
import com.cognifide.aet.job.common.reporters.accessibility.format.bug.Case;
import com.cognifide.aet.job.common.reporters.accessibility.format.bug.Occurence;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class AccessibilityFormatter {

  public List<Bug> format(List<AccessibilityIssue> issues) {
    if (CollectionUtils.isEmpty(issues)) {
      return Collections.emptyList();
    }

    Map<AccessibilityCode, List<AccessibilityIssue>> issuesByCode = issues.stream()
        .collect(Collectors.groupingBy(issue -> new AccessibilityCode(issue.getCode())));

    return issuesByCode.entrySet().stream()
        .map(this::createBug)
        .sorted(Comparator.comparing(Bug::getSummary))
        .collect(Collectors.toList());
  }

  private Bug createBug(Entry<AccessibilityCode, List<AccessibilityIssue>> issuesForCode) {
    AccessibilityCode code = issuesForCode.getKey();
    List<AccessibilityIssue> issues = issuesForCode.getValue();

    return new AccessibilityBug(code, getCases(issues));
  }

  private List<Case> getCases(List<AccessibilityIssue> issues) {
    Map<String, List<AccessibilityIssue>> issuesByMessage = issues.stream()
        .collect(Collectors.groupingBy(AccessibilityIssue::getMessage));

    return issuesByMessage.entrySet().stream()
        .map(entry -> {
          String message = entry.getKey();
          List<AccessibilityIssue> issuesForMessage = entry.getValue();
          return new Case(message, getOccurrences(issuesForMessage));
        })
        .sorted(Comparator.comparing(Case::getErrorMessage))
        .collect(Collectors.toList());
  }

  private List<Occurence> getOccurrences(List<AccessibilityIssue> issues) {
    Map<String, List<AccessibilityIssue>> issuesByUrl = issues.stream()
        .collect(Collectors.groupingBy(AccessibilityIssue::getUrl));

    return issuesByUrl.entrySet().stream()
        .map(entry -> {
          String url = entry.getKey();
          List<AccessibilityIssue> issuesForUrl = entry.getValue();
          return new Occurence(url, getLineNumbers(issuesForUrl));
        })
        .sorted(Comparator.comparing(Occurence::getUrl))
        .collect(Collectors.toList());
  }

  private List<Integer> getLineNumbers(List<AccessibilityIssue> issuesForUrl) {
    return issuesForUrl.stream()
        .map(AccessibilityIssue::getLineNumber)
        .sorted()
        .collect(Collectors.toList());
  }
}
