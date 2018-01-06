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
package com.cognifide.aet.job.common.datafilters.removeregexp;

import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveRegexpDataModifier implements DataFilterJob<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveRegexpDataModifier.class);

  public static final String NAME = "remove-regexp";

  private static final String DATA_REGEXP = "dataRegExp";

  private static final String REGEXP = "regExp";

  private static final String PATTERN_REGEXP = "patternRegExp";

  private String dataRegExp;

  private String patternRanges;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {

    if (StringUtils.isBlank(params.get(REGEXP))) {
      dataRegExp = params.get(DATA_REGEXP);
      patternRanges = params.get(PATTERN_REGEXP);
    } else {
      dataRegExp = params.get(REGEXP);
      patternRanges = params.get(REGEXP);
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
    return NAME + " DataModifier with parameters: " + DATA_REGEXP + ": " + dataRegExp + " "
        + PATTERN_REGEXP + ": " + patternRanges;
  }

  private String modify(String data, String regexp) throws ProcessingException {
    String result = null;
    try {
      result = data.replaceAll(regexp, StringUtils.EMPTY);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
  }
}
