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
package com.cognifide.aet.job.common.datafilters.w3chtml5filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5ComparatorResult;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5Issue;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5IssueType;
import com.cognifide.aet.job.common.datafilters.w3chtmlfilter.W3cHtml5IssuesFilter;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class W3cHtml5IssuesFilterTest {

  public static final String PARAM_LINE = "line";

  public static final String PARAM_COLUMN = "column";

  public static final String PARAM_MESSAGE = "message";

  public static final String PARAM_MESSAGE_PATTERN = "messagePattern";

  private W3cHtml5IssuesFilter tested;

  Map<String, String> params;

  @Before
  public void setUp() {
    tested = new W3cHtml5IssuesFilter();
    params = new HashMap<>();
  }

  @Test
  public void setParameters_whenAllCorect_thenNoValidationErrors() throws ParametersException {
    params = createParams("1", "3", "Some message...", null);

    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_whenAllEmpty_thenValidationErrors() throws ParametersException {
    tested.setParameters(params);

  }

  @Test(expected = ParametersException.class)
  public void setParameters_whenLineNonNumeric_thenValidationErrors()
      throws ParametersException, ProcessingException {
    params = createParams("xyz", "3", "Some message...", null);
    tested.setParameters(params);

  }

  @Test
  public void modifyData_whenLineMatch_issueIsRemoved()
      throws ParametersException, ProcessingException {
    params = createParams("1", null, null, null);
    tested.setParameters(params);
    W3cHtml5ComparatorResult data = prepareComparatorResult(1, 0, "Message...",
        W3cHtml5IssueType.ERR);

    W3cHtml5ComparatorResult processedData = tested.modifyData(data);

    assertThat(processedData.getErrorsCount(), is(0));
    assertThat(processedData.getIssues().size(), is(0));
    assertThat(processedData.getExcludedIssues().size(), is(1));
  }

  @Test
  public void modifyData_whenColumnMatch_issueIsRemoved()
      throws ParametersException, ProcessingException {
    params = createParams(null, "3", null, null);
    tested.setParameters(params);
    W3cHtml5ComparatorResult data = prepareComparatorResult(0, 3, "Message...",
        W3cHtml5IssueType.WARN);

    W3cHtml5ComparatorResult processedData = tested.modifyData(data);

    assertThat(processedData.getWarningsCount(), is(0));
    assertThat(processedData.getIssues().size(), is(0));
    assertThat(processedData.getExcludedIssues().size(), is(1));
  }

  @Test
  public void modifyData_whenMessageMatch_issueIsRemoved()
      throws ParametersException, ProcessingException {
    params = createParams(null, null, "^Message.*", null);
    tested.setParameters(params);
    W3cHtml5ComparatorResult data = prepareComparatorResult(0, 0, "Message...",
        W3cHtml5IssueType.WARN);

    W3cHtml5ComparatorResult processedData = tested.modifyData(data);

    assertThat(processedData.getWarningsCount(), is(0));
    assertThat(processedData.getIssues().size(), is(0));
    assertThat(processedData.getExcludedIssues().size(), is(1));
  }

  @Test
  public void modifyData_whenIssuesExcluded_expectTwoIssuesRemoved()
      throws ParametersException, ProcessingException {
    params = createParams("1", null, null, null);
    tested.setParameters(params);
    List<W3cHtml5Issue> issues = Lists.newArrayList(
        new W3cHtml5Issue(1, 2, "Message", null, null, null, null, W3cHtml5IssueType.WARN),
        new W3cHtml5Issue(1, 3, "Different message", null, null, null, null,
            W3cHtml5IssueType.ERR));
    W3cHtml5ComparatorResult data = prepareComparatorResult(issues,
        Lists.<W3cHtml5Issue>newArrayList());

    final W3cHtml5ComparatorResult processedData = tested.modifyData(data);
    assertThat(processedData.getExcludedIssues().size(), is(2));
  }

  @Test
  public void modifyData_whenPartiallyMatch_issueIsNotRemoved()
      throws ParametersException, ProcessingException {
    params = createParams("1", "3", "Message", null);
    tested.setParameters(params);
    W3cHtml5ComparatorResult data = prepareComparatorResult(2, 3, "Message...",
        W3cHtml5IssueType.WARN);

    W3cHtml5ComparatorResult processedData = tested.modifyData(data);

    assertThat(processedData.getWarningsCount(), is(1));
    assertThat(processedData.getIssues().size(), is(1));
    assertThat(processedData.getExcludedIssues().size(), is(0));
  }

  @Test
  public void modifyData_whenManyCalls_excludedIssuesArePreserved()
      throws ParametersException, ProcessingException {
    params = createParams("1", "3", "Message", null);
    tested.setParameters(params);
    List<W3cHtml5Issue> issues = Lists.newArrayList(
        new W3cHtml5Issue(2, 3, "Message...", null, null, null, null, W3cHtml5IssueType.ERR),
        new W3cHtml5Issue(4, 3, "Warning...", null, null, null, null, W3cHtml5IssueType.WARN));
    List<W3cHtml5Issue> excludedIssues = Lists.newArrayList(
        new W3cHtml5Issue(2, 3, "Message...", null, null, null, null, W3cHtml5IssueType.WARN));
    W3cHtml5ComparatorResult data = prepareComparatorResult(issues, excludedIssues);

    // first call
    data = tested.modifyData(data);

    assertThat(data.getWarningsCount(), is(1));
    assertThat(data.getErrorsCount(), is(1));
    assertThat(data.getIssues().size(), is(2));
    assertThat(data.getExcludedIssues().size(), is(1));

    params = createParams("2", "3", "^Message.*$", null);
    tested.setParameters(params);

    // second call
    data = tested.modifyData(data);

    assertThat(data.getWarningsCount(), is(1));
    assertThat(data.getErrorsCount(), is(0));
    assertThat(data.getIssues().size(), is(1));
    assertThat(data.getExcludedIssues().size(), is(2));
  }

  private Map<String, String> createParams(String line, String column, String messagePattern,
      String plainMassage) {
    Map<String, String> params = new HashMap<>();
    if (StringUtils.isNotBlank(line)) {
      params.put(PARAM_LINE, line);
    }
    if (StringUtils.isNotBlank(column)) {
      params.put(PARAM_COLUMN, column);
    }
    if (StringUtils.isNotBlank(messagePattern)) {
      params.put(PARAM_MESSAGE_PATTERN, messagePattern);
    }
    if (StringUtils.isNotBlank(plainMassage)) {
      params.put(PARAM_MESSAGE, plainMassage);
    }
    return params;
  }

  private W3cHtml5ComparatorResult prepareComparatorResult(int line, int column, String message,
      W3cHtml5IssueType type) {
    List<W3cHtml5Issue> issues = new ArrayList<>();
    issues.add(new W3cHtml5Issue(line, column, message, null, null, null, null, type));
    List<W3cHtml5Issue> excludedIssues = new ArrayList<>();
    return prepareComparatorResult(issues, excludedIssues);
  }

  private W3cHtml5ComparatorResult prepareComparatorResult(List<W3cHtml5Issue> issues,
      List<W3cHtml5Issue> excludedIssues) {
    int errorCount = 0;
    int warningCount = 0;
    for (W3cHtml5Issue issue : issues) {
      W3cHtml5IssueType type = issue.getIssueType();
      if (W3cHtml5IssueType.ERR.equals(type)) {
        errorCount++;
      }
      if (W3cHtml5IssueType.WARN.equals(type) || W3cHtml5IssueType.INFO.equals(type)) {
        warningCount++;
      }
    }

    return new W3cHtml5ComparatorResult(errorCount, warningCount, issues, excludedIssues);
  }

}
