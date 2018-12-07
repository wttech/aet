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

import javax.validation.Valid;

public class ComparatorStepResult extends StepResult {

  private static final long serialVersionUID = 4112700984952534120L;

  private final Status status;

  private final boolean rebaseable;

  private final boolean acceptable;

  @Valid
  private final Pattern pattern;

  public ComparatorStepResult(String artifactId, Status status) {
    this(artifactId, null, status, false);
  }

  public ComparatorStepResult(String artifactId, Pattern pattern, Status status,
      Boolean rebaseable) {
    super(artifactId);
    this.status = status;
    this.rebaseable = rebaseable;
    this.acceptable = rebaseable &&
        (Status.FAILED.equals(status) || Status.CONDITIONALLY_PASSED.equals(status));
    this.pattern = pattern;
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

  public boolean isAcceptable() {
    return acceptable;
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
