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
package com.cognifide.aet.cleaner.context;

import java.io.Serializable;

public class CleanerContext implements Serializable {

  public static final String KEY_NAME = "cleanerContext";

  private static final long serialVersionUID = -8406444471102222048L;

  private final Long removeOlderThan;

  private final Long keepNVersions;

  private final String companyFilter;

  private final String projectFilter;

  private final Boolean dryRun;

  public CleanerContext(Long removeOlderThan, Long keepNVersions, String companyFilter,
      String projectFilter, Boolean dryRun) {
    this.removeOlderThan = removeOlderThan;
    this.keepNVersions = keepNVersions;
    this.companyFilter = companyFilter;
    this.projectFilter = projectFilter;
    this.dryRun = dryRun;
  }

  public Long getRemoveOlderThan() {
    return removeOlderThan;
  }

  public Long getKeepNVersions() {
    return keepNVersions;
  }

  public String getCompanyFilter() {
    return companyFilter;
  }

  public String getProjectFilter() {
    return projectFilter;
  }

  public Boolean isDryRun() {
    return dryRun;
  }
}
