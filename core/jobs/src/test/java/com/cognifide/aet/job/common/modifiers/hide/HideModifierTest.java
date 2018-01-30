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
package com.cognifide.aet.job.common.modifiers.hide;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@RunWith(MockitoJUnitRunner.class)
public class HideModifierTest {

  private static final String PARAM_XPATH = "xpath";

  private static final String PARAM_CSS = "css";

  private static final String PARAM_XPATH_VALUE = "//*[@id='toRemove']";

  private static final String PARAM_CSS_VALUE = "@logo > a";

  private static final String URL = "http://www.cognifide.com";

  @Mock
  private WebDriver webDriver;

  @InjectMocks
  private HideModifier tested;

  @Mock
  private Map<String, String> params;

  @Before
  public void setUp() {
    when(webDriver.getCurrentUrl()).thenReturn(URL);
  }

  @Test
  public void setParameters_XPathIsValid_ValidationPassedSuccessfuly() throws ParametersException {
    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_VALUE);
    tested.setParameters(params);
  }

  @Test
  public void setParameters_CssIsValid_ValidationPassedSuccessfuly() throws ParametersException {
    when(params.containsKey(PARAM_CSS)).thenReturn(true);
    when(params.get(PARAM_CSS)).thenReturn(PARAM_CSS_VALUE);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_CssAndXpathArePassed_ValidationPassedUnsuccessfuly()
      throws ParametersException {
    when(params.containsKey(PARAM_CSS)).thenReturn(true);
    when(params.get(PARAM_CSS)).thenReturn(PARAM_CSS_VALUE);

    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_VALUE);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_CssAndXpathAreNotPassed_ValidationPassedUnsuccessfuly()
      throws ParametersException {
    tested.setParameters(params);
  }
}
