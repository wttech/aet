/*
 * Cognifide AET :: Job Common
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.cookie;

import com.cognifide.aet.job.api.collector.HttpRequestBuilder;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.validation.ValidationResultBuilder;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CookieModifierTest {

	private static final String ACTION_PARAMETER = "action";

	@Mock
	private WebDriver webDriver;

	@Mock
	private HttpRequestBuilder builder;

	@Mock
	private WebCommunicationWrapper wrapper;

	@Mock
	private WebDriver.Options options;

	@Mock
	private ValidationResultBuilder validationResultBuilder;

	@Mock
	private Map<String, String> params;

	@InjectMocks
	private CookieModifier tested;

	@Before
	public void setUp() throws Exception {
		when(wrapper.getWebDriver()).thenReturn(webDriver);
		when(wrapper.getHttpRequestBuilder()).thenReturn(builder);
		when(webDriver.manage()).thenReturn(options);

		when(params.get(ACTION_PARAMETER)).thenReturn("add");
	}

	@Test
	public void testCollect_addCookie() throws Exception {
		tested.setParameters(ImmutableMap.of("action", "add", "cookie-name", "session", "cookie-value",
				"zaq12wsx"));
		tested.collect();

		verify(options).addCookie(new Cookie("session", "zaq12wsx"));
		verify(builder).addCookie("session", "zaq12wsx");
	}

	@Test
	public void testCollect_deleteCookie() throws Exception {
		tested.setParameters(ImmutableMap.of("action", "remove", "cookie-name", "session"));
		tested.collect();

		verify(options).deleteCookieNamed("session");
		verify(builder).removeCookie("session");
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
