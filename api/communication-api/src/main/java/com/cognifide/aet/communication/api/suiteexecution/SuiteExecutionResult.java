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
package com.cognifide.aet.communication.api.suiteexecution;

public class SuiteExecutionResult {

  private String statusUrl;

  private String htmlReportUrl;

  private String xunitReportUrl;

  private String errorMessage;

  private SuiteExecutionResult() {
    //Use create methods to get instance
  }

  public static SuiteExecutionResult createSuccessResult(String statusUrl, String htmlReportUrl,
      String xunitReportUrl) {
    SuiteExecutionResult suiteExecutionResult = new SuiteExecutionResult();
    suiteExecutionResult.setStatusUrl(statusUrl);
    suiteExecutionResult.setHtmlReportUrl(htmlReportUrl);
    suiteExecutionResult.setXunitReportUrl(xunitReportUrl);
    return suiteExecutionResult;
  }

  public static SuiteExecutionResult createErrorResult(String errorMessage) {
    SuiteExecutionResult suiteExecutionResult = new SuiteExecutionResult();
    suiteExecutionResult.setErrorMessage(errorMessage);
    return suiteExecutionResult;
  }

  public String getStatusUrl() {
    return statusUrl;
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

  private void setStatusUrl(String statusUrl) {
    this.statusUrl = statusUrl;
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
