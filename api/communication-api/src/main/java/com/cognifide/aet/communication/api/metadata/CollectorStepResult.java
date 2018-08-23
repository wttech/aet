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

public class CollectorStepResult extends StepResult {

  private static final long serialVersionUID = 8349142369865375088L;

  private final Status status;

  private CollectorStepResult(String artifactId, Payload payload, Status status) {
    super(artifactId, payload);
    this.status = status;
  }

  private CollectorStepResult(String artifactId, Status status) {
    this(artifactId, null, status);
  }

  public static CollectorStepResult newPageOpen() {
    return new CollectorStepResult(null, Status.PAGE_OPENED);
  }

  public static CollectorStepResult newCollectedResult(String artifactId, Payload payload) {
    return new CollectorStepResult(artifactId, payload, Status.COLLECTED);
  }

  public static CollectorStepResult newCollectedResult(String artifactId) {
    return new CollectorStepResult(artifactId, Status.COLLECTED);
  }

  public static CollectorStepResult newResultThatDuplicatesPattern(String patternId) {
    return new CollectorStepResult(patternId, Status.DUPLICATES_PATTERN);
  }

  public static CollectorStepResult newModifierResult() {
    return new CollectorStepResult(null, Status.MODIFIED);
  }

  public static CollectorStepResult newProcessingErrorResult(String error) {
    final CollectorStepResult result = new CollectorStepResult(null, Status.PROCESSING_ERROR);
    result.addError(error);
    return result;
  }

  @Override
  public String getStatusName() {
    return status.name();
  }

  public Status getStatus() {
    return status;
  }

  public enum Status {
    PAGE_OPENED(false),
    COLLECTED(true),
    DUPLICATES_PATTERN(true),
    MODIFIED(false),
    PROCESSING_ERROR(false);

    private final boolean artifacts;

    Status(boolean artifacts) {
      this.artifacts = artifacts;
    }

    public boolean hasArtifacts() {
      return artifacts;
    }
  }
}
