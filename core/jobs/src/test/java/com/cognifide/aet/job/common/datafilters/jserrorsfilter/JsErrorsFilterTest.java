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
package com.cognifide.aet.job.common.datafilters.jserrorsfilter;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JsErrorsFilterTest {

  private JsErrorsFilter tested;

  private static final String PARAM_ERROR = "error";

  private static final String PARAM_SOURCE = "source";

  private static final String PARAM_LINE = "line";

  private static final String PARAM_ERROR_VALUE = "Error message";

  private static final String PARAM_SOURCE_VALUE = "Source Value";

  private static final String PARAM_SOURCE_VALUE_SHORT = "source";

  private static final String PARAM_ERROR_PATTERN = "errorPattern";

  private static final String PARAM_ERROR_PATTERN_VALUE = "^Error message.*$";

  private static final String PARAM_LINE_VALUE = "10";

  private static final String PARAM_LINE_VALUE_NAN = "not a number";

  private static final String INFO_PATTERN = JsErrorsFilter.NAME + " DataModifier with parameters: "
      + PARAM_ERROR_PATTERN + ": %s "
      + PARAM_SOURCE + ": %s "
      + PARAM_LINE + ": %s";
  @Mock
  private Set<JsErrorLog> data;

  private Map<String, String> params;


  private List<JsErrorLog> errorLogsList;

  @Before
  public void setUp() {
    tested = new JsErrorsFilter();
    initErrorLogs();
    data = Sets.newHashSet(errorLogsList);
  }

  @Test
  public void setParametersTest() throws ParametersException {
    params = createParams(PARAM_LINE_VALUE, PARAM_ERROR_PATTERN_VALUE, PARAM_SOURCE_VALUE,
        PARAM_ERROR_VALUE);
    // no exceptions should occur
    tested.setParameters(params);
  }

  @Test
  public void setParametersTest_onlyOneSet() throws ParametersException {
    params = createParams(null, null, PARAM_SOURCE_VALUE, null);
    tested.setParameters(params);
    String expected = String.format(INFO_PATTERN, null, PARAM_SOURCE_VALUE, null, null);
    assertThat(tested.getInfo(), is(expected));
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_allEmpty() throws ParametersException {
    params = createParams(null, null, null, null);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_lineNotANumber() throws ParametersException {
    params = createParams(PARAM_LINE_VALUE_NAN, null, null, null);
    tested.setParameters(params);
  }

  @Test
  public void modifyDataTest_excludeByErrorMessage()
      throws ParametersException {
    params = createParams(null, null, null, PARAM_ERROR_VALUE);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    Set<JsErrorLog> ignored = result.stream().filter(JsErrorLog::isIgnored).collect(toSet());

    assertSize(result, data.size());
    assertSize(ignored, 1);
  }

  @Test
  public void modifyDataTest_filterByErrorMessagePattern()
      throws ProcessingException, ParametersException {
    params = createParams(null, PARAM_ERROR_PATTERN_VALUE, null, null);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    Set<JsErrorLog> ignored = result.stream().filter(JsErrorLog::isIgnored).collect(toSet());

    assertSize(result, data.size());
    assertSize(ignored, 3);
  }

  @Test
  public void modifyDataTest_filterBySource() throws ProcessingException, ParametersException {
    params = createParams(null, null, PARAM_SOURCE_VALUE_SHORT, null);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    Set<JsErrorLog> ignored = result.stream().filter(JsErrorLog::isIgnored).collect(toSet());

    assertSize(result, data.size());
    assertSize(ignored, 3);
  }

  @Test
  public void modifyDataTest_filterByLine() throws ProcessingException, ParametersException {
    params = createParams(PARAM_LINE_VALUE, null, null, null);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    Set<JsErrorLog> ignored = result.stream().filter(JsErrorLog::isIgnored).collect(toSet());

    assertSize(result, data.size());
    assertSize(ignored, 3);
  }

  @Test
  public void modifyDataTest_excludeByLineAndErrorMessagePattern()
      throws ProcessingException, ParametersException {
    params = createParams(PARAM_LINE_VALUE, PARAM_ERROR_PATTERN_VALUE, null, null);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    Set<JsErrorLog> ignored = result.stream().filter(JsErrorLog::isIgnored).collect(toSet());

    assertSize(result, data.size());
    assertSize(ignored, 2);
  }

  private void initErrorLogs() {
    errorLogsList = Lists.newArrayList();
    errorLogsList.add(new JsErrorLog("error message", "source", 1));
    errorLogsList.add(new JsErrorLog("Error message 2", "SOURCE", 2));
    errorLogsList.add(new JsErrorLog("this should stay here", "this should stay here", 3));
    errorLogsList.add(new JsErrorLog("Another message", "prefixed source", 10));
    errorLogsList.add(new JsErrorLog("Another message2", "source", 1));
    errorLogsList.add(new JsErrorLog("Error message", PARAM_SOURCE_VALUE.toLowerCase(), 10));
    errorLogsList.add(new JsErrorLog("Error message random string", PARAM_SOURCE_VALUE, 10));
  }

  private void assertSize(Set<JsErrorLog> jsErrors, int expectedSize) {
    assertThat(jsErrors, hasSize(expectedSize));
  }

  private Map<String, String> createParams(String line, String errorPattern, String source,
      String error) {
    Map<String, String> params = new HashMap<>();
    if (StringUtils.isNotBlank(line)) {
      params.put(PARAM_LINE, line);
    }
    if (StringUtils.isNotBlank(errorPattern)) {
      params.put(PARAM_ERROR_PATTERN, errorPattern);
    }
    if (StringUtils.isNotBlank(source)) {
      params.put(PARAM_SOURCE, source);
    }
    if (StringUtils.isNotBlank(error)) {
      params.put(PARAM_ERROR, error);
    }
    return params;
  }
}


