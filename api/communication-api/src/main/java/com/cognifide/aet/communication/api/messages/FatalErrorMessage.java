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

public class FatalErrorMessage implements BasicMessage {

  private static final long serialVersionUID = -5008507485742360588L;

  private final String message;

  private final String correlationId;

  public FatalErrorMessage(String message, String correlationId) {
    this.message = message;
    this.correlationId = correlationId;
  }

  public String getMessage() {
    return message;
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
    return MoreObjects.toStringHelper(this)
        .add("message", message)
        .add("correlationId", correlationId)
        .toString();
  }
}
