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
package com.cognifide.aet.job.common.datafilters.statuscodesfilter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCode;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCodesCollectorResult;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public abstract class StatusCodesFilterTestBase<T extends StatusCodesFilter> {

  protected static final String PARAM_URL = "url";

  protected static final String PARAM_PATTERN = "pattern";

  protected static final String PARAM_URL_VALUE = "http://test/somejsp.jsp";

  protected static final String PARAM_URL_ANOTHER_VALUE = "http://test/somecss.css";

  protected static final String NOT_FULL_URL_PARAM = "/somejsp.jsp";

  protected static final String PARAM_PATTERN_VALUE = "^.*jsp$";

  protected static final String PARAM_PATTERN_INVALID_VALUE = "*";

  protected String infoPattern;

  protected T tested;

  protected Map<String, String> params;

  @Mock
  protected StatusCodesCollectorResult data;

  protected List<StatusCode> statusCodesList;

  @Before
  public void setUp() {
    tested = getStatusCodeFilterInstance();
    infoPattern = getName() + " DataModifier with parameters: " + PARAM_URL + ": %s " + PARAM_PATTERN + ": %s";
    initStatusCodesList();
    when(data.getStatusCodes()).thenReturn(statusCodesList);
  }

  @Test
  public void setParameters_UrlAndPatternpAreProvided_ExpectValidModifierInfo() throws ParametersException {
    params = createParams(PARAM_URL_VALUE, PARAM_PATTERN_VALUE);
    tested.setParameters(params);
    assertThat(tested.getInfo(), is(String.format(infoPattern, PARAM_URL_VALUE, PARAM_PATTERN_VALUE)));
  }

  @Test
  public void setParameters_OnlyUrlIsProvided_ExpectModifierInfoWithNullPatternValue() throws ParametersException {
    params = createParams(PARAM_URL_VALUE, null);
    tested.setParameters(params);
    assertThat(tested.getInfo(), is(String.format(infoPattern, PARAM_URL_VALUE, "null")));
  }

  @Test
  public void setParameters_OnlyPatternIsProvided_ExpectModifierInfoWithNullUrlValue() throws ParametersException {
    params = createParams(null, PARAM_PATTERN_VALUE);
    tested.setParameters(params);
    assertThat(tested.getInfo(), is(String.format(infoPattern, "null", PARAM_PATTERN_VALUE)));
  }

  @Test(expected = ParametersException.class)
  public void setParameters_AllParametersAreEmpty_ExceptionIsThrown() throws ParametersException {
    params = createParams(null, null);
    tested.setParameters(params);

  }

  @Test(expected = ParametersException.class)
  public void setParameters_PatternIsInvalid_ExceptionIsThrown() throws ParametersException {
    params = createParams(null, PARAM_PATTERN_INVALID_VALUE);
    tested.setParameters(params);
  }

  protected void initStatusCodesList() {
    statusCodesList = Lists.newArrayList();
    statusCodesList.add(new StatusCode(200, "http://test/somejsp.jsp"));
    statusCodesList.add(new StatusCode(200, "http://test/somecss.css"));
    statusCodesList.add(new StatusCode(404, "http://anotherurl.jsp"));
  }

  protected void excludeAllStatusCodes() {
    for (StatusCode statusCode : statusCodesList) {
      statusCode.setExcluded(true);
    }
  }


  protected abstract T getStatusCodeFilterInstance();

  protected String getName() {
    return tested.getName();
  }


  protected Map<String, String> createParams(String url, String pattern) {
    Map<String, String> params = new HashMap<>();
    if (StringUtils.isNotBlank(url)) {
      params.put(PARAM_URL, url);
    }
    if (StringUtils.isNotBlank(pattern)) {
      params.put(PARAM_PATTERN, pattern);
    }
    return params;
  }
}
