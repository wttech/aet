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
package com.cognifide.aet.job.api;


import com.cognifide.aet.vs.DBKey;
import com.google.common.base.MoreObjects;

/**
 * POJO used to pass properties to Collectors/Comparators on worker.
 */
public abstract class StepProperties implements DBKey {

  private static final long serialVersionUID = 4722844433595287647L;

  private final String company;

  private final String project;

  private final String patternId;

  public StepProperties(String company, String project, String patternId) {
    this.company = company;
    this.project = project;
    this.patternId = patternId;
  }

  @Override
  public String getCompany() {
    return company;
  }

  @Override
  public String getProject() {
    return project;
  }

  public String getPatternId() {
    return patternId;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("company", company)
        .add("project", project)
        .add("patternId", patternId)
        .toString();
  }
}
