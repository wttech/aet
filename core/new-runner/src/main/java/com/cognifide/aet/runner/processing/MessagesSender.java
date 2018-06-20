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
package com.cognifide.aet.runner.processing;

import com.cognifide.aet.communication.api.messages.BasicMessage;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import java.util.Observable;
import java.util.Observer;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessagesSender implements Observer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessagesSender.class);

  private final Session session;
  private final MessageProducer resultsProducer;

  MessagesSender(Destination jmsReplyTo, JmsConnection jmsConnection) throws JMSException {
    this.session = jmsConnection.getJmsSession();
    resultsProducer = session.createProducer(jmsReplyTo);
  }

  @Override
  public void update(Observable o, Object message) {
    LOGGER.debug("Updated with message: {}", message);
    if (isMessage(message)) {
      sendMessage((BasicMessage) message);
    } else {
      LOGGER.error("Notified with wrong message type: {} by {}", message, o);
    }
  }

  void sendMessage(BasicMessage message) {
    try {
      LOGGER.debug("Sending message: {}", message);
      resultsProducer.send(session.createObjectMessage(message));
    } catch (JMSException e) {
      LOGGER.warn("Failed to send message {}", message, e);
    }
  }

  private boolean isMessage(Object message) {
    return message instanceof BasicMessage;
  }

  void close() {
    JmsUtils.closeQuietly(resultsProducer);
  }

}
