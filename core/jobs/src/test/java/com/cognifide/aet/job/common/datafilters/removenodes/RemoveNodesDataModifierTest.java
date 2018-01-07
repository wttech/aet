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
package com.cognifide.aet.job.common.datafilters.removenodes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RemoveNodesDataModifierTest {

  private RemoveNodesDataModifier tested;

  private static final String PARAM_XPATH = "xpath";

  private static final String PARAM_XPATH_VALUE = "//*[@id='toRemove']";

  private static final String PARAM_XPATH_INVALID_VALUE = "==";

  private static final String PARAM_XPATH_NOT_MATCHING_VALUE = "//*[@id='nonExistingId']";

  private static final String SOURCE = "<html><body><h1 id=\"toRemove\">Lorem ipsum header</h1></body></html>";

  private static final String RESULT = "<html><body></body></html>";

  private static final String INFO_PATTERN = RemoveNodesDataModifier.NAME
      + " DataModifier with parameters: " + PARAM_XPATH + ": %s";

  @Mock
  private Map<String, String> params;

  @Before
  public void setUp() {
    tested = new RemoveNodesDataModifier();
  }

  @Test
  public void setParameters_XPathValueIsValid_ExpectValidModifierInfo() throws ParametersException {
    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_VALUE);
    tested.setParameters(params);
    assertThat(tested.getInfo(), is(String.format(INFO_PATTERN, PARAM_XPATH_VALUE)));
  }

  @Test(expected = ParametersException.class)
  public void setParameters_XPathIsEmpty_ExceptionIsThrown() throws ParametersException {
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_XPathIsInvalid_ExceptionIsThrown() throws ParametersException {
    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_INVALID_VALUE);
    tested.setParameters(params);
  }

  @Test
  public void modifyData_XPathMatchesData_MatchedDataIsRemoved() throws ProcessingException,
      ParametersException {
    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_VALUE);
    tested.setParameters(params);
    assertThat(tested.modifyData(SOURCE), not(containsString("Lorem ipsum header")));
  }

  @Test
  public void modifyData_XPathDoesNotMatchData_MatchedDataIsNotRemoved() throws ProcessingException,
      ParametersException {
    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_NOT_MATCHING_VALUE);
    tested.setParameters(params);
    assertThat(tested.modifyData(SOURCE), containsString("Lorem ipsum header"));
  }

}
