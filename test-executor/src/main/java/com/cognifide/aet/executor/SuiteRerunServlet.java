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
package com.cognifide.aet.executor;

import static com.cognifide.aet.executor.model.CorrelationIdGenerator.generateCorrelationId;
import static com.cognifide.aet.rest.BasicDataServlet.isValidCorrelationId;
import static com.cognifide.aet.rest.BasicDataServlet.isValidName;
import static com.cognifide.aet.rest.BasicDataServlet.responseAsJson;

import com.cognifide.aet.communication.api.execution.SuiteExecutionResult;
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.Operation;
import com.cognifide.aet.communication.api.metadata.Statistics;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.executor.http.HttpSuiteExecutionResultWrapper;
import com.cognifide.aet.rest.Helper;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.CharEncoding;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpStatus;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Component(label = "Suite Rerun Servlet", description = "Executes received test suite", immediate = true)
public class SuiteRerunServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerunServlet.class);
  private static final String SERVLET_PATH = "/suite-rerun";
  private static final long serialVersionUID = 6317770911546678642L;

  @Reference
  private HttpService httpService;

  @Reference
  private SuiteExecutor suiteExecutor;

  @Reference
  private transient MetadataDAO metadataDAO;

  private static final Gson GSON = new Gson();

  private SuiteExecutionResult suiteExecutionResult;

  private HttpSuiteExecutionResultWrapper resultWrapper = null;

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    addCors(response);
    String correlationId = request.getParameter(Helper.CORRELATION_ID_PARAM);
    String suiteName = request.getParameter(Helper.SUITE_PARAM);
    String testName = request.getParameter(Helper.TEST_RERUN_PARAM);

    Suite suite = null;
    DBKey dbKey;

    try {
      dbKey = Helper.getDBKeyFromRequest(request);
      suite = getSuiteFromMetadata(dbKey, correlationId, suiteName);
    } catch (ValidatorException | StorageException e) {
      LOGGER.error("Validation problem!", e);
      response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      return;
    }

    prepareSuiteToRerun(suite, testName);

    try {
      resultWrapper = suiteExecutor.executeSuite(suite);
      createResponse(resultWrapper, response);
    } catch (javax.jms.JMSException | ValidatorException e) {
      e.printStackTrace();
    }
  }

  private void createResponse(HttpSuiteExecutionResultWrapper resultWrapper,
      HttpServletResponse response) throws IOException {
    suiteExecutionResult = resultWrapper.getExecutionResult();
    String responseBody = GSON.toJson(suiteExecutionResult);

    if (resultWrapper.hasError()) {
      response.sendError(resultWrapper.getStatusCode(), suiteExecutionResult.getErrorMessage());
    } else {
      response.setStatus(HttpStatus.SC_OK);
      response.setContentType("application/json");
      response.setCharacterEncoding(CharEncoding.UTF_8);
      response.getWriter().write(responseBody);
    }
  }

  private void prepareSuiteToRerun(Suite suite, String testName) {
    Test testToRerun = suite.getTest(testName);
    suite.removeAllTests();
    suite.addTest(testToRerun);
    suite.setCorrelationId(
        generateCorrelationId(suite.getCompany(), suite.getProject(), suite.getName()));
    cleanDataFromSuite(suite);
  }

  private void cleanDataFromSuite(Suite suite) {
    for (Test test : suite.getTests()) {
      for (Url url : test.getUrls()) {
        url.setCollectionStats(null);
        for (Step step : url.getSteps()) {
          step.setStepResult(null);
          if (step.getComparators() == null) {
            continue;
          }
          for (Comparator comparator : step.getComparators()) {
            comparator.setStepResult(null);
            comparator.setFilters(new ArrayList<>());
          }
        }
      }
    }
  }

  private Suite getSuiteFromMetadata(DBKey dbKey, String correlationId, String suiteName)
      throws StorageException {
    if (isValidCorrelationId(correlationId)) {
      return metadataDAO.getSuite(dbKey, correlationId);
    } else if (isValidName(suiteName)) {
      return metadataDAO.getLatestRun(dbKey, suiteName);
    } else {
      return null;
    }
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    addCors(resp);
  }

  private void addCors(HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept");
    response.setHeader("Access-Control-Allow-Methods", "POST");
  }

  @Activate
  public void start() {
    try {
      httpService.registerServlet(SERVLET_PATH, this, null, null);
    } catch (ServletException | NamespaceException e) {
      LOGGER.error("Failed to register servlet at " + SERVLET_PATH, e);
    }
  }

  @Deactivate
  public void stop() {
    httpService.unregister(SERVLET_PATH);
    httpService = null;
  }

}
