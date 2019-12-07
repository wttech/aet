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
package com.cognifide.aet.services;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.common.comparators.cookie.CookieCompareComparatorResult;
import com.cognifide.aet.job.common.comparators.cookie.CookieTestComparatorResult;
import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta;
import com.cognifide.aet.models.AccessibilityErrorWrapper;
import com.cognifide.aet.models.CookieErrorWrapper;
import com.cognifide.aet.models.ErrorsMap;
import com.cognifide.aet.models.JsErrorWrapper;
import com.cognifide.aet.models.ScreenErrorWrapper;
import com.cognifide.aet.models.SourceErrorWrapper;
import com.cognifide.aet.models.StatusCodesErrorWrapper;
import com.cognifide.aet.models.W3cHtml5ErrorWrapper;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ErrorsService.class, immediate = true)
public class ErrorsService {

  @Reference
  private ArtifactsDAO artifactsDAO;

  public ErrorsMap getErrorsFromTest(Test test, DBKey dbKey, String errorType) throws IOException {
    ErrorsMap errorsMap = new ErrorsMap();
    for (Url url : test.getUrls()) {
      processUrl(url, dbKey, errorsMap, errorType);
    }

    return errorsMap;
  }

  private void processUrl(Url url, DBKey dbKey, ErrorsMap errorsMap, String errorType)
      throws IOException {
    if (errorType != null) {
      List<Step> steps = url.getSteps().stream()
          .filter(s -> s.getType().equals(errorType)).collect(Collectors.toList());
      if (steps.isEmpty()) {
        return;
      }
      for (Step step : steps) {
        processStep(step, dbKey, errorsMap, errorType, url.getName());
      }
    } else {
      for (Step step : url.getSteps()) {
        processStep(step, dbKey, errorsMap, step.getType(), url.getName());
      }
    }
  }

  private void processStep(Step step, DBKey dbKey, ErrorsMap errorsMap, String errorType,
      String urlName) throws IOException {
    if (step.getComparators() == null || step.getComparators().isEmpty()) {
      return;
    }

    for (Comparator comparator : step.getComparators()) {
      if (comparator.getStepResult().getStatus() == Status.PASSED) {
        continue;
      }
      switch (errorType) {
        case JsErrorWrapper.ERROR_TYPE:
          processJsErrors(comparator, dbKey, urlName, errorsMap);
          break;
        case StatusCodesErrorWrapper.ERROR_TYPE:
          processStatusCodesErrors(comparator, dbKey, urlName, errorsMap);
          break;
        case AccessibilityErrorWrapper.ERROR_TYPE:
          processAccessibilityErrors(comparator, dbKey, urlName, errorsMap);
          break;

        case ScreenErrorWrapper.ERROR_TYPE:
          processScreenErrors(comparator, step, urlName, errorsMap);
          break;

        case CookieErrorWrapper.ERROR_TYPE:
          processCookieErrors(comparator, dbKey, urlName, errorsMap);
          break;

        case SourceErrorWrapper.ERROR_TYPE:
          processSourceErrors(comparator, dbKey, urlName, errorsMap);
          break;
      }
    }
  }

  private void processJsErrors(Comparator comparator, DBKey dbKey, String urlName,
      ErrorsMap errorsMap) throws IOException {
    Set<JsErrorLog> jsErrors = artifactsDAO.getJsonFormatArtifact(dbKey,
        comparator.getStepResult().getArtifactId(), JsErrorWrapper.ARTIFACT_TYPE);
    JsErrorWrapper jsErrorWrapper = new JsErrorWrapper(jsErrors, urlName);

    errorsMap.mergeMap(JsErrorWrapper.ERROR_TYPE, jsErrorWrapper);
  }

  private void processStatusCodesErrors(Comparator comparator, DBKey dbKey, String urlName,
      ErrorsMap errorsMap) throws IOException {
    StatusCodesErrorWrapper statusCodesErrors = artifactsDAO.getJsonFormatArtifact(dbKey,
        comparator.getStepResult().getArtifactId(), StatusCodesErrorWrapper.ARTIFACT_TYPE);
    statusCodesErrors.setUrlName(urlName);

    errorsMap.mergeMap(StatusCodesErrorWrapper.ERROR_TYPE, statusCodesErrors);
  }

  private void processAccessibilityErrors(Comparator comparator, DBKey dbKey, String urlName,
      ErrorsMap errorsMap) throws IOException {
    AccessibilityErrorWrapper accessibilityErrors = artifactsDAO.getJsonFormatArtifact(dbKey,
        comparator.getStepResult().getArtifactId(), AccessibilityErrorWrapper.ARTIFACT_TYPE);
    accessibilityErrors.setUrlName(urlName);

    errorsMap.mergeMap(AccessibilityErrorWrapper.ERROR_TYPE, accessibilityErrors);
  }

  private void processScreenErrors(Comparator comparator, Step step, String urlName,
      ErrorsMap errorsMap) {
    ScreenErrorWrapper screenError = new ScreenErrorWrapper(step.getName(),
        comparator.getStepResult().getData(), urlName);

    errorsMap.mergeMap(ScreenErrorWrapper.ERROR_TYPE, screenError);
  }

  private void processCookieErrors(Comparator comparator, DBKey dbKey, String urlName,
      ErrorsMap errorsMap) throws IOException {
    String action = comparator.getParameters().get(CookieErrorWrapper.ACTION_PARAM);
    CookieErrorWrapper cookieError = null;
    if (action.equals(CookieErrorWrapper.ACTION_COMPARE)) {
      CookieCompareComparatorResult result = artifactsDAO.getJsonFormatArtifact(dbKey,
          comparator.getStepResult().getArtifactId(),
          CookieErrorWrapper.ARTIFACT_COOKIE_COMPARE_TYPE);
      cookieError = new CookieErrorWrapper(result, urlName);
    } else if (action.equals("test")) {
      CookieTestComparatorResult result = artifactsDAO.getJsonFormatArtifact(dbKey,
          comparator.getStepResult().getArtifactId(), CookieErrorWrapper.ARTIFACT_COOKIE_TEST_TYPE);
      cookieError = new CookieErrorWrapper(result, urlName);
    }

    if (cookieError != null) {
      errorsMap.mergeMap(CookieErrorWrapper.ERROR_TYPE, cookieError);
    }
  }

  private void processSourceErrors(Comparator comparator, DBKey dbKey, String urlName,
      ErrorsMap errorsMap) throws IOException {
    String comparatorType = comparator.getParameters().get(SourceErrorWrapper.COMPARATOR_PARAM);
    if (comparatorType.equals(W3cHtml5ErrorWrapper.COMPARATOR_TYPE)) {
      W3cHtml5ErrorWrapper error = artifactsDAO.getJsonFormatArtifact(dbKey,
          comparator.getStepResult().getArtifactId(), W3cHtml5ErrorWrapper.ARTIFACT_TYPE);
      error.setUrlName(urlName);

      errorsMap.mergeMap(W3cHtml5ErrorWrapper.RESULT_KEY, error);
    } else if (comparatorType.equals(SourceErrorWrapper.COMPARATOR_TYPE)) {
      Map<String, List<ResultDelta>> result = artifactsDAO.getJsonFormatArtifact(dbKey,
          comparator.getStepResult().getArtifactId(), SourceErrorWrapper.ARTIFACT_TYPE);
      SourceErrorWrapper error = new SourceErrorWrapper(result, urlName,
          comparator.getStepResult().getData());

      errorsMap.mergeMap(SourceErrorWrapper.ERROR_TYPE, error);
    }
  }
}
