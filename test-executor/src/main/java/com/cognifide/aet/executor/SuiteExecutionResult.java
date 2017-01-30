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
package com.cognifide.aet.executor;

public class SuiteExecutionResult {

  private String correlationId;

  private String htmlReportUrl;

  private String xunitReportUrl;

  private String errorMessage;

  private SuiteExecutionResult() {
    //Use create methods to get instance
  }

  static SuiteExecutionResult createSuccessResult(String correlationId, String htmlReportUrl,
      String xunitReportUrl) {
    SuiteExecutionResult suiteExecutionResult = new SuiteExecutionResult();
    suiteExecutionResult.setCorrelationId(correlationId);
    suiteExecutionResult.setHtmlReportUrl(htmlReportUrl);
    suiteExecutionResult.setXunitReportUrl(xunitReportUrl);
    return suiteExecutionResult;
  }

  static SuiteExecutionResult createErrorResult(String errorMessage) {
    SuiteExecutionResult suiteExecutionResult = new SuiteExecutionResult();
    suiteExecutionResult.setErrorMessage(errorMessage);
    return suiteExecutionResult;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public String getHtmlReportUrl() {
    return htmlReportUrl;
  }

  public String getXunitReportUrl() {
    return xunitReportUrl;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  private void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  private void setHtmlReportUrl(String htmlReportUrl) {
    this.htmlReportUrl = htmlReportUrl;
  }

  private void setXunitReportUrl(String xunitReportUrl) {
    this.xunitReportUrl = xunitReportUrl;
  }

  private void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
