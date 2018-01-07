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
package com.cognifide.aet.validation;

import java.util.List;

/**
 * Responsible for building validation outcome
 *
 * @see Validator#validate(ValidationResultBuilder)
 */
public interface ValidationResultBuilder {

  /**
   * Adds error message to validation outcome
   *
   * @param message error message
   * @return validationBuilder (enables chaining)
   */
  ValidationResultBuilder addErrorMessage(String message);

  /**
   * Adds errors message to validation outcome
   *
   * @param message error message
   * @param t Throwable
   * @return validationBuilder (enables chaining)
   */
  ValidationResultBuilder addErrorMessage(String message, Throwable t);

  /**
   * Indicates if validation has been successful
   *
   * @return boolean value with validation outcome
   */
  boolean isValid();

  /**
   * Checks if builder registered any errors during validation process
   */
  boolean hasErrors();

  /**
   * Gets List of errors
   *
   * @return list of errors
   */
  List<ErrorMessage> getErrorMessages();

}
