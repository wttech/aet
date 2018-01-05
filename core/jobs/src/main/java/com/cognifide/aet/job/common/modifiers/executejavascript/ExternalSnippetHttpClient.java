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
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(ExternalSnippetHttpClient.class)
public class ExternalSnippetHttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSnippetHttpClient.class);

  private static final long DEFAULT_CONNECTION_TTL = 60L;
  private static final int DEFAULT_MAX_CONCURRENT_CONNECTIONS = 50;

  @Property(label = "Time in seconds that defines maximum life span of persistent connections regardless of their expiration setting",
      longValue = DEFAULT_CONNECTION_TTL)
  private static final String CONNECTION_TTL_PARAMETER = "connectionTtl";

  @Property(label = "The maximal number of concurrent connections this service can provide. Connections over this limit are rejected.",
      intValue = DEFAULT_MAX_CONCURRENT_CONNECTIONS)
  private static final String MAX_CONCURRENT_CONNECTIONS_PARAMETER = "maxConcurrentConnections";

  private CloseableHttpClient httpClient;

  @Activate
  protected void activate(Map<String, Object> properties) {
    LOGGER.info("Activating ExternalSnippetHttpClient - {}", this);

    Long connectionTtl = PropertiesUtil
        .toLong(properties.get(CONNECTION_TTL_PARAMETER), DEFAULT_CONNECTION_TTL);
    Integer maxConcurrentConnections = PropertiesUtil
        .toInteger(properties.get(MAX_CONCURRENT_CONNECTIONS_PARAMETER),
            DEFAULT_MAX_CONCURRENT_CONNECTIONS);

    final PoolingHttpClientConnectionManager poolingConnManager =
        new PoolingHttpClientConnectionManager(connectionTtl, TimeUnit.SECONDS);
    poolingConnManager.setMaxTotal(maxConcurrentConnections);

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
