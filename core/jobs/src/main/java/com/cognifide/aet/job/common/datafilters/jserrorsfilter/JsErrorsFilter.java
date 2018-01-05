/**
 * AET
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
import org.apache.commons.lang3.StringUtils;

public class JsErrorsFilter extends AbstractDataModifierJob<Set<JsErrorLog>> {

  public static final String NAME = "js-errors-filter";

  private static final String PARAM_ERROR = "error";

  private static final String PARAM_ERROR_PATTERN = "errorPattern";

  private static final String PARAM_SOURCE = "source";

  private static final String PARAM_LINE = "line";

  private Pattern errorMessagePattern;

  private String sourceFile;

  private Integer line;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    errorMessagePattern = ParamsHelper
        .getPatternFromPatternParameterOrPlainText(PARAM_ERROR_PATTERN, PARAM_ERROR, params);
    line = ParamsHelper.getParamAsInteger(PARAM_LINE, params);

    sourceFile = ParamsHelper.getParamAsString(PARAM_SOURCE, params);

    ParamsHelper.atLeastOneIsProvided(errorMessagePattern, sourceFile, line);
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
    String source = jse.getSourceName();
    return shouldExcludeRegardingSource(source)
        && ParamsHelper.matches(errorMessagePattern, jse.getErrorMessage())
        && ParamsHelper.equalOrNotSet(line, jse.getLineNumber());
  }

  private boolean shouldExcludeRegardingSource(String errorSource) {
    boolean shouldExclude;

    boolean sourceParamNotSpecified = StringUtils.isBlank(sourceFile);
    if (sourceParamNotSpecified) {
      shouldExclude = true;
    } else {
      // covers equals as well
      shouldExclude = errorSource.endsWith(sourceFile);
    }
    return shouldExclude;
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: "
        + PARAM_ERROR_PATTERN + ": " + errorMessagePattern + " "
        + PARAM_SOURCE + ": " + sourceFile + " "
        + PARAM_LINE + ": " + line;
  }

}
