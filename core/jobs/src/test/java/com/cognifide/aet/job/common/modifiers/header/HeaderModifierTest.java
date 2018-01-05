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
package com.cognifide.aet.job.common.modifiers.header;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.collector.HttpRequestExecutor;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HeaderModifierTest {

  @Mock
  private ProxyServerWrapper proxyServer;

  @Mock
  private WebCommunicationWrapper webCommunicationWrapper;

  @Mock
  private HttpRequestExecutor requestExecutor;

  @InjectMocks
  private HeaderModifier headerModifier;

  @Before
  public void setUp() throws Exception {
    when(webCommunicationWrapper.getProxyServer()).thenReturn(proxyServer);
    when(webCommunicationWrapper.isUseProxy()).thenReturn(true);
    when(webCommunicationWrapper.getHttpRequestExecutor()).thenReturn(requestExecutor);
  }

  @Test
  public void testCollect() throws Exception {
    headerModifier.setParameters(ImmutableMap.of("key", "header", "value", "value1"));
    headerModifier.collect();

    verify(proxyServer).addHeader("header", "value1");
    verify(requestExecutor).addHeader("header", "value1");
  }

  @Test(expected = ProcessingException.class)
  public void testCollect_missingProxy() throws Exception {
    when(webCommunicationWrapper.isUseProxy()).thenReturn(false);

    headerModifier.setParameters(ImmutableMap.of("key", "header", "value", "value1"));
    headerModifier.collect();
  }

  @Test(expected = ParametersException.class)
  public void testSetParameters_invalid() throws Exception {
    headerModifier.setParameters(ImmutableMap.of("key", "header"));
  }

}
