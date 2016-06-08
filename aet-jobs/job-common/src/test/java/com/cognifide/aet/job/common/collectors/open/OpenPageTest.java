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
package com.cognifide.aet.job.common.collectors.open;

import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.HttpRequestBuilder;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class OpenPageTest {

	private static final String URL = "http://example.com";

	@Mock
	private WebCommunicationWrapper webCommunicationWrapper;

	@Mock
	private CollectorProperties collectorProperties;

	@Mock
	private Map<String, String> params;

	@Mock
	private ProxyServerWrapper proxyServer;

	@Mock
	private WebDriver webDriver;

	@Mock
	private HttpRequestBuilder httpRequestBuilder;

	private OpenPage tested;

	@Before
	public void setUp() {
		when(collectorProperties.getUrl()).thenReturn(URL);
		when(webCommunicationWrapper.isUseProxy()).thenReturn(false);
		when(webCommunicationWrapper.getProxyServer()).thenReturn(proxyServer);
		when(webCommunicationWrapper.getWebDriver()).thenReturn(webDriver);
		when(webCommunicationWrapper.getHttpRequestBuilder()).thenReturn(httpRequestBuilder);

		tested = new OpenPage(webCommunicationWrapper, collectorProperties);
	}

	@Test
	public void collectTest() throws ProcessingException {
		tested.collect();

		verify(webDriver, times(1)).get(URL);
		verify(httpRequestBuilder, times(1)).createRequest(URL, "GET");
	}

	@Test
	public void collectTest_withProxy() throws ProcessingException {
		when(webCommunicationWrapper.isUseProxy()).thenReturn(true);

		tested.collect();

		verify(proxyServer, times(1)).newHar(URL);
		verify(webDriver, times(1)).get(URL);
		verify(httpRequestBuilder, times(1)).createRequest(URL, "GET");
	}

	@Test
	public void setParametersTest() {
		tested.setParameters(params);
	}

}
