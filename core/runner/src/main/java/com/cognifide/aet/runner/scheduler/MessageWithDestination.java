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
package com.cognifide.aet.runner.scheduler;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

public class MessageWithDestination {

  private final Destination destination;

  private final Message message;

  private final int messagesToReceived;

  public MessageWithDestination(Destination destination, Message message, int messagesToReceived) {
    this.destination = destination;
    this.message = message;
    this.messagesToReceived = messagesToReceived;
  }

  Destination getDestination() {
    return destination;
  }

  public Message getMessage() {
    return message;
  }

  ReceivedMessagesInfo getMessagesToReceived() throws JMSException {
    return new ReceivedMessagesInfo(messagesToReceived, message.getJMSCorrelationID());
  }
}
