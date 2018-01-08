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

/**
 * Represents the status of test suite processing.
 */
public enum ProcessingStatus {

  /**
   * The test suite is being processed by system.
   */
  PROGRESS,

  /**
   * An error occurred during the test suite processing that does not terminate processing.
   */
  ERROR,

  /**
   * An error occurred during the test suite processing that terminates processing.
   */
  FATAL_ERROR,

  /**
   * Suite processing has finished successfully.
   */
  FINISHED,

  /**
   * Suite status could not be established. This usually means that the client has already obtained
   * all status messages available but new messages might be available later.
   */
  UNKNOWN
}
