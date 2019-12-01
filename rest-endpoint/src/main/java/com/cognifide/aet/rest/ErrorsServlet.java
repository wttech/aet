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
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.common.comparators.cookie.CookieComparatorResult;
import com.cognifide.aet.job.common.comparators.cookie.CookieCompareComparatorResult;
import com.cognifide.aet.job.common.comparators.cookie.CookieTestComparatorResult;
import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta;
import com.cognifide.aet.models.AccessibilityErrorWrapper;
import com.cognifide.aet.models.CookieErrorWrapper;
import com.cognifide.aet.models.JsErrorWrapper;
import com.cognifide.aet.models.ScreenErrorWrapper;
import com.cognifide.aet.models.SourceErrorWrapper;
import com.cognifide.aet.models.StatusCodesErrorWrapper;
import com.cognifide.aet.models.W3cHtml5ErrorWrapper;
import com.cognifide.aet.rest.helpers.ErrorType;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, List<Object>> artifacts = processTest(test.get(), dbKey, errorType);

        resp.setContentType(Helper.APPLICATION_JSON_CONTENT_TYPE);
        resp.getWriter().write(GSON.toJson(artifacts));
      } else {
        createNotFoundTestResponse(resp, testName, dbKey);
      }
    } else {
      createNotFoundSuiteResponse(resp, correlationId, dbKey);
    }
  }

  private Map<String, List<Object>> processTest(Test test, DBKey dbKey, String errorType)
      throws IOException {
    Map<String, List<Object>> artifacts = new HashMap<>();
    for (Url url : test.getUrls()) {
      processUrl(url, dbKey, artifacts, errorType, url.getName());
    }

    return artifacts;
  }

  private void processUrl(Url url, DBKey dbKey, Map<String, List<Object>> artifacts,
      String errorType,
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

  private void processStep(Step step, DBKey dbKey, Map<String, List<Object>> artifacts,
      String errorType,
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
        Set<JsErrorLog> jsErrors = artifactsDAO.getJsonFormatArtifact(dbKey,
            comparator.getStepResult().getArtifactId(), type);
        JsErrorWrapper jsErrorWrapper = new JsErrorWrapper(jsErrors, urlName);

        mergeMap(artifacts, ErrorType.JS_ERRORS.getErrorName(), jsErrorWrapper);
      } else if (errorType.equals(ErrorType.STATUS_CODES.getErrorName())) {
        StatusCodesErrorWrapper sc = artifactsDAO.getJsonFormatArtifact(dbKey,
            comparator.getStepResult().getArtifactId(), type);
        sc.setUrlName(urlName);

        mergeMap(artifacts, ErrorType.STATUS_CODES.getErrorName(), sc);
      } else if (errorType.equals(ErrorType.ACCESSIBILITY.getErrorName())) {
        AccessibilityErrorWrapper accessibilityError = artifactsDAO.getJsonFormatArtifact(dbKey,
            comparator.getStepResult().getArtifactId(), type);
        accessibilityError.setUrlName(urlName);

        mergeMap(artifacts, ErrorType.ACCESSIBILITY.getErrorName(), accessibilityError);
      } else if (errorType.equals(ErrorType.SCREEN.getErrorName())) {
        ScreenErrorWrapper screenError = new ScreenErrorWrapper(step.getName(),
            comparator.getStepResult().getData(), urlName);
        mergeMap(artifacts, ErrorType.SCREEN.getErrorName(), screenError);
      } else if (errorType.equals("cookie")) {
        if(comparator.getParameters().get("action").equals("compare")) {
          type = ErrorType.COOKIE_COMPARE.getType();
          CookieCompareComparatorResult result = artifactsDAO.getJsonFormatArtifact(dbKey,
              comparator.getStepResult().getArtifactId(), type);
          CookieErrorWrapper cookieError = new CookieErrorWrapper(result, urlName);
          mergeMap(artifacts, "cookie", cookieError);
        } else if(comparator.getParameters().get("action").equals("test")) {
          type = ErrorType.COOKIE_TEST.getType();
          CookieTestComparatorResult result = artifactsDAO.getJsonFormatArtifact(dbKey,
              comparator.getStepResult().getArtifactId(), type);
          CookieErrorWrapper cookieError = new CookieErrorWrapper(result, urlName);
          mergeMap(artifacts, "cookie", cookieError);
        }
      } else if (errorType.equals("source")) {
        if (comparator.getParameters().get("comparator").equals("w3c-html5")) {
          type = ErrorType.SOURCE_W3CHTML5.getType();
          W3cHtml5ErrorWrapper error = artifactsDAO.getJsonFormatArtifact(dbKey,
              comparator.getStepResult().getArtifactId(), type);
          error.setUrlName(urlName);

          mergeMap(artifacts, "source_w3c-html5", error);
        } else if (comparator.getParameters().get("comparator").equals("source")) {
          type = ErrorType.SOURCE.getType();
          Map<String, List<ResultDelta>> result = artifactsDAO.getJsonFormatArtifact(dbKey,
              comparator.getStepResult().getArtifactId(), type);
          SourceErrorWrapper error = new SourceErrorWrapper(result, urlName,
              comparator.getStepResult().getData());
          mergeMap(artifacts, "source_source", error);
        }
      }
    }
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

  private void mergeMap(Map<String, List<Object>> map, String errorType, Object object) {
    map.merge(errorType, new ArrayList<>(Collections.singletonList(object)),
        (old, error) -> {
          old.addAll(error);
          return old;
        });
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
