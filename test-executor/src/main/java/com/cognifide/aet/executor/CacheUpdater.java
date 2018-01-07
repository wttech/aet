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
import com.cognifide.aet.rest.LockService;
import com.google.common.cache.Cache;
import java.util.Queue;

class CacheUpdater {

  private final Cache<String, SuiteRunner> runnerCache;

  private final Cache<String, Queue<SuiteStatusResult>> statusCache;

  private final LockService lockService;

  CacheUpdater(Cache<String, SuiteRunner> runnerCache,
      Cache<String, Queue<SuiteStatusResult>> statusCache,
      LockService lockService) {
    this.runnerCache = runnerCache;
    this.statusCache = statusCache;
    this.lockService = lockService;
  }

  /**
   * Accesses the SuiteRunner, SuiteStatusResult and Lock cache entries for given correlation ID and
   * suiteIdentifier to prevent their expiration.
   *
   * @param correlationId correlation ID of the test suite for which the cache entries will be
   * updated
   * @param suiteIdentifier identifier of the test suite for which the cache entries will be
   * updated
   */
  void update(String correlationId, String suiteIdentifier) {
    runnerCache.getIfPresent(correlationId);
    statusCache.getIfPresent(correlationId);
    lockService.setLock(suiteIdentifier, correlationId);
  }
}
