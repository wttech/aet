/*
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.executor;

import com.google.gson.JsonObject;
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
@Component(label = "SuiteRerunServlet", description = "Executes received test", immediate = true)
public class SuiteRerunServlet extends HttpServlet {

  private static final long serialVersionUID = -4708227978736783811L;

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerunServlet.class);
  private static final String SERVLET_PATH = "/suite-rerun";
  private static final String SUITE_PARAM = "suite";
  private static final String TEST_PARAM = "test";
  private static final String COMPANY_PARAM = "company";
  private static final String PROJECT_PARAM = "project";

  @Reference
  private HttpService httpService;

  @Reference
  private SuiteExecutor suiteExecutor;


  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    addCors(response);
    Map<String, String> requestData = getRequestData(request);
    final String suite = requestData.get(SUITE_PARAM);
    final String company = requestData.get(COMPANY_PARAM);
    final String project = requestData.get(PROJECT_PARAM);
    final String test = requestData.get(TEST_PARAM);

    try {
      response.setStatus(HttpStatus.SC_OK);
      response.setContentType("application/json");
      response.setCharacterEncoding(CharEncoding.UTF_8);
      JsonObject jo = new JsonObject();
      jo.addProperty("message", "ok");
      jo.addProperty("test",test);
      response.getWriter().write(jo.toString());
    } catch (Exception e) {
      response.sendError(HttpStatus.SC_BAD_REQUEST,
          "No suite found for name " + suite + ", project " + project + ", company " + company);
    }
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept");
    resp.setHeader("Access-Control-Allow-Methods", "POST");
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
