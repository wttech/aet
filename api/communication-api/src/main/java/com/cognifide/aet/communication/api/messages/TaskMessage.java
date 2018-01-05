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

import java.io.Serializable;

/**
 * Basic task message used to send extra data via JMS.
 */
public class TaskMessage<T extends Serializable> implements BasicMessage {

  private static final long serialVersionUID = -1426781824477755876L;

  private final MessageType messageType;

  private final T data;

  /**
   * @param type - message type.
   * @param data - additional data that will be sent.
   */
  public TaskMessage(MessageType type, T data) {
    this.messageType = type;
    this.data = data;
  }

  /**
   * @return sent data object.
   */
  public T getData() {
    return data;
  }

  @Override
  public MessageType getMessageType() {
    return messageType;
  }
}
