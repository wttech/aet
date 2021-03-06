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

import java.util.Objects;

public class RequestMonitoringResult implements Comparable<RequestMonitoringResult> {

  private final String url;

  private final double size; // kB

  public RequestMonitoringResult(String url, double size) {
    this.url = url;
    this.size = size / 1024d;
  }

  public String getUrl() {
    return url;
  }

  public double getSize() {
    return size;
  }

  @Override
  public int compareTo(RequestMonitoringResult o) {
    return Double.compare(this.size, o.getSize());
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj != null && obj.getClass() == this.getClass()) {
      RequestMonitoringResult other = (RequestMonitoringResult) obj;
      result = Objects.equals(url, other.url)
          && Objects.equals(size, other.size);
    }
    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, size);
  }
}
