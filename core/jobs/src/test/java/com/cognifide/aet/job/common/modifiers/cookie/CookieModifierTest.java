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
package com.cognifide.aet.job.common.modifiers.cookie;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.HttpRequestExecutor;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.validation.ValidationResultBuilder;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

@RunWith(MockitoJUnitRunner.class)
public class CookieModifierTest {

  private static final String ACTION_PARAMETER = "action";

  @Mock
  private WebDriver webDriver;

  @Mock
  private HttpRequestExecutor requestExecutor;

  @Mock
  private WebCommunicationWrapper wrapper;

  @Mock
  private WebDriver.Options options;

  @Mock
  private ValidationResultBuilder validationResultBuilder;

  @Mock
  private Map<String, String> params;

  @Mock
  private CollectorProperties properties;

  @InjectMocks
  private CookieModifier tested;

  @Before
  public void setUp() throws Exception {
    when(wrapper.getWebDriver()).thenReturn(webDriver);
    when(wrapper.getHttpRequestExecutor()).thenReturn(requestExecutor);
    when(webDriver.manage()).thenReturn(options);

    when(params.get(ACTION_PARAMETER)).thenReturn("add");
    when(properties.getUrl()).thenReturn("http://example.url.com");
  }

  @Test
  public void testCollect_addCookie() throws Exception {
    tested.setParameters(
        ImmutableMap.of("action", "add", "cookie-name", "session", "cookie-value", "zaq12wsx"));
    tested.collect();

    verify(options).addCookie(new Cookie("session", "zaq12wsx"));
    verify(requestExecutor).addCookie("session", "zaq12wsx");
  }

  @Test
  public void testCollect_deleteCookie() throws Exception {
    tested.setParameters(ImmutableMap.of("action", "remove", "cookie-name", "session"));
    tested.collect();

    verify(options).deleteCookieNamed("session");
    verify(requestExecutor).removeCookie("session");
  }

  @Test
  public void setParametersTest() throws ParametersException {
    when(validationResultBuilder.hasErrors()).thenReturn(false);
    // no parameters exception expected
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_invalid() throws ParametersException {
    when(validationResultBuilder.hasErrors()).thenReturn(true);
    // parameters exception expected
    tested.setParameters(params);
  }

}
