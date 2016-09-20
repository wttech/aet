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
package com.cognifide.aet.job.common.modifiers.click;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.validation.ValidationResultBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClickModifierTest {

  private static final String PARAM_XPATH = "xpath";

  private static final String PARAM_TIMEOUT = "timeout";

  private static final String PARAM_XPATH_VALUE = "//*[@id='toClick']";

  private static final String PARAM_TIMEOUT_VALUE = "5";

  @Mock
  private WebDriver webDriver;

  @InjectMocks
  private ClickModifier tested;

  @Mock
  private Map<String, String> params;

  @Mock
  private WebElement elementToClick;

  @Mock
  private ValidationResultBuilder validationResultBuilder;

  @Before
  public void setUp() {
    when(webDriver.findElement(By.xpath(PARAM_XPATH_VALUE))).thenReturn(elementToClick);
  }

  public void setupParams(String timeoutValue, String xpathValue) {
    when(params.get(PARAM_TIMEOUT)).thenReturn(timeoutValue);
    when(params.get(PARAM_XPATH)).thenReturn(xpathValue);
  }

  @Test
  public void setParameters_whenValidParamsAreProvided_thenValidationPassedSuccessfuly() throws ParametersException {
    setupParams(PARAM_TIMEOUT_VALUE, PARAM_TIMEOUT_VALUE);

    tested.setParameters(params);

    verify(validationResultBuilder, times(0)).addErrorMessage(anyString());
  }

  @Test
  public void setParameters_whenInvalidTimeoutParamIsProvided_thenExceptionIsThrown() throws ParametersException {
    setupParams("xyz", PARAM_XPATH_VALUE);

    tested.setParameters(params);

    verify(validationResultBuilder, times(1)).addErrorMessage(anyString());
  }

  @Test
  public void setParameters_whenTooBigTimeoutParamIsProvided_thenExceptionIsThrown() throws ParametersException {
    setupParams("300000", PARAM_XPATH_VALUE);

    tested.setParameters(params);

    verify(validationResultBuilder, times(1)).addErrorMessage(anyString());
  }

  @Test
  public void setParameters_whenNoXpathParamIsProvided_thenExceptionIsThrown() throws ParametersException {
    setupParams(PARAM_TIMEOUT_VALUE, null);

    tested.setParameters(params);

    verify(validationResultBuilder, times(1)).addErrorMessage(anyString());
  }

  @Test
  public void collect_whenValidParamsAreProvided_elementIsClicked() throws ProcessingException, ParametersException {
    setupParams(PARAM_TIMEOUT_VALUE, PARAM_XPATH_VALUE);
    when(elementToClick.isDisplayed()).thenReturn(true);

    tested.setParameters(params);
    tested.collect();

    verify(elementToClick, times(1)).click();
  }
}