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
package com.cognifide.aet.worker.drivers;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

import com.cognifide.aet.job.api.collector.HttpRequestBuilder;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.worker.helpers.JavaScriptError;

import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Set;

/**
 * @author lukasz.wieczorek
 */
public class WebCommunicationWrapperImpl implements WebCommunicationWrapper {

  private static final Function<JavaScriptError, JsErrorLog> ERROR_LOG_FUNCTION = new Function<JavaScriptError, JsErrorLog>() {

    @Override
    public JsErrorLog apply(JavaScriptError input) {
      JsErrorLog jsErrorLog = null;
      if (input != null) {
        jsErrorLog = new JsErrorLog(input.getErrorMessage(), input.getSourceName(),
                input.getLineNumber());
      }
      return jsErrorLog;
    }
  };

  private final WebDriver webDriver;

  private final ProxyServerWrapper proxyServer;

  private final HttpRequestBuilder builder;

  public WebCommunicationWrapperImpl(WebDriver webDriver, ProxyServerWrapper server, HttpRequestBuilder builder) {
    this.webDriver = webDriver;
    this.proxyServer = server;
    this.builder = builder;
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public ProxyServerWrapper getProxyServer() {
    return proxyServer;
  }

  @Override
  public boolean isUseProxy() {
    return proxyServer != null;
  }

  @Override
  public Set<JsErrorLog> getJSErrorLogs() {
    List<JavaScriptError> javaScriptErrors = JavaScriptError.readErrors(webDriver);
    return FluentIterable.from(javaScriptErrors).transform(ERROR_LOG_FUNCTION).toSortedSet(
            Ordering.natural());
  }

  @Override
  public HttpRequestBuilder getHttpRequestBuilder() {
    return builder;
  }

}
