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
package com.cognifide.aet.worker.drivers.firefox;

import com.cognifide.aet.job.api.collector.HttpRequestExecutor;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.worker.helpers.JavaScriptError;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;

public class FirefoxCommunicationWrapperImpl implements WebCommunicationWrapper {

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

  private final HttpRequestExecutor requestExecutor;

  public FirefoxCommunicationWrapperImpl(WebDriver webDriver, ProxyServerWrapper server,
      HttpRequestExecutor requestExecutor) {
    this.webDriver = webDriver;
    this.proxyServer = server;
    this.requestExecutor = requestExecutor;
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
    return javaScriptErrors.stream()
        .map(ERROR_LOG_FUNCTION)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Ordering.natural())));
  }

  @Override
  public HttpRequestExecutor getHttpRequestExecutor() {
    return requestExecutor;
  }

}
