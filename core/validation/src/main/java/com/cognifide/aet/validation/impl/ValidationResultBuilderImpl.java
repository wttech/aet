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
import java.util.ArrayList;
import java.util.List;

public class ValidationResultBuilderImpl implements ValidationResultBuilder {

  private final List<ErrorMessage> errorMessages = new ArrayList<>();

  @Override
  public ValidationResultBuilder addErrorMessage(final String message) {
    addErrorMessage(message, null);
    return this;
  }

  @Override
  public ValidationResultBuilder addErrorMessage(final String message, final Throwable t) {
    errorMessages.add(new ErrorMessageImpl(message, t));
    return this;
  }

  @Override
  public boolean isValid() {
    return errorMessages.isEmpty();
  }

  @Override
  public boolean hasErrors() {
    return !errorMessages.isEmpty();
  }

  @Override
  public List<ErrorMessage> getErrorMessages() {
    return errorMessages;
  }

  @Override
  public String toString() {
    return ValidationUtils.validationResultToString(this);
  }
}
