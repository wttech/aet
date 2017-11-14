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
package com.cognifide.aet.runner.distribution;

import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;

import javax.jms.Destination;

public interface TestSuiteTaskFactory {

  /**
   * Create test suite task. All results will be sent to the given destination.
   * @param indexedSuite - test suite with index.
   * @param resultsDestination - processing results destination.
   * @param maintenanceMessage - flag that says if this message is sent in maintenance mode
   */
  TestSuiteTask create(SuiteIndexWrapper indexedSuite, Destination resultsDestination, boolean maintenanceMessage);

}
