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

import com.cognifide.aet.communication.api.suiteexecution.SuiteExecutionResult;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Component(label = "SuiteServlet", description = "Executes received test suite", immediate = true, metatype = true)
public class SuiteServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteServlet.class);

  private static final String SERVLET_PATH = "/suite";

  private static final String SUITE_PARAM = "suite";

  private static final String DOMAIN_PARAM = "domain";

  @Reference
  private HttpService httpService;

  @Reference
  private SuiteExecutor suiteExecutor;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (ServletFileUpload.isMultipartContent(request)) {
      Map<String, String> requestData = getRequestData(request);
      String suite = requestData.get(SUITE_PARAM);
      String domain = requestData.get(DOMAIN_PARAM);
      String endpointDomain = StringUtils.substringBefore(request.getRequestURL().toString(), SERVLET_PATH);

      if (StringUtils.isNotBlank(suite)) {
        SuiteExecutionResult suiteExecutionResult = suiteExecutor.execute(suite, domain, endpointDomain);
        Gson gson = new Gson();
        String responseBody = gson.toJson(suiteExecutionResult);

        if (suiteExecutionResult.getErrorMessage() == null) {
          response.setStatus(200);
          response.setContentType("application/json");
          response.setCharacterEncoding(CharEncoding.UTF_8);
          response.getWriter().write(responseBody);
        } else {
          response.sendError(500, suiteExecutionResult.getErrorMessage());
        }
      } else {
        response.sendError(400, "Request does not contain the test suite");
      }
    } else {
      response.sendError(400, "Request content is incorrect");
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

  private Map<String, String> getRequestData(HttpServletRequest request) {
    Map<String, String> requestData = new HashMap<>();

    ServletFileUpload upload = new ServletFileUpload();
    try {
      FileItemIterator itemIterator = upload.getItemIterator(request);
      while (itemIterator.hasNext()) {
        FileItemStream item = itemIterator.next();
        InputStream itemStream = item.openStream();
        String value = Streams.asString(itemStream, CharEncoding.UTF_8);
        requestData.put(item.getFieldName(), value);
      }
    } catch (FileUploadException | IOException e) {
      LOGGER.error("Failed to process request", e);
    }

    return requestData;
  }
}
