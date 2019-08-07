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

import com.cognifide.aet.communication.api.execution.SuiteExecutionResult;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.executor.http.HttpSuiteExecutionResultWrapper;
import com.cognifide.aet.executor.http.RerunDataWrapper;
import com.cognifide.aet.rest.Helper;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpStatus;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;

@Component(immediate = true)
public class SuiteRerunServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerunServlet.class);
  private static final String SERVLET_PATH = Helper.getRerunPath();
  private static final long serialVersionUID = -2644460433789203661L;

  @Reference
  private HttpService httpService;

  @Reference
  private SuiteExecutor suiteExecutor;

  @Reference
  private transient MetadataDAO metadataDAO;

  private static final Gson GSON = new Gson();


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException {

    addCors(response);

    try {
      RerunDataWrapper rerunDataWrapper = GSON.fromJson(request.getReader(), RerunDataWrapper.class);

      if (rerunDataWrapper != null) {
        DBKey dbKey = Helper.getDBKey(rerunDataWrapper.getCompany(), rerunDataWrapper.getProject());
        Run objectToRunWrapper = SuiteRerun
                .getAndPrepareObject(metadataDAO, dbKey, rerunDataWrapper);

        if (objectToRunWrapper != null) {
          createResponse(objectToRunWrapper, response);
        } else {
          response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
      } else {
        response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      }
    } catch (ValidatorException | JsonSyntaxException | JMSException e) {
      LOGGER.error("Validation problem!", e);
      response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
    }
  }

  private void createResponse(Run objectToRunWrapper, HttpServletResponse response)
          throws IOException, ValidatorException, JMSException {

    HttpSuiteExecutionResultWrapper resultWrapper = suiteExecutor.executeSuite(objectToRunWrapper);
    SuiteExecutionResult suiteExecutionResult = resultWrapper.getExecutionResult();
    String responseBody = GSON.toJson(suiteExecutionResult);

    if (resultWrapper.hasError()) {
      response.sendError(resultWrapper.getStatusCode(), suiteExecutionResult.getErrorMessage());
    } else {
      response.setStatus(HttpStatus.SC_OK);
      response.setContentType("application/json");
      response.setCharacterEncoding(Charsets.UTF_8.name());
      response.getWriter().write(responseBody);
    }
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
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
