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
package com.cognifide.aet.validation.impl;

import com.cognifide.aet.validation.ErrorMessage;
import com.cognifide.aet.validation.ValidationResultBuilder;
import org.apache.commons.lang3.StringUtils;

public final class ValidationUtils {

  private ValidationUtils() {
    // empty utils constructor
  }

  public static String validationResultToString(ValidationResultBuilder validationResultBuilder) {
    StringBuilder stringBuilder = new StringBuilder();
    if (validationResultBuilder != null && validationResultBuilder.hasErrors()) {
      stringBuilder.append("Validation failed. ");
      stringBuilder.append(validationResultBuilder.getErrorMessages().size());
      stringBuilder.append(" errors were found:" + StringUtils.LF);

      int counter = 1;
      for (ErrorMessage errorMessage : validationResultBuilder.getErrorMessages()) {
        stringBuilder.append("Error " + counter + StringUtils.LF);
        stringBuilder.append("\tMessage: ");
        stringBuilder.append(errorMessage.getMessage());
        stringBuilder.append(StringUtils.LF);
        if (errorMessage.getThrowable() != null) {
          stringBuilder.append("\tCaused by: ");
          stringBuilder.append(errorMessage.getThrowable());
          stringBuilder.append(StringUtils.LF);
        }
        counter++;
      }
    }
    return stringBuilder.toString();
  }

}
