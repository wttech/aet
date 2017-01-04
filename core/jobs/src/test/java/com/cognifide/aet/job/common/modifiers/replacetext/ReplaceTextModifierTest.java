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
package com.cognifide.aet.job.common.modifiers.replacetext;

import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReplaceTextModifierTest {

  private static final String PARAM_XPATH = "xpath";
  private static final String PARAM_CSS = "css";
  private static final String ATTRIBUTE_PARAM = "attributeName";
  private static final String VALUE_PARAM = "value";

  private static final String PARAM_XPATH_VALUE = "//*[@id='toRemove']";
  private static final String PARAM_CSS_VALUE = "@logo > a";
  private static final String PARAM_ATTRIBUTE_VALUE = "href";
  private static final String PARAM_VALUE_VALUE = "aetaetaet";

  private static final String URL = "http://www.cognifide.com";

  @Mock
  private WebDriver webDriver;

  @Mock
  private CollectorProperties properties;

  @InjectMocks
  private ReplaceTextModifier tested;

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
    when(properties.getUrl()).thenReturn(URL);
    tested.setParameters(params);
  }

  @Test
  public void setParameters_CssIsValid_ValidationPassedSuccessfuly() throws ParametersException {
    when(params.containsKey(PARAM_CSS)).thenReturn(true);
    when(params.get(PARAM_CSS)).thenReturn(PARAM_CSS_VALUE);
    when(properties.getUrl()).thenReturn(URL);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_CssAndXpathArePassed_ExceptionIsThrown() throws ParametersException {
    when(params.containsKey(PARAM_CSS)).thenReturn(true);
    when(params.get(PARAM_CSS)).thenReturn(PARAM_CSS_VALUE);

    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_VALUE);

    when(properties.getUrl()).thenReturn(URL);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_CssAndXpathAreNotPassed_ExceptionIsThrown() throws ParametersException {
    when(properties.getUrl()).thenReturn(URL);
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_XPathIsInvalid_ExceptionIsThrown() throws ParametersException {
    tested.setParameters(params);
  }

  @Test
  public void ReplaceTextInElement_ValidXPathIsProvided_WebDriverFindElementsMethodIsCalledOnce()
          throws ProcessingException, ParametersException {
    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_VALUE);
    tested.setParameters(params);
    tested.collect();
    verify(webDriver, atLeast(1)).findElement(By.xpath(PARAM_XPATH_VALUE));
  }

  @Test
  public void ReplaceTextInElement_ValidCssIsProvided_WebDriverFindElementsMethodIsCalledOnce()
          throws ProcessingException, ParametersException {
    when(params.containsKey(PARAM_CSS)).thenReturn(true);
    when(params.get(PARAM_CSS)).thenReturn(PARAM_CSS_VALUE);
    tested.setParameters(params);
    tested.collect();
    verify(webDriver, atLeast(1)).findElement(By.cssSelector(PARAM_CSS_VALUE));
  }

  @Test
  public void ReplaceTextInElement_AllValidParamsAreProvided_WebDriverFindElementsMethodIsCalledOnce()
          throws ProcessingException, ParametersException {
    when(params.containsKey(PARAM_XPATH)).thenReturn(true);
    when(params.get(PARAM_XPATH)).thenReturn(PARAM_XPATH_VALUE);
    when(params.containsKey(ATTRIBUTE_PARAM)).thenReturn(true);
    when(params.get(ATTRIBUTE_PARAM)).thenReturn(PARAM_ATTRIBUTE_VALUE);
    when(params.containsKey(VALUE_PARAM)).thenReturn(true);
    when(params.get(VALUE_PARAM)).thenReturn(PARAM_VALUE_VALUE);
    when(properties.getUrl()).thenReturn(URL);

    tested.setParameters(params);
    tested.collect();
    verify(webDriver, atLeast(1)).findElement(By.xpath(PARAM_XPATH_VALUE));
  }


}
