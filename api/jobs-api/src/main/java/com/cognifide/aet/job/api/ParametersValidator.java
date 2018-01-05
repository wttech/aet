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
package com.cognifide.aet.job.api;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import org.apache.commons.lang3.StringUtils;

/**
 * Checks parameters and throws ParametersException if they are not valid.
 */
public final class ParametersValidator {

  private ParametersValidator() {
  }

  public static void checkRange(int value, int min, int max, String errorMessage)
      throws ParametersException {
    if (value < min || value > max) {
      throw new ParametersException(errorMessage);
    }
  }

  public static void checkNotBlank(String value, String errorMessage) throws ParametersException {
    if (StringUtils.isBlank(value)) {
      throw new ParametersException(errorMessage);
    }
  }

  public static void checkAtLeastOneNotBlank(String errorMessage, String... values)
      throws ParametersException {
    for (String value : values) {
      if (StringUtils.isNotBlank(value)) {
        return;
      }
    }
    throw new ParametersException(errorMessage);
  }

  public static void checkParameter(boolean expression, String errorMessage)
      throws ParametersException {
    if (!expression) {
      throw new ParametersException(errorMessage);
    }
  }

}
