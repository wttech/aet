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
package com.cognifide.aet.job.common.datafilters.accessibilityfilter;

import static com.cognifide.aet.job.common.datafilters.accessibilityfilter.AccessibilityFilter.PARAM_COLUMN;
import static com.cognifide.aet.job.common.datafilters.accessibilityfilter.AccessibilityFilter.PARAM_ERROR;
import static com.cognifide.aet.job.common.datafilters.accessibilityfilter.AccessibilityFilter.PARAM_ERROR_PATTERN;
import static com.cognifide.aet.job.common.datafilters.accessibilityfilter.AccessibilityFilter.PARAM_LINE;
import static com.cognifide.aet.job.common.datafilters.accessibilityfilter.AccessibilityFilter.PARAM_MARKUP_CSS_SELECTOR;
import static com.cognifide.aet.job.common.datafilters.accessibilityfilter.AccessibilityFilter.PARAM_PRINCIPLE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.google.common.collect.ImmutableMap;
import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
@Configure(separator = "\\|")
public class AccessibilityFilterTest {

  private AccessibilityFilter accessibilityFilter;

  private List<AccessibilityIssue> issues;

  private AccessibilityIssue issue;

  @Before
  public void setUp() {
    accessibilityFilter = new AccessibilityFilter();

    issue = mock(AccessibilityIssue.class);
    issues = Collections.singletonList(issue);
  }


  @TestWith({
      "P1 | P1 | true",
      "P1 | P2 | false",
  })
  public void modifyData_principleProvided(String errorPrinciple, String principeParam,
      boolean exclude) throws ProcessingException, ParametersException {
    Map<String, String> params = ImmutableMap.of(PARAM_PRINCIPLE, principeParam);
    when(issue.getCode()).thenReturn(errorPrinciple);

    setParamsAndValidate(params, exclude);
  }


  @TestWith({
      "Lorem ipsum | .*ipsum.* | true",
      "Lorem ipsum | .*dolor.* | false",
  })
  public void modifyData_errorPatternProvided(String errorMessage, String errorPattern,
      boolean exclude) throws ProcessingException, ParametersException {
    Map<String, String> params = ImmutableMap.of(PARAM_ERROR_PATTERN, errorPattern);
    when(issue.getMessage()).thenReturn(errorMessage);

    setParamsAndValidate(params, exclude);
  }

  @TestWith({
      "Lorem ipsum | Lorem ipsum       | true",
      "Lorem ipsum | Lorem             | false",
      "Lorem ipsum | Lorem IPSUM       | false",
      "Lorem ipsum | .*ipsum.*         | false",
      "Lorem ipsum | Lorem Ipsum dolor | false"
  })
  public void modifyData_errorProvided(String errorMessage, String errorParam,
      boolean exclude) throws ProcessingException, ParametersException {
    Map<String, String> params = ImmutableMap.of(PARAM_ERROR, errorParam);
    when(issue.getMessage()).thenReturn(errorMessage);

    setParamsAndValidate(params, exclude);
  }

  @TestWith({
      "10 | 20 | 10   | 20   | true",
      "10 | 20 | 10   | null | true",
      "10 | 20 | null | 20   | true",
      "10 | 20 | 10   | null | true",
      "10 | 20 | 10   | 21   | false",
      "10 | 20 | 11   | 20   | false",
  })
  public void modifyData_positionProvided(Integer line, Integer column, Integer lineParam,
      Integer columnParam, boolean exclude) throws ProcessingException, ParametersException {
    Map<String, String> params = new HashMap<>();
    if (lineParam != null) {
      params.put(PARAM_LINE, lineParam.toString());
    }
    if (columnParam != null) {
      params.put(PARAM_COLUMN, columnParam.toString());
    }
    when(issue.getLineNumber()).thenReturn(line);
    when(issue.getColumnNumber()).thenReturn(column);
    
    setParamsAndValidate(params, exclude);
  }

  @TestWith({
      "<form><input type='text' class='form-control'></form> | form              | true",
      "<form><input type='text' class='form-control'></form> | [type=text]       | true",
      "<form><input type='text' class='form-control'></form> | .form-control     | true",
      "<form><input type='text' class='form-control'></form> | form.form-control | false",
      "<form><input type='text' class='form-control'></form> | .foo              | false",
  })
  public void modifyData_markupCssSelectorProvided(String markup, String markupCss,
      boolean exclude) throws ParametersException, ProcessingException {
    Map<String, String> params = ImmutableMap.of(PARAM_MARKUP_CSS_SELECTOR, markupCss);
    when(issue.getElementString()).thenReturn(markup);
    setParamsAndValidate(params, exclude);
  }


  private void setParamsAndValidate(Map<String, String> params, boolean exclude)
      throws ParametersException, ProcessingException {
    accessibilityFilter.setParameters(params);
    accessibilityFilter.modifyData(issues);
    if (exclude) {
      verify(issue).exclude();
    } else {
      verify(issue, never()).exclude();
    }
  }
}

