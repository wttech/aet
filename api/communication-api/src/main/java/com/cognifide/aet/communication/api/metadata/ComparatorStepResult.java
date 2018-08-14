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

public class ComparatorStepResult extends StepResult {

  private static final long serialVersionUID = 4112741834952534050L;

  private final Status status;

  private final boolean rebaseable;

  public ComparatorStepResult(String artifactId, Status status) {
    this(artifactId, status, false);
  }

  public ComparatorStepResult(String artifactId, Status status, Boolean rebaseable) {
    super(artifactId);
    this.status = status;
    this.rebaseable = rebaseable;
  }

  @Override
  public String getStatusName() {
    return status.name();
  }

  public Status getStatus() {
    return status;
  }

  public boolean isRebaseable() {
    return rebaseable;
  }

  public enum Status {
    PASSED,
    FAILED,
    WARNING,
    REBASED,
    PROCESSING_ERROR,
    CONDITIONALLY_PASSED,
  }
}
