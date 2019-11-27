/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.rest;

import static com.cognifide.aet.rest.Helper.isValidCorrelationId;
import static com.cognifide.aet.rest.Helper.responseAsJson;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.models.AccessibilityError;
import com.cognifide.aet.models.JsError;
import com.cognifide.aet.models.StatusCodesError;
import com.cognifide.aet.models.W3cHtml5Error;
import com.cognifide.aet.rest.helpers.ErrorType;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class ErrorsServlet extends BasicDataServlet {

  private static final long serialVersionUID = 4312853975173807071L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorsServlet.class);

  @Reference
  private MetadataDAO metadataDAO;
  @Reference
  private ArtifactsDAO artifactsDAO;

  @Reference
  private transient HttpService httpService;

  @Override
  protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String correlationId = req.getParameter(Helper.CORRELATION_ID_PARAM);
    String testName = req.getParameter(Helper.TEST_RERUN_PARAM);

    Suite suite;
    try {
      if (isValidCorrelationId(correlationId)) {
        suite = metadataDAO.getSuite(dbKey, correlationId);
      } else {
        resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        resp.getWriter()
            .write(responseAsJson(GSON, "Invalid correlationId of suite was specified."));
        return;
      }
    } catch (StorageException e) {
      LOGGER.error("Failed to get suite", e);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(responseAsJson(GSON, "Failed to get suite %s", e.getMessage()));
      return;
    }

    if (suite != null) {
      Optional<Test> test = suite.getTests().stream()
          .filter(t -> t.getName().equals(testName)).findFirst();
      if (test.isPresent()) {
        String errorType = Helper.getErrorTypeFromRequest(req);
        List<Object> artifacts = processTest(test.get(), dbKey, errorType);

        resp.setContentType(Helper.APPLICATION_JSON_CONTENT_TYPE);
        resp.getWriter().write(GSON.toJson(artifacts));
      } else {
        createNotFoundTestResponse(resp, testName, dbKey);
      }
    } else {
      createNotFoundSuiteResponse(resp, correlationId, dbKey);
    }
  }

  private List<Object> processTest(Test test, DBKey dbKey, String errorType) throws IOException {
    List<Object> artifacts = new ArrayList<>();
    for (Url url : test.getUrls()) {
      processUrl(url, dbKey, artifacts, errorType, url.getName());
    }

    return artifacts;
  }

  private void processUrl(Url url, DBKey dbKey, List<Object> artifacts, String errorType,
      String urlName)
      throws IOException {
    if (errorType != null) {
      List<Step> steps = url.getSteps().stream()
          .filter(s -> s.getType().equals(errorType)).collect(Collectors.toList());
      if (steps.isEmpty()) {
        return;
      }
      for (Step step : steps) {
        processStep(step, dbKey, artifacts, errorType, urlName);
      }
    } else {
      for (Step step : url.getSteps()) {
        processStep(step, dbKey, artifacts, step.getType(), urlName);
      }
    }
  }

  private void processStep(Step step, DBKey dbKey, List<Object> artifacts, String errorType,
      String urlName)
      throws IOException {
    Type type = ErrorType.getTypeByName(errorType);
    if (step.getComparators() == null || step.getComparators().isEmpty()) {
      return;
    }

    for (Comparator comparator : step.getComparators()) {
      if (comparator.getStepResult().getStatus() == Status.PASSED) {
        continue;
      }
      if (errorType.equals(ErrorType.JS_ERRORS.getErrorName())) {
        Set<JsError> jsErrors = artifactsDAO.getJsonFormatArtifact(dbKey,
            comparator.getStepResult().getArtifactId(), type);
        jsErrors.forEach(er -> er.setType(ErrorType.JS_ERRORS.getErrorName()));
        artifacts.addAll(jsErrors);
      } else if (errorType.equals(ErrorType.STATUS_CODES.getErrorName())) {
        StatusCodesError sc = artifactsDAO.getJsonFormatArtifact(dbKey,
            comparator.getStepResult().getArtifactId(), type);
        sc.setUrlName(urlName);
        sc.setType(ErrorType.STATUS_CODES.getErrorName());
        artifacts.add(sc);
      } else if (errorType.equals(ErrorType.ACCESSIBILITY.getErrorName())) {
        AccessibilityError accessibilityError = artifactsDAO.getJsonFormatArtifact(dbKey,
            comparator.getStepResult().getArtifactId(), type);
        accessibilityError.setUrlName(urlName);
        accessibilityError.setType(ErrorType.ACCESSIBILITY.getErrorName());
        artifacts.add(accessibilityError);
      } else if(errorType.equals(ErrorType.SOURCE_W3CHTML5.getErrorName())) {
        W3cHtml5Error error = artifactsDAO.getJsonFormatArtifact(dbKey,
            comparator.getStepResult().getArtifactId(), type);
        error.setUrlName(urlName);
        error.setType(ErrorType.SOURCE_W3CHTML5.getErrorName());
        artifacts.add(error);
      }
    }
    //for now working for js-errors
//    if (errorType.equals(ErrorType.LAYOUT.getErrorName())) {
//      //todo wyciagamy dane z "data" (i moze cos z artifactId - maskArtifactId)
//
//    } else if (errorType.equals(ErrorType.SOURCE.getErrorName())) {
//      //todo wyciagamy dane z artifactId i cos z "data"
//    } else if (errorType.equals(ErrorType.JS_ERRORS.getErrorName())) {
//      for (Comparator comparator : step.getComparators()) {
//        if (comparator.getStepResult() != null
//            && comparator.getStepResult().getArtifactId() != null) {
//          artifacts.addAll(artifactsDAO.getJsonFormatArtifact(dbKey,
//              comparator.getStepResult().getArtifactId(), type));
//        }
//      }

  }

  private void createNotFoundTestResponse(HttpServletResponse response, String testName,
      DBKey dbKey)
      throws IOException {
    response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
    response.setContentType(Helper.APPLICATION_JSON_CONTENT_TYPE);
    response.getWriter().write(
        responseAsJson(GSON, "Unable to get test with name: %s for %s", testName, dbKey.toString())
    );
  }

  private void createNotFoundSuiteResponse(HttpServletResponse response, String correlationId,
      DBKey dbKey) throws IOException {
    response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
    response.setContentType(Helper.APPLICATION_JSON_CONTENT_TYPE);
    response.getWriter().write(
        responseAsJson(GSON, "Unable to get Suite Metadata with correlationId: %s for %s",
            correlationId, dbKey.toString())
    );
  }

  @Override
  protected HttpService getHttpService() {
    return httpService;
  }

  @Override
  protected void setHttpService(HttpService httpService) {
    this.httpService = httpService;
  }

  @Activate
  public void start() {
    register(Helper.getErrorsPath());
  }

  @Deactivate
  public void stop() {
    unregister(Helper.getErrorsPath());
  }
}
