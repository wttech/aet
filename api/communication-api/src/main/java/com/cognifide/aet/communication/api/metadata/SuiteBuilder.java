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
package com.cognifide.aet.communication.api.metadata;

import java.util.Set;

public class SuiteBuilder {

  private String correlationId;

  private String company;

  private String project;

  private String name;

  private Set<String> patternCorrelationId;

  private String projectHashCode;

  public SuiteBuilder setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  public SuiteBuilder setProjectHashCode(String projectHashCode) {
    this.projectHashCode = projectHashCode;
    return this;
  }

  public SuiteBuilder setCompany(String company) {
    this.company = company;
    return this;
  }

  public SuiteBuilder setProject(String project) {
    this.project = project;
    return this;
  }

  public SuiteBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public SuiteBuilder setPatternCorrelationId(Set<String> patternCorrelationId) {
    this.patternCorrelationId = patternCorrelationId;
    return this;
  }

  public Suite createSuite() {
    Suite suite;
    if (projectHashCode == null || projectHashCode.isEmpty()) {
      suite = new Suite(correlationId, company, project, name, patternCorrelationId);
    } else {
      suite = new Suite(correlationId, company, project, name, patternCorrelationId,
          projectHashCode);
    }
    return suite;
  }
}