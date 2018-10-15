/**
 * AET
 *
 * Copyright (C) 2018 Cognifide Limited
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
package com.cognifide.aet.job.common.modifiers.scroll;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

class ParametersParser {

  private static final String TOP = "window.scrollTo(0, 0);";
  private static final String BOTTOM = "window.scrollTo(0, document.body.scrollHeight);";
  private static final String CSS = "document.querySelector(\"%s\").scrollIntoView();";
  private static final String XPATH = "document.evaluate(\"%s\", document, null, "
      + "XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.scrollIntoView();";

  private static final String POSITION_PARAM_NAME = "position";
  private static final String CSS_PARAM_NAME = "css";
  private static final String XPATH_PARAM_NAME = "xpath";

  private String jsCommand = BOTTOM;

  void setParameters(Map<String, String> parameters) throws ParametersException {
    jsCommand = getOptionalSnippetFromParams(parameters)
        .orElse(BOTTOM);
  }

  String getJavaScriptSnippet() {
    return jsCommand;
  }

  private Optional<String> getOptionalSnippetFromParams(Map<String, String> parameters)
      throws ParametersException {

    if (parameters.keySet().size() > 1) {
      throw new ParametersException("Only one parameter should be provided: "
          + "position (top/bottom), css, xpath");
    }

    Optional<String> specificPosition = getSpecificPositionSnippet(parameters);

    Optional<String> css = Optional.ofNullable(parameters.get(CSS_PARAM_NAME))
        .map(selector -> String.format(CSS, selector));

    Optional<String> xpath = Optional.ofNullable(parameters.get(XPATH_PARAM_NAME))
        .map(selector -> String.format(XPATH, selector));

    return specificPosition.isPresent() ? specificPosition
        : css.isPresent() ? css
            : xpath;
  }

  private Optional<String> getSpecificPositionSnippet(Map<String, String> parameters)
      throws ParametersException {

    String positionParamValue = parameters.get(POSITION_PARAM_NAME);
    String snippet = null;
    if (StringUtils.isNotBlank(positionParamValue)) {
      snippet = getTopBottomPositionSnippet(positionParamValue);
    }
    return Optional.ofNullable(snippet);
  }

  private String getTopBottomPositionSnippet(String position) throws ParametersException {
    String snippet;
    switch (position.toLowerCase()) {
      case "top":
        snippet = TOP;
        break;
      case "bottom":
        snippet = BOTTOM;
        break;
      default:
        throw new ParametersException("Invalid 'position' value, only 'top' / 'bottom' available");
    }
    return snippet;
  }

}
