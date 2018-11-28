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
package com.cognifide.aet.job.common.collectors.accessibility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

class AccessibilityIssueMarkupFinder {

  private final String html;
  private final List<AccessibilityIssue> issues;

  AccessibilityIssueMarkupFinder(String html, String json) {
    this.html = html;
    this.issues = parseIssues(json);
  }

  List<AccessibilityIssue> get() {
    fetchElementPositions();
    return issues;
  }

  private List<AccessibilityIssue> parseIssues(String json) {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(AccessibilityIssue.class, new AccessibilityIssueDeserializer())
        .create();
    Type list = new TypeToken<List<AccessibilityIssue>>() {
    }.getType();
    return gson.fromJson(json, list);
  }

  private void fetchElementPositions() {
    Map<AccessibilityIssue, Integer> lastOccurrences = new HashMap<>();
    for (AccessibilityIssue issue : issues) {
      int searchStartIndex = getSearchStartIndex(lastOccurrences, issue);
      int indexOfElement = html.indexOf(issue.getElementString(), searchStartIndex);
      if (indexOfElement >= 0) {
        String beforeOccurrence = html.substring(0, indexOfElement);
        int lineBreaks = StringUtils.countMatches(beforeOccurrence, "\n");
        int columnNumber = getColumnNumber(lineBreaks, beforeOccurrence);

        issue.setLineNumber(lineBreaks + 1);
        issue.setColumnNumber(columnNumber + 1);
        lastOccurrences.put(issue, indexOfElement);
      }
    }
  }

  private int getSearchStartIndex(Map<AccessibilityIssue, Integer> lastOccurrences,
      AccessibilityIssue issue) {
    int startIndex = 0;
    if (lastOccurrences.containsKey(issue)) {
      startIndex = lastOccurrences.get(issue) + issue.getElementString().length();
    }
    return startIndex;
  }

  private int getColumnNumber(int lineBreaks, String beforeOccurrence) {
    int columnNumber;
    if (lineBreaks > 0) {
      int indexOfLastLineBreak = beforeOccurrence.lastIndexOf('\n');
      columnNumber = beforeOccurrence.substring(indexOfLastLineBreak).length();
    } else {
      columnNumber = beforeOccurrence.length();
    }
    return columnNumber;
  }
}
