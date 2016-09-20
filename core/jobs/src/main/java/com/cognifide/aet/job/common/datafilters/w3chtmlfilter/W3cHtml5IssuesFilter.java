/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.datafilters.w3chtmlfilter;

import com.cognifide.aet.job.api.datafilter.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5ComparatorResult;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5Issue;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5IssueComparator;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5IssueType;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class W3cHtml5IssuesFilter extends AbstractDataModifierJob<W3cHtml5ComparatorResult> {

  public static final String NAME = "w3c-filter";

  private static final String PARAM_MESSAGE = "message";

  private static final String PARAM_LINE = "line";

  private static final String PARAM_COLUMN = "column";
  private static final Comparator<? super W3cHtml5Issue> W3C_ISSUE_COMPARATOR = new W3cHtml5IssueComparator();

  private String message;

  private int line;

  private int column;

  @Override
  public W3cHtml5ComparatorResult modifyData(W3cHtml5ComparatorResult data) throws ProcessingException {
    int errorsCount = data.getErrorsCount();
    int warningsCount = data.getWarningsCount();
    List<W3cHtml5Issue> excluded = data.getExcludedIssues();
    List<W3cHtml5Issue> notExcluded = new ArrayList<>();

    for (W3cHtml5Issue issue : data.getIssues()) {
      if (match(issue)) {
        excluded.add(issue);
        if (W3cHtml5IssueType.ERR.equals(issue.getIssueType())) {
          errorsCount--;
        }
        if (W3cHtml5IssueType.WARN.equals(issue.getIssueType())
                || W3cHtml5IssueType.INFO.equals(issue.getIssueType())) {
          warningsCount--;
        }
      } else {
        notExcluded.add(issue);
      }
    }
    Collections.sort(excluded, W3C_ISSUE_COMPARATOR);
    return new W3cHtml5ComparatorResult(errorsCount, warningsCount, notExcluded, excluded);
  }

  private boolean match(W3cHtml5Issue issue) {
    final boolean messageNotSetOrIgnored =
            StringUtils.isBlank(message) || StringUtils.startsWithIgnoreCase(issue.getMessage(), message);
    final boolean lineNotSetOrIgnored = line == 0 || line == issue.getLine();
    final boolean columnNotSetOrIgnored = column == 0 || column == issue.getColumn();
    return messageNotSetOrIgnored && lineNotSetOrIgnored && columnNotSetOrIgnored;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (params.containsKey(PARAM_MESSAGE)) {
      message = params.get(PARAM_MESSAGE);
    }
    line = getNumericParam(PARAM_LINE, params);
    column = getNumericParam(PARAM_COLUMN, params);

    if (isParametersEmpty()) {
      throw new ParametersException("Parameters for w3c filter cannot be empty.");
    }
  }

  private int getNumericParam(String paramName, Map<String, String> params) throws ParametersException {
    int paramValue = 0;
    if (params.containsKey(paramName)) {
      try {
        paramValue = Integer.valueOf(params.get(paramName));
      } catch (NumberFormatException e) {
        throw new ParametersException(paramName + " parameter for w3c filter should be numeric.", e);
      }
    }
    return paramValue;
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: " + PARAM_MESSAGE + ": '" + message + "' " + PARAM_LINE
            + ": " + line + " " + PARAM_COLUMN + ": " + column;
  }

  private boolean isParametersEmpty() {
    return line == 0 && column == 0 && StringUtils.isBlank(message);
  }
}
