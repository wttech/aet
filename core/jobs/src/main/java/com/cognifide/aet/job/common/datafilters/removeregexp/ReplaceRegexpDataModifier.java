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
package com.cognifide.aet.job.common.datafilters.removeregexp;

import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.ParamsHelper;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceRegexpDataModifier implements DataFilterJob<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReplaceRegexpDataModifier.class);

  public static final String NAME = "replace-regexp";

  private static final String DATA_REGEXP = "dataRegExp";

  private static final String REGEXP = "regExp";

  private static final String PATTERN_REGEXP = "patternRegExp";

  private static final String NEW_VALUE = "value";

  private Pattern dataRegExp;

  private Pattern patternRanges;

  private String replacement;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {

    replacement = ParamsHelper.getParamAsString(NEW_VALUE, params);
    replacement = replacement != null ? replacement : StringUtils.EMPTY;

    if (StringUtils.isBlank(params.get(REGEXP))) {
      dataRegExp = ParamsHelper.getAsPattern(DATA_REGEXP, params);
      patternRanges = ParamsHelper.getAsPattern(PATTERN_REGEXP, params);
    } else {
      dataRegExp = ParamsHelper.getAsPattern(REGEXP, params);
      patternRanges = ParamsHelper.getAsPattern(REGEXP, params);
    }

  }

  @Override
  public String modifyData(String data) throws ProcessingException {
    return modify(data, dataRegExp);
  }

  @Override
  public String modifyPattern(String data) throws ProcessingException {
    return modify(data, patternRanges);
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: " + DATA_REGEXP + ": " + dataRegExp + " " + PATTERN_REGEXP + ": " + patternRanges;
  }

  private String modify(String data, Pattern pattern) throws ProcessingException {
    String result = null;
    try {
      result = pattern.matcher(data).replaceAll(replacement);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
  }
}
