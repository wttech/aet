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
package com.cognifide.aet.communication.api.wrappers;

public class MetadataRunDecorator extends RunDecorator {

  private String correlationId;

  private String company;

  private String project;

  private String name;

  public MetadataRunDecorator(Run decoratedRun, String correlationId, String company,
      String project) {
    super(decoratedRun);
    this.correlationId = correlationId;
    this.company = company;
    this.project = project;
  }

  @Override
  public String getCorrelationId() {
    return correlationId;
  }

  @Override
  public String getCompany() {
    return company;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSuiteIdentifier() {
    return company + "-" + project + "-" + name;
  }

  @Override
  public Object getObjectToRun() {
    return decoratedRun.getObjectToRun();
  }

  @Override
  public String getProject() {
    return project;
  }

}
