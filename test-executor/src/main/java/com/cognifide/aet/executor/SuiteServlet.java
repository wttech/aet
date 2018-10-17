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
import com.cognifide.aet.executor.http.HttpSuiteExecutionResultWrapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = HttpServlet.class, immediate = true)
public class SuiteServlet extends HttpServlet {

  private static final long serialVersionUID = 5266041156537459410L;

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteServlet.class);
  private static final String SERVLET_PATH = "/suite";
  private static final String SUITE_PARAM = "suite";
  private static final String NAME_PARAM = "name";
  private static final String DOMAIN_PARAM = "domain";
  private static final String PATTERN_CORRELATION_ID_PARAM = "pattern";
  private static final String PATTERN_SUITE_PARAM = "patternSuite";
  private static final String PROJECT_CHECK_SUM = "projectChecksSum";


  @Reference
  private HttpService httpService;

  @Reference
  private SuiteExecutor suiteExecutor;

  /**
   * Starts processing of the test suite defined in the XML file provided in post body. Overrides
   * domain specified in the suite file if one has been provided in post body. Returns JSON defined
   * by {@link SuiteExecutionResult}. The request's content type must be 'multipart/form-data'.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    if (ServletFileUpload.isMultipartContent(request)) {
      Map<String, String> requestData = getRequestData(request);
      final String suite = requestData.get(SUITE_PARAM);
      final String name = requestData.get(NAME_PARAM);
      final String domain = requestData.get(DOMAIN_PARAM);
      final String patternCorrelationId = requestData.get(PATTERN_CORRELATION_ID_PARAM);
      final String patternSuite = requestData.get(PATTERN_SUITE_PARAM);
      final String projectCheckSum = requestData.get(PROJECT_CHECK_SUM);

      if (StringUtils.isNotBlank(suite)) {
        HttpSuiteExecutionResultWrapper resultWrapper = suiteExecutor
            .execute(suite, name, domain, patternCorrelationId, patternSuite,projectCheckSum);
        final SuiteExecutionResult suiteExecutionResult = resultWrapper.getExecutionResult();
        Gson gson = new Gson();

        String responseBody = gson.toJson(suiteExecutionResult);

        if (resultWrapper.hasError()) {
          response.sendError(resultWrapper.getStatusCode(),
              suiteExecutionResult.getErrorMessage());
        } else {
          response.setStatus(HttpStatus.SC_OK);
          response.setContentType("application/json");
          response.setCharacterEncoding(CharEncoding.UTF_8);
          response.getWriter().write(responseBody);
        }
      } else {
        response.sendError(HttpStatus.SC_BAD_REQUEST, "Request does not contain the test suite");
      }
    } else {
      response.sendError(HttpStatus.SC_BAD_REQUEST, "Request content is incorrect");
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
