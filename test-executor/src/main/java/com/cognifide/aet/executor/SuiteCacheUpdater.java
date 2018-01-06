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

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.executor.common.RunnerTerminator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class runs a thread which updates the caches for given suite run.
 */
class SuiteCacheUpdater implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteCacheUpdater.class);

  private static final int CACHE_UPDATE_INTERVAL_MILLIS = 5000;

  private final CacheUpdater cacheUpdater;

  private final RunnerTerminator runnerTerminator;

  private final Suite suite;

  SuiteCacheUpdater(CacheUpdater cacheUpdater, RunnerTerminator runnerTerminator, Suite suite) {
    this.cacheUpdater = cacheUpdater;
    this.runnerTerminator = runnerTerminator;
    this.suite = suite;
  }

  /**
   * Updates caches for suite.
   */
  @Override
  public void run() {
    while (runnerTerminator.isActive()) {
      try {
        Thread.sleep(CACHE_UPDATE_INTERVAL_MILLIS);
        cacheUpdater.update(suite.getCorrelationId(), suite.getSuiteIdentifier());
      } catch (InterruptedException e) {
        LOGGER.error("Failed to update cache for suite: '{}'.", suite.getCorrelationId(), e);
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Starts a thread which updates caches.
   */
  void start() {
    Thread cacheUpdateThread = new Thread(this);
    cacheUpdateThread.start();
  }
}
