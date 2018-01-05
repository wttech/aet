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
package com.cognifide.aet.communication.api.execution;

/**
 * The result of starting the test suite execution.
 */
public class SuiteExecutionResult {

  private String correlationId;

  private String statusUrl;

  private String htmlReportUrl;

  private String xunitReportUrl;

  private String errorMessage;

  private SuiteExecutionResult() {
    //Use create methods to get instance
  }

  public static SuiteExecutionResult createSuccessResult(String correlationId, String statusUrl,
      String htmlReportUrl, String xunitReportUrl) {
    SuiteExecutionResult suiteExecutionResult = new SuiteExecutionResult();
    suiteExecutionResult.correlationId = correlationId;
    suiteExecutionResult.statusUrl = statusUrl;
    suiteExecutionResult.htmlReportUrl = htmlReportUrl;
    suiteExecutionResult.xunitReportUrl = xunitReportUrl;
    return suiteExecutionResult;
  }

  public static SuiteExecutionResult createErrorResult(String errorMessage) {
    SuiteExecutionResult suiteExecutionResult = new SuiteExecutionResult();
    suiteExecutionResult.errorMessage = errorMessage;
    return suiteExecutionResult;
  }

  /**
   * @return suite's correlation ID
   */
  public String getCorrelationId() {
    return correlationId;
  }

  /**
   * @return link the client should use to get the status of test suite processing
   */
  public String getStatusUrl() {
    return statusUrl;
  }

  /**
   * @return link to generated html report
   */
  public String getHtmlReportUrl() {
    return htmlReportUrl;
  }

  /**
   * @return link to generated xUnit report
   */
  public String getXunitReportUrl() {
    return xunitReportUrl;
  }

  /**
   * @return error message if the test suite execution failed to start
   */
  public String getErrorMessage() {
    return errorMessage;
  }

}
