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

import com.cognifide.aet.communication.api.metadata.RunType;
import com.cognifide.aet.communication.api.metadata.Test;

public class TestRunWrapper implements Run {

  private Test test;

  public TestRunWrapper(Test test) {
    this.test = test;
    test.setReran();
  }

  @Override
  public void setObjectToRun(Object object) {
    this.test = (Test) object;
  }

  @Override
  public RunType getType() {
    return RunType.TEST;
  }

  @Override
  public Test getObjectToRun() {
    return test;
  }

  public String getTestName() {
    return test.getName();
  }
}
