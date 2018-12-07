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
import com.cognifide.aet.worker.api.CollectorDispatcher;
import com.cognifide.aet.worker.api.ComparatorDispatcher;
import com.cognifide.aet.worker.drivers.WebDriverProvider;
import com.cognifide.aet.worker.exceptions.ConsumerInitException;
import com.cognifide.aet.worker.results.FeedbackQueue;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = WorkersListenersService.class,
    immediate = true)
@Designate(ocd = WorkersListenersFactoryConfig.class)
public class WorkersListenersService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkersListenersService.class);

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private WebDriverProvider webDriverProvider;

  @Reference
  private CollectorDispatcher collectorDispatcher;

  @Reference
  private ComparatorDispatcher comparatorDispatcher;

  private WorkersListenersFactoryConfig config;

  private Session jmsSession;
  private FeedbackQueue collectorFeedbackQueue;
  private FeedbackQueue comparatorFeedbackQueue;
  private Set<MessageConsumer> consumers;

  @Activate
  void activate(WorkersListenersFactoryConfig config) {
    this.config = config;
    try {
      jmsSession = jmsConnection.getJmsSession();
      collectorFeedbackQueue = new FeedbackQueue(jmsSession, config.collectorProducerQueueName());
      comparatorFeedbackQueue = new FeedbackQueue(jmsSession, config.comparatorProducerQueueName());
      consumers = new HashSet<>();
      consumers.addAll(spawnCollectors(config));
      consumers.addAll(spawnComparators(config));
    } catch (JMSException e) {
      LOGGER.error("Failed to activate WorkersListenersService", e);
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  private Set<MessageConsumer> spawnListeners(String consumerQueueName, String prefetchSize,
      int noOfInstances,
      Function<Integer, MessageListener> getListenerInstance) {
    final Set<MessageConsumer> consumersSet = new HashSet<>();
    final String queueName = consumerQueueName + "?consumer.prefetchSize=" + prefetchSize;
    IntStream.of(noOfInstances)
        .forEach(no -> {
          try {
            MessageConsumer consumer = jmsSession.createConsumer(jmsSession.createQueue(queueName));
            MessageListener messageListener = getListenerInstance.apply(no);
            consumer.setMessageListener(messageListener);
            consumersSet.add(consumer);
          } catch (JMSException e) {
            LOGGER.error("Failed to create consumer {} for {}", no, consumerQueueName, e);
            throw new ConsumerInitException(
                String.format("Failed to create consumer %s for %s", no, consumerQueueName), e);
          }
        });
    return consumersSet;
  }

  private Integer getenvOrDefault(String envVarName, int defaultValue) {
    return Optional.ofNullable(System.getenv(envVarName))
        .filter(StringUtils::isNotBlank)
        .map(Integer::parseInt)
        .orElse(defaultValue);
  }

  private Set<MessageConsumer> spawnCollectors(WorkersListenersFactoryConfig config) {
    return spawnListeners(config.collectorConsumerQueueName(), config.collectorPrefetchSize(),
        getenvOrDefault(WorkersListenersFactoryConfig.COLLECTORS_NO_ENV,
            config.collectorInstancesNo()),
        no -> new CollectorMessageListener("Collector" + no, collectorDispatcher,
            collectorFeedbackQueue, webDriverProvider));
  }

  private Set<MessageConsumer> spawnComparators(WorkersListenersFactoryConfig config) {
    return spawnListeners(config.comparatorConsumerQueueName(), config.comparatorPrefetchSize(),
        getenvOrDefault(WorkersListenersFactoryConfig.COMPARATORS_NO_ENV,
            config.comparatorInstancesNo()),
        no -> new ComparatorMessageListener("Comparator" + no, comparatorDispatcher,
            comparatorFeedbackQueue));
  }

  @Deactivate
  void deactivate() {
    if (collectorFeedbackQueue != null) {
      collectorFeedbackQueue.close();
    }
    if (comparatorFeedbackQueue != null) {
      comparatorFeedbackQueue.close();
    }
    consumers.forEach(JmsUtils::closeQuietly);
    JmsUtils.closeQuietly(jmsSession);
  }

}
