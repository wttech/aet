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

import com.cognifide.aet.communication.api.ProcessingError;
import com.google.common.base.MoreObjects;

public class ProcessingErrorMessage implements BasicMessage {

  private static final long serialVersionUID = -6491695043921615086L;

  private final ProcessingError processingError;

  private final String correlationId;

  public ProcessingErrorMessage(ProcessingError processingError, String correlationId) {
    this.processingError = processingError;
    this.correlationId = correlationId;
  }

  public ProcessingError getProcessingError() {
    return processingError;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.ERROR;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("correlationId", correlationId)
        .add("processingError", processingError).toString();

  }
}
