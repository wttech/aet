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
package com.cognifide.aet.executor;

import com.cognifide.aet.communication.api.execution.SuiteStatusResult;
import com.google.common.cache.Cache;
import java.util.Queue;

class SuiteStatusHandler {

  private Cache<String, Queue<SuiteStatusResult>> statusCache;

  SuiteStatusHandler(Cache<String, Queue<SuiteStatusResult>> statusCache) {
    this.statusCache = statusCache;
  }

  /**
   * Adds given suite processing status to the statuses queue associated with the specified
   * correlation ID of the test suite run.
   *
   * @param correlationId correlationId of the test suite run
   * @param status suite processing status
   */
  void handle(String correlationId, SuiteStatusResult status) {
    Queue<SuiteStatusResult> statusQueue = statusCache.getIfPresent(correlationId);
    if (statusQueue != null) {
      statusQueue.add(status);
    }
  }
}
