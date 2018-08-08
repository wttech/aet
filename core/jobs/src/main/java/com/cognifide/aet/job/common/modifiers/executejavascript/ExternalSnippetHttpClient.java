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
package com.cognifide.aet.job.common.modifiers.executejavascript;

import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.modifiers.executejavascript.configuration.ExternalSnippetHttpClientConf;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ExternalSnippetHttpClient.class)
@Designate(ocd = ExternalSnippetHttpClientConf.class)
public class ExternalSnippetHttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSnippetHttpClient.class);

  private CloseableHttpClient httpClient;

  private ExternalSnippetHttpClientConf config;

  @Activate
  protected void activate(ExternalSnippetHttpClientConf config) {
    LOGGER.info("Activating ExternalSnippetHttpClient - {}", this);
    this.config = config;

    final PoolingHttpClientConnectionManager poolingConnManager =
        new PoolingHttpClientConnectionManager(config.connectionTtl(), TimeUnit.SECONDS);
    poolingConnManager.setMaxTotal(config.maxConcurrentConnections());

    httpClient = HttpClients.custom().setConnectionManager(poolingConnManager).build();
  }

  @Deactivate
  protected void deactivate() {
    LOGGER.info("Deactivating ExternalSnippetHttpClient");
    if (httpClient != null) {
      try {
        LOGGER.info("Releasing http client");
        httpClient.close();
      } catch (IOException e) {
        LOGGER.error("Can't close httpClient", e);
      }
    }
  }

  public String get(String url, String basicAuth) throws ProcessingException {
    return call(new HttpGet(url), basicAuth);
  }

  private String call(HttpRequestBase requestBase, String basicAuth) throws ProcessingException {
    final StopWatch stopWatch = new StopWatch();
    if (LOGGER.isDebugEnabled()) {
      stopWatch.start();
      LOGGER.debug("Requesting {} with {} method", requestBase.getURI(), requestBase.getMethod());
    }
    setBasicAuthHeader(requestBase, basicAuth);
    String result;
    try (CloseableHttpResponse response = httpClient.execute(requestBase)) {
      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        result = CharStreams
            .toString(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8));
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Response [{}] {} in {}ms - {}", statusCode, requestBase.getURI(),
              stopWatch.getTime(), result);
        }
      } else {
        throw new ProcessingException(String
            .format("Couldn't get snippet from %s : status code: %d",
                requestBase.getURI().toString(), statusCode));
      }
    } catch (IOException e) {
      throw new ProcessingException(
          String.format("Couldn't connect to %s", requestBase.getURI().toString()), e);
    }
    return result;
  }

  private void setBasicAuthHeader(HttpRequestBase requestBase, String basicAuth) {
    if (StringUtils.isNotBlank(basicAuth)) {
      requestBase.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);
    }
  }

}
