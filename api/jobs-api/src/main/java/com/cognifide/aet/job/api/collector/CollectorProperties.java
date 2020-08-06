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
package com.cognifide.aet.job.api.collector;

import com.cognifide.aet.job.api.StepProperties;
import org.apache.commons.lang3.StringUtils;

public class CollectorProperties extends StepProperties {

  private static final long serialVersionUID = -7759549743792782981L;

  private final String path;

  private final String domain;

  public CollectorProperties(String domain, String path, String company, String project, String patternId) {
    super(company, project, patternId);
    this.domain = domain;
    this.path = path;
  }

  public String getUrl() {
    return StringUtils.trimToEmpty(domain) + path;
  }

  public String getPath() {
    return path;
  }

  public String getDomain() {
    return domain;
  }
}
