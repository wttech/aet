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
import java.util.Objects;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class WorkerMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkerMessageListener.class);

  private final Session jmsSession;
  private final MessageConsumer consumer;

  final String name;
  final FeedbackQueue feedbackQueue;

  WorkerMessageListener(String name, JmsConnection jmsConnection,
      String consumerQueueName,
      String producerQueueName) {
    LOGGER.info("Starting {}", name);
    this.name = name;
    try {
      jmsSession = jmsConnection.getJmsSession();
      feedbackQueue = new FeedbackQueue(jmsSession, producerQueueName);
      consumer = jmsSession.createConsumer(jmsSession.createQueue(consumerQueueName));
      consumer.setMessageListener(this);
    } catch (JMSException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  void close() {
    if (feedbackQueue != null) {
      feedbackQueue.close();
    }
    JmsUtils.closeQuietly(consumer);
    JmsUtils.closeQuietly(jmsSession);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkerMessageListener that = (WorkerMessageListener) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" +
        "name='" + name + '\'' +
        '}';
  }
}
