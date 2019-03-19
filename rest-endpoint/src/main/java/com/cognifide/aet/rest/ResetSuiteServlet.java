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


import static com.cognifide.aet.communication.api.metadata.Step.SCREEN;
import static com.cognifide.aet.rest.Helper.isValidCorrelationId;
import static com.cognifide.aet.rest.Helper.isValidName;
import static com.cognifide.aet.rest.Helper.responseAsJson;

import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
//@Component(service = BasicDataServlet.class, immediate = true)
public class ResetSuiteServlet extends BasicDataServlet {
  private static final long serialVersionUID = 4354305495282724069L;

  @Reference
  private MetadataDAO metadataDAO;

  @Reference
  private transient HttpService httpService;

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ResetSuiteServlet.class);

  protected static final Gson GSON = new Gson();

  private static final Gson PRETTY_PRINT_GSON = new GsonBuilder().setPrettyPrinting().create();

  private static final JsonParser JSON_PARSER = new JsonParser();

  private static final String FORMATTED_PARAM = "formatted";

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
      throws ServletException, IOException {

    String url = request.getParameter("urlToResetPattern").trim();

    Suite suite = getSuite(request);

    try {
      resetPattern(suite, url);
      LOGGER.info("The suite pattern has been reset {}",suite.toString());
      resp.setStatus(HttpURLConnection.HTTP_OK);
    } catch (ValidatorException validatorException) {
      LOGGER.error("Invalid json provided: {}", validatorException.getIssues(),
          validatorException);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(
          responseAsJson(GSON, "Invalid Suite representation : %s", validatorException.getIssues()));
    } catch (StorageException e) {
      LOGGER.error("Failed to save suite", e);
      resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      resp.getWriter()
          .write(responseAsJson(GSON, "ERROR: Unable to save provided Suite. %s", e.getMessage()));
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
      e.printStackTrace();
    }
    return Suite.fromJson(reader);
  }

  private static String responseAsJson(Gson GSON, String format, Object... args) {
    return GSON.toJson(new ErrorMessage(format, args));
  }

  @Override
  protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp) throws IOException {
  }

  @Override
  protected HttpService getHttpService() {
    return this.httpService;
  }

  @Override
  protected void setHttpService(HttpService httpService) {
    this.httpService = httpService;
  }


  private Suite resetPattern(Suite suite, String url) throws StorageException, ValidatorException {
    List<Test> tests = suite.getTests();
    if ((tests!= null && !tests.isEmpty())) {
    Test test = getLatestTest(tests);//todo get last test? add id null
    Set<Url> urls = test.getUrls();
    Url selectedUrl = urls.stream().filter(u -> url.equals(u.getUrl())).findFirst().get();
    Step selectedStep = selectedUrl.getSteps().stream().filter(step -> SCREEN.equals(step.getName())).findFirst().get();
    selectedStep.replacePatternWithCurrentPattern();
    }else{
      LOGGER.warn(String.format("Suite test has not contains any element: %s",suite.toString()));
      throw new IllegalStateException(String.format("Suite test has not element: %s",suite.toString()));
    }


    return metadataDAO.updateSuite(suite);
  }

  private Test getLatestTest(List<Test> tests) {
    return tests.get(tests.size()-1);
  }

  private void createNotFoundResponse(HttpServletResponse response, String correlationId, String suiteName, String suiteVersion, DBKey dbKey) throws IOException {
    response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
    String paramsValuesMessage = "";
    if (correlationId != null) {
      paramsValuesMessage = String.format("correlationId: %s", correlationId);
    } else if (suiteName != null) {
      paramsValuesMessage = String.format("suite name: %s", suiteName);
      if (suiteVersion != null) {
        paramsValuesMessage = String.format("%s, version: %s", paramsValuesMessage, suiteVersion);
      }
    }
    response.getWriter()
        .write(responseAsJson(GSON, "Unable to get Suite Metadata with %s for %s", paramsValuesMessage, dbKey.toString()));
  }

}