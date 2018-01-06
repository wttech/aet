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
package com.cognifide.aet.communication.api.execution;

import org.apache.commons.lang3.StringUtils;

/**
 * The status of test suite processing.
 */
public class SuiteStatusResult {

  private ProcessingStatus status;

  private String message;

  public SuiteStatusResult(ProcessingStatus status) {
    this(status, StringUtils.EMPTY);
  }

  public SuiteStatusResult(ProcessingStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  /**
   * @return status of suite processing. See {@link ProcessingStatus} for available values
   */
  public ProcessingStatus getStatus() {
    return status;
  }

  /**
   * @return optional status message
   */
  public String getMessage() {
    return message;
  }
}
