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
package com.cognifide.aet.common;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.suiteexecution.SuiteExecutionResult;
import com.cognifide.aet.communication.api.suiteexecution.SuiteStatusResult;
import com.jcabi.log.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestSuiteRunner {

  private static final String DATE_FORMAT = "HH:mm:ss.SSS";

  private static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>() {
    @Override
    protected DateFormat initialValue() {
      return new SimpleDateFormat(DATE_FORMAT);
    }
  };

  private static final int STATUS_CHECK_INTERVAL = 1000;

  private final ResponseHandler<SuiteExecutionResult> suiteExecutionResponseHandler;

  private final ResponseHandler<SuiteStatusResult> suiteStatusResponseHandler;

  private final String buildDirectory;

  private final int timeout;

  private final RedirectWriter redirectWriter;

  private final String endpointDomain;

  private final String domain;

  private final boolean xUnit;

  public TestSuiteRunner(String endpointDomain, String buildDirectory, int timeout, String domain,
      boolean xUnit) {
    this.redirectWriter = new RedirectWriter(buildDirectory);
    this.buildDirectory = buildDirectory;
    this.timeout = timeout;
    this.endpointDomain = endpointDomain;
    this.domain = domain;
    this.xUnit = xUnit;
    suiteExecutionResponseHandler = new JsonResponseHandler<>(SuiteExecutionResult.class);
    suiteStatusResponseHandler = new JsonResponseHandler<>(SuiteStatusResult.class);
  }

  public void runTestSuite(final File testSuite) throws AETException {
    RunnerTerminator runnerTerminator = new RunnerTerminator();
    try {
      SuiteExecutionResult suiteExecutionResult = startSuiteExecution(testSuite);

      String now = DATE_FORMATTER.get().format(new Date());
      Logger.info(this, "CorrelationID: %s", suiteExecutionResult.getCorrelationId());
      Logger.info(this,
              "********************************************************************************");
      Logger.info(this,
              "********************** Job Setup finished at " + now + ".**********************");
      Logger.info(this,
              "*** Suite is now processed by the system, progress will be available below. ****");
      Logger.info(this,
              "********************************************************************************");
      while (runnerTerminator.isActive()) {
        Thread.sleep(STATUS_CHECK_INTERVAL);
        SuiteStatusResult suiteStatus = getSuiteStatus(suiteExecutionResult.getStatusUrl());
        processStatus(runnerTerminator, suiteExecutionResult.getHtmlReportUrl(), suiteStatus);
      }
      if (xUnit) {
        downloadXUnitTest(suiteExecutionResult.getXunitReportUrl());
      }
    } catch (IOException | InterruptedException e) {
      throw new AETException("Failed to process test suite", e);
    } finally {
      Logger.info(this, "Suite processing finished.");
    }
  }

  private SuiteExecutionResult startSuiteExecution(File testSuite) throws IOException {
    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
        .addBinaryBody("suite", testSuite, ContentType.APPLICATION_XML, testSuite.getName());
    if (domain != null) {
      entityBuilder.addTextBody("domain", domain);
    }
    HttpEntity entity = entityBuilder.build();
    return Request.Post(getSuiteUrl())
        .body(entity)
        .connectTimeout(timeout)
        .socketTimeout(timeout)
        .execute()
        .handleResponse(suiteExecutionResponseHandler);
  }

  private String getSuiteUrl() {
    return endpointDomain + "/suite";
  }

  private SuiteStatusResult getSuiteStatus(String statusUrl) throws IOException {
    return Request.Get(statusUrl)
        .connectTimeout(timeout)
        .socketTimeout(timeout)
        .execute()
        .handleResponse(suiteStatusResponseHandler);
  }

  private void downloadXUnitTest(String xUnitUrl) {
    try {
      new ReportWriter().write(buildDirectory, xUnitUrl, "xunit-report.xml");
    } catch (IOException e) {
      Logger.error(this, "Failed to obtain xUnit report from: %s", xUnitUrl, e);
    }
  }

  private void processStatus(RunnerTerminator runnerTerminator, String reportUrl,
      SuiteStatusResult suiteStatusResult) throws AETException {
    StatusProcessor statusProcessor = ProcessorFactory.produce(suiteStatusResult, reportUrl,
        redirectWriter, runnerTerminator);
    if (statusProcessor != null) {
      statusProcessor.process();
    }
  }
}
