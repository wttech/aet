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
package com.cognifide.aet.worker.drivers.chrome;

import com.cognifide.aet.job.api.collector.HttpRequestExecutor;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.google.common.collect.Ordering;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

public class ChromeCommunicationWrapperImpl implements WebCommunicationWrapper {

  private static final Function<LogEntry, JsErrorLog> LOG_ENTRY_TO_JS_ERROR = new LogEntryToJsError();

  private static final Predicate<LogEntry> ONLY_SEVERE_ERRORS = input -> Level.SEVERE
      .equals(input.getLevel());

  private final WebDriver webDriver;

  private final ProxyServerWrapper proxyServer;

  private final HttpRequestExecutor requestExecutor;

  public ChromeCommunicationWrapperImpl(WebDriver webDriver, ProxyServerWrapper server,
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
    LogEntries logEntries = webDriver.manage().logs().get(LogType.BROWSER);
    return StreamSupport.stream(logEntries.spliterator(), false)
        .filter(ONLY_SEVERE_ERRORS)
        .map(LOG_ENTRY_TO_JS_ERROR)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Ordering.natural())));
  }

  @Override
  public HttpRequestExecutor getHttpRequestExecutor() {
    return requestExecutor;
  }
}