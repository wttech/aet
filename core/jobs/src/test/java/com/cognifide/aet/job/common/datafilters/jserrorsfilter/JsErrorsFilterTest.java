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
package com.cognifide.aet.job.common.datafilters.jserrorsfilter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNot.not;

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

  private static final String PARAM_ERROR = "errorPattern";

  private static final String PARAM_SOURCE = "sourcePattern";

  private static final String PARAM_LINE = "line";

  private static final String PARAM_ERROR_VALUE = "^Error message.*";

  private static final String PARAM_SOURCE_VALUE = "Source Value";

  private static final String PARAM_LINE_VALUE = "10";

  private static final String PARAM_LINE_VALUE_NAN = "not a number";

  private static final String INFO_PATTERN = JsErrorsFilter.NAME + " DataModifier with parameters: "
      + PARAM_SOURCE + ": %s " + PARAM_ERROR + ": %s " + PARAM_LINE + ": %s";

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
    params = getParams(PARAM_LINE_VALUE, PARAM_SOURCE_VALUE, PARAM_ERROR_VALUE);
    // no exceptions should occur
    tested.setParameters(params);
    assertThat(tested.getInfo(),
        is(String.format(INFO_PATTERN, PARAM_SOURCE_VALUE, PARAM_ERROR_VALUE, PARAM_LINE_VALUE)));
  }

  @Test
  public void setParametersTest_onlyOneSet() throws ParametersException {
    params = getParams(null, PARAM_SOURCE_VALUE, null);
    tested.setParameters(params);
    assertThat(tested.getInfo(), is(String.format(INFO_PATTERN, PARAM_SOURCE_VALUE, "null", "null")));
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_allEmpty() throws ParametersException {
    params = getParams(null, null, null);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_lineNotANumber() throws ParametersException {
    params = getParams(PARAM_LINE_VALUE_NAN, null, null);
    tested.setParameters(params);
  }

  @Test
  public void modifyDataTest_filterByErrorMessagePattern() throws ProcessingException, ParametersException {
    params = getParams(null, null, PARAM_ERROR_VALUE);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    assertThat(result, hasSize(4));
    assertThat(result, hasItems(errorLogsList.get(2), errorLogsList.get(3), errorLogsList.get(4)));
  }

  @Test
  public void modifyDataTest_filterBySource() throws ProcessingException, ParametersException {
    params = getParams(null, PARAM_SOURCE_VALUE, null);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    assertThat(result, hasSize(6));
    assertThat(result, not(hasItems(errorLogsList.get(6))));
  }

  @Test
  public void modifyDataTest_filterByLine() throws ProcessingException, ParametersException {
    params = getParams(PARAM_LINE_VALUE, null, null);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    assertThat(result, hasSize(4));
    assertThat(result, not(hasItems(errorLogsList.get(6), errorLogsList.get(5), errorLogsList.get(3))));
  }

  @Test
  public void modifyDataTest_filterByAll() throws ProcessingException, ParametersException {
    params = getParams(PARAM_LINE_VALUE, PARAM_SOURCE_VALUE, PARAM_ERROR_VALUE);
    tested.setParameters(params);
    Set<JsErrorLog> result = tested.modifyData(data);
    assertThat(result, hasSize(6));
    assertThat(result, not(hasItems(errorLogsList.get(6))));
  }

  private void initErrorLogs() {
    errorLogsList = Lists.newArrayList();
    errorLogsList.add(new JsErrorLog("error message", "source", 1));
    errorLogsList.add(new JsErrorLog("Error message 2", "source", 2));
    errorLogsList.add(new JsErrorLog("this should stay here", "this should stay here", 3));
    errorLogsList.add(new JsErrorLog("Another message", "source", 10));
    errorLogsList.add(new JsErrorLog("Another message2", "source", 1));
    errorLogsList.add(new JsErrorLog("Error message", PARAM_SOURCE_VALUE.toLowerCase(), 10));
    errorLogsList.add(new JsErrorLog("Error message random string", PARAM_SOURCE_VALUE, 10));
  }


  private Map<String, String> getParams(String line, String source, String errorPattern) {
    Map<String, String> params = new HashMap<>();
    if (StringUtils.isNotBlank(line)) {
      params.put(PARAM_LINE, line);
    }
    if (StringUtils.isNotBlank(source)) {
      params.put(PARAM_SOURCE, source);
    }
    if (StringUtils.isNotBlank(errorPattern)) {
      params.put(PARAM_ERROR, errorPattern);
    }
    return params;
  }
}


