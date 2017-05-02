/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.datafilters.jserrorsfilter;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.datafilter.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.ParamsHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class JsErrorsFilter extends AbstractDataModifierJob<Set<JsErrorLog>> {

  public static final String NAME = "js-errors-filter";

  private static final String PARAM_ERROR = "errorPattern";

  private static final String PARAM_SOURCE = "sourcePattern";

  private static final String PARAM_LINE = "line";

  private Pattern errorMessage;

  private Pattern sourceFile;

  private Integer line;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    sourceFile = ParamsHelper.getAsPattern(PARAM_SOURCE, params);
    errorMessage = ParamsHelper.getAsPattern(PARAM_ERROR, params);
    line = ParamsHelper.getParamAsInteger(PARAM_LINE, params);
    ParamsHelper.atLeastOneIsProvided(errorMessage, sourceFile, line);
  }

  @Override
  public Set<JsErrorLog> modifyData(Set<JsErrorLog> data) throws ProcessingException {
    return FluentIterable.from(data).filter(new Predicate<JsErrorLog>() {
      @Override
      public boolean apply(@Nullable JsErrorLog input) {
        return !shouldFilterOut(input);
      }
    }).toSet();
  }

  private boolean shouldFilterOut(JsErrorLog jse) {
    return ParamsHelper.matches(sourceFile, jse.getSourceName())
        && ParamsHelper.matches(errorMessage, jse.getErrorMessage())
        && ParamsHelper.equalOrNotSet(line, jse.getLineNumber());
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: " + PARAM_SOURCE + ": " + sourceFile + " "
        + PARAM_ERROR + ": " + errorMessage + " " + PARAM_LINE + ": " + line;
  }

}
