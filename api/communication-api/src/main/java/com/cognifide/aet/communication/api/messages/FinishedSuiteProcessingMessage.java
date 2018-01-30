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
package com.cognifide.aet.communication.api.messages;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BasicMessage that enables sending Report data.
 */
public class FinishedSuiteProcessingMessage implements BasicMessage {

  private static final long serialVersionUID = 5927443708704361660L;

  private final Status status;

  private final String correlationId;

  private List<String> errors = new ArrayList<>();

  public FinishedSuiteProcessingMessage(Status status, String correlationId) {
    this.status = status;
    this.correlationId = correlationId;
  }

  public Status getStatus() {
    return status;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public List<String> getErrors() {
    return ImmutableList.copyOf(errors);
  }

  public void addError(String error) {
    errors.add(error);
  }

  public void addErrors(List<String> errors) {
    this.errors.addAll(errors);
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.PROCESSING_FINISHED;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("correlationId", correlationId)
        .add("status", status)
        .add("errors", errors)
        .toString();
  }

  /**
   * Report generation status.
   */
  public enum Status {
    OK, FAILED
  }

}
