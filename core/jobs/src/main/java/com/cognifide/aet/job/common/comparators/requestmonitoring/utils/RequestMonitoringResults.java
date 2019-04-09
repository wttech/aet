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

import java.util.SortedSet;
import java.util.TreeSet;

public class RequestMonitoringResults {

  private final SortedSet<RequestMonitoringResult> results;

  private double totalSize;

  public RequestMonitoringResults() {
    this.results = new TreeSet<>();
    this.totalSize = 0d;
  }

  public final void addItem(RequestMonitoringResult item) {
    results.add(item);
    totalSize += item.getSize();
  }

  public SortedSet<RequestMonitoringResult> getResults() {
    return results;
  }

  public double getTotalSize() {
    return totalSize;
  }
}
