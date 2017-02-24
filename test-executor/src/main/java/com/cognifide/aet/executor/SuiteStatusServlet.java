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

import com.google.gson.Gson;

import com.cognifide.aet.communication.api.suiteexecution.SuiteStatusResult;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Component(label = "SuiteStatusServlet", description = "Returns status of test suite processing",
    immediate = true)
public class SuiteStatusServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteStatusServlet.class);

  public static final String SERVLET_PATH = "/suitestatus";

  @Reference
  private HttpService httpService;

  @Reference
  private SuiteExecutor suiteExecutor;

  /**
   * Returns JSON with suite status defined by {@link SuiteStatusResult} for a given correlation ID.
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String correlationId = StringUtils.substringAfter(request.getRequestURI(), SERVLET_PATH).replace("/", "");
    SuiteStatusResult suiteStatusResult = suiteExecutor.getExecutionStatus(correlationId);

    if (suiteStatusResult != null) {
      Gson gson = new Gson();
      String responseBody = gson.toJson(suiteStatusResult);

      response.setStatus(200);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(responseBody);
    } else {
      response.sendError(404, "Suite status not found");
    }
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
