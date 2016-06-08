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
package com.cognifide.aet.job.common.collectors.performance.clientside;

import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.JsRuntimeWrapper;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.Result;
import net.lightbody.bmp.core.har.Har;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author maciej.laskowski
 *         Created: 2015-04-27
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientSidePerformanceCollector.class)
public class ClientSidePerformanceCollectorTest {

	private ClientSidePerformanceCollector tested;

	@Mock
	private Node dataNode;

	@Mock
	private WebCommunicationWrapper wrapper;

	@Mock
	private CollectorProperties properties;

	@Mock
	private BundleContext context;

	@Before
	public void setUp() throws Exception {
		tested = new ClientSidePerformanceCollector(dataNode, wrapper, properties, context);
	}

	@Test(expected = ProcessingException.class)
	public void collect_whenWrapperHasNoProxy_expectProcessingException() throws Exception {
		when(wrapper.getProxyServer()).thenReturn(null);
		tested.collect();
	}

	@Test
	public void collect_whenTrue() throws Exception {
		JsRuntimeWrapper mockedWrapper = Mockito.mock(JsRuntimeWrapper.class);
		PowerMockito.whenNew(JsRuntimeWrapper.class).withArguments(any()).thenReturn(mockedWrapper);

		ProxyServerWrapper mockedProxy = Mockito.mock(ProxyServerWrapper.class);
		Har mockedHar = Mockito.mock(Har.class);
		when(wrapper.getProxyServer()).thenReturn(mockedProxy);
		when(mockedProxy.getHar()).thenReturn(mockedHar);

		tested.collect();
		verify(dataNode, times(1)).saveResult(Matchers.<Result>any());
	}
}