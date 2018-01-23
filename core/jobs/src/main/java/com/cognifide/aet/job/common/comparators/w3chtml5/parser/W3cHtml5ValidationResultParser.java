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
package com.cognifide.aet.job.common.comparators.w3chtml5.parser;

import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5ComparatorResult;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5Issue;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5IssueType;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class W3cHtml5ValidationResultParser {

  public W3cHtml5ComparatorResult parse(String json) {

    Gson gson = new GsonBuilder()
        .registerTypeAdapter(W3cHtml5Issue.class, new W3cHtml5IssueDeserializer()).create();

    JsonArray messages = new JsonParser().parse(json).getAsJsonObject().getAsJsonArray("messages");
    Type list = new TypeToken<List<W3cHtml5Issue>>() {
    }.getType();
    List<W3cHtml5Issue> issues = gson.fromJson(messages, list);

    IssuesUtils utils = new IssuesUtils(issues).invoke();
    int errorCount = utils.getErrorCount();
    int warningCount = utils.getWarningCount();

    return new W3cHtml5ComparatorResult(errorCount, warningCount, issues,
        new ArrayList<W3cHtml5Issue>());
  }

  private static final class IssuesUtils {

    private List<W3cHtml5Issue> issues;

    private int errorCount;

    private int warningCount;

    private IssuesUtils(List<W3cHtml5Issue> issues) {
      this.issues = issues;
    }

    private int getErrorCount() {
      return errorCount;
    }

    private int getWarningCount() {
      return warningCount;
    }

    private IssuesUtils invoke() {
      errorCount = Iterables
          .size(Iterables.filter(issues, new IssueTypePredicate(W3cHtml5IssueType.ERR)));
      warningCount = Iterables
          .size(Iterables.filter(issues, new IssueTypePredicate(W3cHtml5IssueType.WARN)));

      Collections.sort(issues, new Comparator<W3cHtml5Issue>() {
        @Override
        public int compare(W3cHtml5Issue i1, W3cHtml5Issue i2) {
          return i1.getIssueType().compareTo(i2.getIssueType());
        }
      });
      return this;
    }
  }

  private static final class IssueTypePredicate implements Predicate<W3cHtml5Issue> {

    private final W3cHtml5IssueType issueType;

    private IssueTypePredicate(W3cHtml5IssueType issueType) {
      this.issueType = issueType;
    }

    @Override
    public boolean apply(W3cHtml5Issue issue) {
      return issueType == issue.getIssueType();
    }
  }
}
