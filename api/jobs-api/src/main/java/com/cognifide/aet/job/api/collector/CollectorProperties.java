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
package com.cognifide.aet.job.api.collector;

import com.cognifide.aet.job.api.StepProperties;

public class CollectorProperties extends StepProperties {

  private static final long serialVersionUID = -188750694404101821L;

  private final String url;

  private final String urlName;

  public CollectorProperties(String url, String urlName, String company, String project,
      String patternId) {
    super(company, project, patternId);
    this.url = url;
    this.urlName = urlName;
  }

  public String getUrl() {
    return url;
  }

  public String getUrlName() {
    return urlName;
  }
}
