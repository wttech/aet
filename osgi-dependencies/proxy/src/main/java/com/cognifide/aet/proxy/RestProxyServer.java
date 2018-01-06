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
package com.cognifide.aet.proxy;

import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.proxy.exceptions.UnableToAddHeaderException;
import com.github.detro.browsermobproxyclient.BMPCProxy;
import com.github.detro.browsermobproxyclient.exceptions.BMPCUnableToConnectException;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.browsermob.core.har.Har;
import org.json.JSONObject;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestProxyServer implements ProxyServerWrapper {

  private static final String HTTP = "http";

  public static final int STATUS_CODE_OK = 200;

  private BMPCProxy server;

  private boolean captureContent;

  private boolean captureHeaders;

  private RestProxyManager proxyManager;

  private static final Logger LOGGER = LoggerFactory.getLogger(RestProxyServer.class);

  public RestProxyServer(BMPCProxy bmpcProxy, RestProxyManager restProxyManager) {
    this.server = bmpcProxy;
    this.proxyManager = restProxyManager;
  }

  @Override
  public Proxy seleniumProxy() throws UnknownHostException {
    return server.asSeleniumProxy()
        .setHttpProxy(String.format("%s:%d", proxyManager.getServer(), getPort()))
        .setSslProxy(String.format("%s:%d", proxyManager.getServer(), getPort()));
  }

  @Override
  public Har getHar() {
    return new GsonBuilder().serializeNulls()
        .registerTypeAdapter(Date.class, new DateDeserializer())
        .create()
        .fromJson(server.har(), Har.class);
  }

  @Override
  public Har newHar(String initialPageRef) {
    return new GsonBuilder().serializeNulls()
        .registerTypeAdapter(Date.class, new DateDeserializer())
        .create()
        .fromJson(server.newHar(initialPageRef, captureHeaders, captureContent, false), Har.class);
  }

  @Override
  public int getPort() {
    return server.getProxyPort();
  }

  @Override
  public void setCaptureHeaders(boolean captureHeaders) {
    this.captureHeaders = captureHeaders;
  }

  @Override
  public void setCaptureContent(boolean captureContent) {
    this.captureContent = captureContent;
  }

  @Override
  public void addHeader(String name, String value) {
    CloseableHttpClient httpClient = HttpClients.createSystem();
    try {
      URIBuilder uriBuilder = new URIBuilder().setScheme(HTTP).setHost(server.getAPIHost())
          .setPort(server.getAPIPort());
      // Request BMP to add header
      HttpPost request = new HttpPost(uriBuilder.setPath(
          String.format("/proxy/%d/headers", server.getProxyPort())).build());
      request.setHeader("Content-Type", "application/json");
      JSONObject json = new JSONObject();
      json.put(name, value);
      request.setEntity(new StringEntity(json.toString()));
      // Execute request
      CloseableHttpResponse response = httpClient.execute(request);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != STATUS_CODE_OK) {
        throw new UnableToAddHeaderException("Invalid HTTP Response when attempting to add header"
            + statusCode);
      }
      response.close();
    } catch (Exception e) {
      throw new BMPCUnableToConnectException(
          String.format("Unable to connect to BMP Proxy at '%s:%s'",
              server.getAPIHost(), server.getAPIPort()), e);
    } finally {
      try {
        httpClient.close();
      } catch (IOException e) {
        LOGGER.warn("Unable to close httpClient", e);
      }
    }
  }

  @Override
  public void stop() {
    server.close();
    proxyManager.detach(this);
  }

}
