/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.worker.listeners;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.worker.results.FeedbackQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

@Component
public abstract class AbstractTaskMessageListener implements MessageListener {

  protected static final String LISTENER_NAME = "name";

  protected static final String CONSUMER_QUEUE_NAME = "consumerQueueName";

  protected static final String PRODUCER_QUEUE_NAME = "producerQueueName";

  protected static final String PREFETCH_SIZE_NAME = "pf";

  protected static final String PREFETCH_SIZE_DEFAULT_VALUE = "1";

  protected Session jmsSession;

  protected MessageConsumer consumer;

  protected FeedbackQueue feedbackQueue;

  @Property(name = PREFETCH_SIZE_NAME, label = "Prefetch size", description = "http://activemq.apache.org/what-is-the-prefetch-limit-for.html", value = PREFETCH_SIZE_DEFAULT_VALUE)
  private String prefetchSize;

  protected void doActivate(Map<String, String> properties) {
    setName(properties.get(LISTENER_NAME));
    this.prefetchSize = StringUtils.defaultString(properties.get(PREFETCH_SIZE_NAME),
            PREFETCH_SIZE_DEFAULT_VALUE);
    String consumerQueueName = properties.get(CONSUMER_QUEUE_NAME);
    setConsumerQueueName(consumerQueueName);
    String producerQueueName = properties.get(PRODUCER_QUEUE_NAME);
    setProducerQueueName(producerQueueName);

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

  protected void doDeactivate() {
    if (feedbackQueue != null) {
      feedbackQueue.close();
    }
    JmsUtils.closeQuietly(consumer);
    JmsUtils.closeQuietly(jmsSession);
  }

  protected abstract void setName(String name);

  protected abstract void setConsumerQueueName(String consumerQueueName);

  protected abstract void setProducerQueueName(String producerQueueName);

  protected abstract JmsConnection getJmsConnection();

}
