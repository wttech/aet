/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.aet.rest;


import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class ResetSuiteServlet extends BasicDataServlet {

  private static final long serialVersionUID = 4354305495282724069L;

  private static final String URL_TO_RESET_PATTERN = "urlToResetPattern";

  @Reference
  private ResetService resetService;

  @Reference
  private transient HttpService httpService;

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ResetSuiteServlet.class);

  private static final Gson GSON = new Gson();

  @Activate
  public void start() {
    register(Helper.getResetPath());
  }

  @Deactivate
  public void stop() {
    unregister(Helper.getResetPath());//    /api/metadata/reset
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse resp)
      throws IOException {

    String url = request.getParameter(URL_TO_RESET_PATTERN).trim();

    Suite suite = getSuite(request);

    try {
      resetService.resetPattern(suite, url);
      LOGGER.info("The suite pattern has been reset {}", suite);
      resp.setStatus(HttpURLConnection.HTTP_OK);
    } catch (IllegalStateException illegalStateException) {
      LOGGER.error("Invalid Suite representation: {}", illegalStateException.getMessage());
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(
          responseAsJson("Invalid Suite representation : %s", illegalStateException.getMessage()));
    } catch (ValidatorException validatorException) {
      LOGGER.error("Invalid json provided: {}", validatorException.getIssues(),
          validatorException);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(
          responseAsJson("Invalid Suite representation : %s", validatorException.getIssues()));
    } catch (StorageException e) {
      LOGGER.error("Failed to save suite", e);
      resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      resp.getWriter()
          .write(responseAsJson("ERROR: Unable to save provided Suite. %s", e.getMessage()));
    } finally {
      resp.flushBuffer();
    }

  }

  private Suite getSuite(HttpServletRequest req) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(
          new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      LOGGER.error("Cant read suite form request", e);
    }
    return Suite.fromJson(reader);
  }

  private static String responseAsJson(String format, Object... args) {
    return GSON.toJson(new ErrorMessage(format, args));
  }

  @Override
  protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp) {
  }

  @Override
  protected HttpService getHttpService() {
    return this.httpService;
  }

  @Override
  protected void setHttpService(HttpService httpService) {
    this.httpService = httpService;
  }

}