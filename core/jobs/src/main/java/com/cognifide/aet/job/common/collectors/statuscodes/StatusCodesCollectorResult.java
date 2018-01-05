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
package com.cognifide.aet.job.common.collectors.statuscodes;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;

public class StatusCodesCollectorResult implements Serializable {

  private static final long serialVersionUID = 1660377757372764396L;

  private final List<StatusCode> statusCodes = Lists.newArrayList();

  private final String url;

  public StatusCodesCollectorResult(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void addStatusCode(String url, int code) {
    this.statusCodes.add(new StatusCode(code, url));
  }

  public List<StatusCode> getStatusCodes() {
    return statusCodes;
  }
}
