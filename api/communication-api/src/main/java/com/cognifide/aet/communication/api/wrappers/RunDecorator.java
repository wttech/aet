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

public abstract class RunDecorator<T> implements Run<T> {

  private static final long serialVersionUID = 6866141366419923230L;

  protected Run<T> decoratedRun;

  public Run<T> getRun() {
    return decoratedRun;
  }

  public RunDecorator(Run<T> decoratedRun) {
    this.decoratedRun = decoratedRun;
  }

  @Override
  public RunType getType() {
    return decoratedRun.getType();
  }
}
