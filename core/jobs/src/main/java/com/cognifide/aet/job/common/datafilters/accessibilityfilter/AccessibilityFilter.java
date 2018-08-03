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
package com.cognifide.aet.job.common.datafilters.accessibilityfilter;

import com.cognifide.aet.job.api.datafilter.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.utils.ParamsHelper;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AccessibilityFilter extends AbstractDataModifierJob<List<AccessibilityIssue>> {

  public static final String NAME = "accessibility-filter";

  static final String PARAM_PRINCIPLE = "principle";

  static final String PARAM_LINE = "line";

  static final String PARAM_COLUMN = "column";

  static final String PARAM_ERROR = "error";

  static final String PARAM_ERROR_PATTERN = "errorPattern";

  static final String PARAM_MARKUP_CSS_SELECTOR = "markupCss";

  private Pattern errorMessagePattern;

  private String principle;

  private Integer line;

  private Integer column;

  private String markupCssSelector;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    errorMessagePattern = ParamsHelper
        .getPatternFromPatternParameterOrPlainText(PARAM_ERROR_PATTERN, PARAM_ERROR, params);
    principle = ParamsHelper.getParamAsString(PARAM_PRINCIPLE, params);
    line = ParamsHelper.getParamAsInteger(PARAM_LINE, params);
    column = ParamsHelper.getParamAsInteger(PARAM_COLUMN, params);
    markupCssSelector = ParamsHelper.getParamAsString(PARAM_MARKUP_CSS_SELECTOR, params);

    ParamsHelper
        .atLeastOneIsProvided(principle, errorMessagePattern, line, column, markupCssSelector);
  }

  @Override
  public List<AccessibilityIssue> modifyData(List<AccessibilityIssue> data)
      throws ProcessingException {
    for (AccessibilityIssue issue : data) {
      if (ParamsHelper.equalOrNotSet(principle, issue.getCode())
          && ParamsHelper.matches(errorMessagePattern, issue.getMessage())
          && ParamsHelper.equalOrNotSet(line, issue.getLineNumber())
          && ParamsHelper.equalOrNotSet(column, issue.getColumnNumber())
          && matchesCssSelector(issue.getElementString())) {
        issue.exclude();
      }
    }
    return data;
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: " + PARAM_PRINCIPLE + ": " + principle + " "
        + PARAM_ERROR_PATTERN + ": " + errorMessagePattern + " " + PARAM_LINE + ": " + line + " "
        + PARAM_COLUMN + ": "
        + column;
  }

  private boolean matchesCssSelector(String markupFragment) {
    boolean result = true;
    if (markupCssSelector != null) {
      Document markup = Jsoup.parse(markupFragment);
      Elements elements = markup.select(markupCssSelector);
      result = !elements.isEmpty();
    }
    return result;
  }

}
