/**
 * AET <p> Copyright (C) 2013 Cognifide Limited <p> Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.click;

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
import org.openqa.selenium.WebElement;

@RunWith(MockitoJUnitRunner.class)
public class ClickModifierTest {

  private static final String PARAM_XPATH = "xpath";

  private static final String PARAM_CSS = "css";

  private static final String PARAM_TIMEOUT = "timeout";

  private static final String PARAM_XPATH_VALUE = "//*[@id='toClick']";

  private static final String PARAM_CSS_VALUE = "#hlogo > a";

  private static final String PARAM_TIMEOUT_VALUE = "15000";

  @Mock
  private WebDriver webDriver;

  @InjectMocks
  private ClickModifier tested;

  @Mock
  private Map<String, String> params;

  @Mock
  private WebElement elementToClick;


  @Before
  public void setUp() {
    when(webDriver.findElement(By.xpath(PARAM_XPATH_VALUE))).thenReturn(elementToClick);
  }

  public void setupParams(String timeoutValue, String xpathValue, String cssValue) {
    when(params.get(PARAM_TIMEOUT)).thenReturn(timeoutValue);
    when(params.get(PARAM_XPATH)).thenReturn(xpathValue);
    when(params.get(PARAM_CSS)).thenReturn(cssValue);
  }

  @Test
  public void setParameters_whenValidXpathParamIsProvided_thenValidationPassedSuccessfuly()
      throws ParametersException {
    setupParams(PARAM_TIMEOUT_VALUE, PARAM_XPATH_VALUE, null);
    tested.setParameters(params);
  }

  @Test
  public void setParameters_whenValidCSSParamIsProvided_thenValidationPassedSuccessfuly()
      throws ParametersException {
    setupParams(PARAM_TIMEOUT_VALUE, null, PARAM_CSS_VALUE);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_whenInvalidTimeoutParamIsProvided_thenExceptionIsThrown()
      throws ParametersException {
    setupParams("xyz", PARAM_XPATH_VALUE, null);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_whenInvalidXpathParamIsProvided_thenExceptionIsThrown()
      throws ParametersException {
    setupParams(PARAM_TIMEOUT_VALUE, "", null);
    tested.setParameters(params);
  }


  @Test(expected = ParametersException.class)
  public void setParameters_whenTooBgTimeoutParamIsProvided_thenExceptionIsThrown()
      throws ParametersException {
    setupParams("300000", PARAM_XPATH_VALUE, null);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_whenNoParamsIsProvided_thenExceptionIsThrown()
      throws ParametersException {
    setupParams(PARAM_TIMEOUT_VALUE, null, null);
    tested.setParameters(params);
  }

}
