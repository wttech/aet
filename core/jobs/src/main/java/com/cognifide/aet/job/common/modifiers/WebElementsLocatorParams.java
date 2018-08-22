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
package com.cognifide.aet.job.common.modifiers;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

abstract public class WebElementsLocatorParams {

  private static final String PARAM_TIMEOUT = "timeout";

  protected static final String XPATH_PARAM = "xpath";

  protected static final String CSS_PARAM = "css";

  protected static final String EXCLUDE_ELEMENT_PARAM = "exclude-elements";

  private static final long TIMEOUT_SECONDS_MAX_VALUE = 15L;

  private static final long TIMEOUT_SECONDS_DEFAULT_VALUE = 1L;

  private By locator;

  /**
   * Timeout for waiting for element (in seconds).
   */
  private long timeoutInSeconds = TIMEOUT_SECONDS_DEFAULT_VALUE;

  protected long getTimeoutInSeconds() {
    return timeoutInSeconds;
  }

  protected void setElementParams(Map<String, String> params) throws ParametersException {
    String xpath = params.get(XPATH_PARAM);
    String css = params.get(CSS_PARAM);
    if (StringUtils.isBlank(xpath) == StringUtils.isBlank(css)) {
      throw new ParametersException(
          "Either 'xpath' or 'css' parameter must be provided for element modifier.");
    }
    locator = StringUtils.isNotBlank(xpath) ? By.xpath(xpath) : By.cssSelector(css);
    initializeTimeOutParam(params.get(PARAM_TIMEOUT));
  }

  protected By getLocator() {
    return locator;
  }

  protected boolean isSelectorPresent() {
    return locator != null;
  }

  private void initializeTimeOutParam(String timeoutString) throws ParametersException {
    if (StringUtils.isNotBlank(timeoutString)) {
      if (!StringUtils.isNumeric(timeoutString)) {
        throw new ParametersException(
            "Parameter 'timeout' on Click Modifier isn't a numeric value.");
      }
      timeoutInSeconds = TimeUnit.SECONDS
          .convert(Long.valueOf(timeoutString), TimeUnit.MILLISECONDS);
      if (timeoutInSeconds < 0) {
        throw new ParametersException("'timeout' parameter value should be greater or equal zero.");
      } else if (TIMEOUT_SECONDS_MAX_VALUE < timeoutInSeconds) {
        throw new ParametersException("'timeout' parameter value can't be greater than "
            + Long.toString(TIMEOUT_SECONDS_MAX_VALUE) + " seconds.");
      }
    }
  }
}
