/**
 * Automated Exploratory Tests
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
package com.cognifide.aet.executor;

import com.google.common.cache.Cache;

import java.util.Queue;

public class SuiteStatusHandler {

  private Cache<String, Queue<SuiteStatusResult>> statusCache;

  public SuiteStatusHandler(Cache<String, Queue<SuiteStatusResult>> statusCache) {
    this.statusCache = statusCache;
  }

  public void handle(String correlationId, SuiteStatusResult status) {
    Queue<SuiteStatusResult> statusQueue = statusCache.getIfPresent(correlationId);
    if (statusQueue != null) {
      statusQueue.add(status);
    }
  }
}
