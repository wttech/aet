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
package com.cognifide.aet.job.common.modifiers.login;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.HttpRequestExecutor;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RunWith(MockitoJUnitRunner.class)
public class LoginModifierTest {

  private static final String DEFAULT_LOGIN_TOKEN = "login-token";

  private static final String DEFAULT_LOGIN_INPUT_ELEMENT_SELECTOR = "//input[@name='j_username']";

  private static final String DEFAULT_PASSWORD_INPUT_ELEMENT_SELECTOR = "//input[@name='j_password']";

  private static final String DEFAULT_SUBMIT_BUTTON_ELEMENT_SELECTOR = "//*[@type='submit']";

  private static final String VALUE_ATTRIBUTE = "value";

  private static final String WRONG_LOGIN = "wrong";

  private static final String WRONG_PASSWORD = "wrong";

  private static final String LOGIN_PAGE_URL = "http://localhost/login.html";

  @Mock
  private WebCommunicationWrapper webCommunicationWrapper;

  @Mock
  private WebDriver webDriver;

  @Mock
  private WebDriver.Options options;

  @Mock
  private Cookie cookie;

  @Mock
  private HttpRequestExecutor requestExecutor;

  @Mock
  private WebElement loginInput;

  @Mock
  private WebElement passwordInput;

  @Mock
  private WebElement submitButton;

  @Mock
  private CollectorProperties properties;

  private final Map<String, String> params = Collections.singletonMap("login-page", LOGIN_PAGE_URL);

  private LoginModifier tested;

  @Before
  public void setUp() throws ParametersException {
    // this will init modifier with default configuration
    webDriver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
    when(webCommunicationWrapper.getHttpRequestExecutor()).thenReturn(requestExecutor);
    when(webCommunicationWrapper.getWebDriver()).thenReturn(webDriver);
    when(webDriver.manage()).thenReturn(options);

    when(webDriver.findElement(By.xpath(DEFAULT_LOGIN_INPUT_ELEMENT_SELECTOR)))
        .thenReturn(loginInput);
    when(webDriver.findElement(By.xpath(DEFAULT_PASSWORD_INPUT_ELEMENT_SELECTOR)))
        .thenReturn(passwordInput);
    when(webDriver.findElement(By.xpath(DEFAULT_SUBMIT_BUTTON_ELEMENT_SELECTOR)))
        .thenReturn(submitButton);

    when(loginInput.isDisplayed()).thenReturn(true);
    when(passwordInput.isDisplayed()).thenReturn(true);
    when(submitButton.isDisplayed()).thenReturn(true);

    when(properties.getUrl()).thenReturn("http://example.url.com");
    tested = new LoginModifier(webCommunicationWrapper);

    tested.setParameters(params);
  }

  @Test(expected = ProcessingException.class)
  public void collectTest_noTokenLoginFilledFailed() throws ProcessingException {
    when(options.getCookieNamed(DEFAULT_LOGIN_TOKEN)).thenReturn(null).thenReturn(cookie);
    when(loginInput.getAttribute(VALUE_ATTRIBUTE)).thenReturn(WRONG_LOGIN);
    when(passwordInput.getAttribute(VALUE_ATTRIBUTE)).thenReturn(WRONG_PASSWORD);

    tested.collect();

    verify(webDriver, times(3)).get(LOGIN_PAGE_URL);
    verify(submitButton, times(0)).click();
  }

  @Test
  public void collectTest_hasToken() throws ProcessingException {
    when(options.getCookieNamed(DEFAULT_LOGIN_TOKEN)).thenReturn(cookie);

    tested.collect();

    verify(requestExecutor).addCookie(anyString(), anyString());
    verify(webDriver, never()).get(LOGIN_PAGE_URL);
  }

}
