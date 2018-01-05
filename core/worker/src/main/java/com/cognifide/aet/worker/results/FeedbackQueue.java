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
package com.cognifide.aet.worker.results;

import com.cognifide.aet.queues.JmsUtils;
import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lukasz.wieczorek
 */
public class FeedbackQueue {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackQueue.class);

  private static final int DEFAULT_TIME_TO_LIVE = 300000;

  private Session jmsSession;

  private MessageProducer sender;

  public FeedbackQueue(Session jmsSession, String queueName) throws JMSException {
    this(jmsSession, queueName, DEFAULT_TIME_TO_LIVE);
  }

  public FeedbackQueue(Session jmsSession, String queueName, int timeToLive) throws JMSException {
    this.jmsSession = jmsSession;
    Queue queue = jmsSession.createQueue(queueName);
    sender = jmsSession.createProducer(queue);
    sender.setTimeToLive(timeToLive);
  }

  public String sendObjectMessageWithCorrelationID(Serializable object, String correlationID) {
    String result;
    try {
      ObjectMessage objectMessage = jmsSession.createObjectMessage(object);
      objectMessage.setJMSCorrelationID(correlationID);
      sender.send(objectMessage);
      result = objectMessage.getJMSMessageID();
    } catch (JMSException e) {
      LOGGER.error(e.getMessage(), e);
      result = null;
    }
    return result;
  }

  public void close() {
    JmsUtils.closeQuietly(sender);
  }

}
