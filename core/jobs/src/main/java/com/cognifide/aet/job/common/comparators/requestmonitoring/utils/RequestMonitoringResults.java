/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.comparators.requestmonitoring.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

public class RequestMonitoringResults {

  private final Collection<RequestMonitoringResult> results;

  public RequestMonitoringResults() {
    results = new TreeSet<>(Collections.reverseOrder());
  }

  public final void addItem(String url, long bodySize) {
    RequestMonitoringResult item = new RequestMonitoringResult(url, bodySize);
    results.add(item);
  }

  public Collection<RequestMonitoringResult> getResults() {
    return results;
  }

  public double getTotalSize() {
    return results.stream().map(RequestMonitoringResult::getSize).reduce(0.0, Double::sum);
  }
}
