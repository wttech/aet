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
package com.cognifide.aet.worker.listeners;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.worker.results.FeedbackQueue;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

public abstract class AbstractTaskMessageListener implements MessageListener {

  private Session jmsSession;

  private MessageConsumer consumer;

  FeedbackQueue feedbackQueue;

  void doActivate(String consumerQueueName, String producerQueueName, String prefetchSize) {

    String queueName = consumerQueueName + "?consumer.prefetchSize=" + prefetchSize;
    try {
      jmsSession = getJmsConnection().getJmsSession();
      consumer = jmsSession.createConsumer(jmsSession.createQueue(queueName));
      consumer.setMessageListener(this);
      feedbackQueue = new FeedbackQueue(jmsSession, producerQueueName);
    } catch (JMSException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  void doDeactivate() {
    if (feedbackQueue != null) {
      feedbackQueue.close();
    }
    JmsUtils.closeQuietly(consumer);
    JmsUtils.closeQuietly(jmsSession);
  }

  protected abstract JmsConnection getJmsConnection();

}
