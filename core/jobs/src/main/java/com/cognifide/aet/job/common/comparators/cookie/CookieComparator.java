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
package com.cognifide.aet.job.common.comparators.cookie;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.Cookie;

public class CookieComparator implements ComparatorJob {

  public static final String COMPARATOR_TYPE = "cookie";

  public static final String COMPARATOR_NAME = "cookie";

  private static final Function<Cookie, String> COOKIE_STRING_FUNCTION = new Function<Cookie, String>() {

    @Override
    public String apply(Cookie input) {
      String cookieName = null;
      if (input != null) {
        cookieName = input.getName();
      }
      return cookieName;
    }
  };

  private static final String ACTION_PARAMETER = "action";

  private static final String NAME_PARAMETER = "cookie-name";

  private static final String VALUE_PARAMETER = "cookie-value";

  private static final String SHOW_MATCHED = "showMatched";

  private static final Type COOKIES_SET_TYPE = new TypeToken<Set<Cookie>>() {
  }.getType();

  private final ComparatorProperties properties;

  private final ArtifactsDAO artifactsDAO;

  private CompareAction compareAction;

  private String name;

  private String value;

  private boolean showMatched = true;

  CookieComparator(ComparatorProperties properties, ArtifactsDAO artifactsDAO) {
    this.properties = properties;
    this.artifactsDAO = artifactsDAO;
  }

  @SuppressWarnings("unchecked")
  private ComparatorStepResult compareCookies(Set<Cookie> cookies) throws IOException {
    final Set<Cookie> cookiesPattern = artifactsDAO
        .getJsonFormatArtifact(properties, properties.getPatternId(),
            COOKIES_SET_TYPE);

    final Set<String> collectedCookiesNames = FluentIterable.from(cookies)
        .transform(COOKIE_STRING_FUNCTION).toSet();
    final Set<String> patternCookiesNames = FluentIterable.from(cookiesPattern)
        .transform(COOKIE_STRING_FUNCTION).toSet();

    Set<String> additionalCookies = Sets.difference(collectedCookiesNames, patternCookiesNames);
    Set<String> notFoundCookies = Sets.difference(patternCookiesNames, collectedCookiesNames);
    Set<String> foundCookies = Collections.emptySet();
    if (showMatched) {
      foundCookies = Sets.intersection(patternCookiesNames, collectedCookiesNames);
    }
    boolean compareResult = additionalCookies.isEmpty() && notFoundCookies.isEmpty();
    CookieCompareComparatorResult result = new CookieCompareComparatorResult(compareAction, cookies,
        notFoundCookies,
        additionalCookies, foundCookies);

    final String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, result);
    return new ComparatorStepResult(artifactId,
        compareResult ? ComparatorStepResult.Status.PASSED : ComparatorStepResult.Status.FAILED,
        !compareResult);
  }

  private ComparatorStepResult testCookie(Set<Cookie> cookies) {
    boolean testResult = false;
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(name) && (value == null || value.equals(cookie.getValue()))) {
        testResult = true;
        break;
      }
    }

    CookieTestComparatorResult result = new CookieTestComparatorResult(compareAction, cookies, name,
        value);
    final String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, result);
    return new ComparatorStepResult(artifactId,
        testResult ? ComparatorStepResult.Status.PASSED : ComparatorStepResult.Status.FAILED);
  }

  private ComparatorStepResult listCookies(Set<Cookie> cookies) {
    CookieComparatorResult result = new CookieComparatorResult(compareAction, cookies);
    final String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, result);
    return new ComparatorStepResult(artifactId, ComparatorStepResult.Status.PASSED);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ComparatorStepResult compare() throws ProcessingException {
    ComparatorStepResult result;
    try {
      final Set<Cookie> cookies = artifactsDAO
          .getJsonFormatArtifact(properties, properties.getCollectedId(),
              COOKIES_SET_TYPE);
      switch (compareAction) {
        case TEST:
          result = testCookie(cookies);
          break;
        case COMPARE:
          result = compareCookies(cookies);
          break;
        case LIST:
        default:
          result = listCookies(cookies);
          break;
      }
      result.addData("compareAction", compareAction.name());
    } catch (IOException e) {
      throw new ProcessingException("Error while obtaining cookies from " + properties, e);
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    compareAction = CompareAction.fromString(params.get(ACTION_PARAMETER));
    name = params.get(NAME_PARAMETER);
    value = params.get(VALUE_PARAMETER);
    if (CompareAction.TEST.equals(compareAction) && name == null) {
      String message = String.format("Missing %s", NAME_PARAMETER);
      throw new ParametersException(message);
    }
    if (params.containsKey(SHOW_MATCHED)) {
      this.showMatched = Boolean.valueOf(params.get(SHOW_MATCHED));
    }
  }

}
