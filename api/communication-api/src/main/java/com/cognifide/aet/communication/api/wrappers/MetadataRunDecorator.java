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

import com.cognifide.aet.communication.api.metadata.Suite;

public class MetadataRunDecorator<T> extends RunDecorator<T> {

  private static final long serialVersionUID = 2300347507334858549L;

  private Suite suite;

  public MetadataRunDecorator(Run decoratedRun, Suite suite) {
    super(decoratedRun);
    this.suite = suite;
  }

  @Override
  public void setObjectToRun(T object) {
    decoratedRun.setObjectToRun(object);
  }

  @Override
  public void setRealSuite(Suite suite) {
    this.suite = suite;
  }

  @Override
  public String getCorrelationId() {
    return suite.getCorrelationId();
  }

  @Override
  public String getCompany() {
    return suite.getCompany();
  }

  @Override
  public String getName() {
    return suite.getName();
  }

  @Override
  public String getSuiteIdentifier() {
    return suite.getSuiteIdentifier();
  }

  @Override
  public T getObjectToRun() {
    return decoratedRun.getObjectToRun();
  }

  @Override
  public String getProject() {
    return suite.getProject();
  }

  @Override
  public Suite getRealSuite() {
    return suite;
  }


  public String getTestName() {
    return decoratedRun.getTestName();
  }

}
