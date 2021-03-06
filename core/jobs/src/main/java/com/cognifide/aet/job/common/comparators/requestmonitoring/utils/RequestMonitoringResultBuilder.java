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

public class RequestMonitoringResultBuilder {

  private String url;

  private double size; //kB

  public RequestMonitoringResultBuilder() {
  }

  public RequestMonitoringResultBuilder setUrl(String url) {
    this.url = url;
    return this;
  }

  public RequestMonitoringResultBuilder setSize(long sizeInBytes) {
    this.size = sizeInBytes / 1024d; // kB
    return this;
  }

  public RequestMonitoringResult build() {
    return new RequestMonitoringResult(url, size);
  }

}
