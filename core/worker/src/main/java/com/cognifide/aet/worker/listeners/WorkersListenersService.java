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
import com.cognifide.aet.communication.api.queues.QueuesConstant;
import com.cognifide.aet.worker.api.CollectorDispatcher;
import com.cognifide.aet.worker.api.ComparatorDispatcher;
import com.cognifide.aet.worker.drivers.WebDriverProvider;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
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
@Designate(ocd = WorkersListenersServiceConfig.class)
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

  private Set<WorkerMessageListener> consumers;

  @Activate
  void activate(WorkersListenersServiceConfig config) {
    LOGGER.info("Starting Workers Listeners Service with {}", config);
    consumers = new HashSet<>();
    consumers.addAll(spawnCollectors(config));
    consumers.addAll(spawnComparators(config));
  }

  private Set<WorkerMessageListener> spawnListeners(int noOfInstances,
      Function<Integer, WorkerMessageListener> getListenerInstance) {
    final Set<WorkerMessageListener> consumersSet = new HashSet<>();
    IntStream.rangeClosed(1, noOfInstances)
        .forEach(no -> {
          WorkerMessageListener listener = getListenerInstance.apply(no);
          consumersSet.add(listener);
        });
    return consumersSet;
  }

  private Integer getenvOrDefault(String envVarName, int defaultValue) {
    return Optional.ofNullable(System.getenv(envVarName))
        .filter(StringUtils::isNotBlank)
        .map(Integer::parseInt)
        .orElse(defaultValue);
  }

  private Set<WorkerMessageListener> spawnCollectors(WorkersListenersServiceConfig config) {
    final String queueName =
        QueuesConstant.COLLECTOR.getJobsQueueName() + "?consumer.prefetchSize=" + config
            .collectorPrefetchSize();
    return spawnListeners(getenvOrDefault(WorkersListenersServiceConfig.COLLECTORS_NO_ENV,
        config.collectorInstancesNo()),
        no -> new CollectorMessageListener("Collector-" + no, collectorDispatcher,
            webDriverProvider, jmsConnection, queueName,
            QueuesConstant.COLLECTOR.getResultsQueueName()));
  }

  private Set<WorkerMessageListener> spawnComparators(WorkersListenersServiceConfig config) {
    final String queueName =
        QueuesConstant.COMPARATOR.getJobsQueueName() + "?consumer.prefetchSize=" + config
            .comparatorPrefetchSize();
    return spawnListeners(getenvOrDefault(WorkersListenersServiceConfig.COMPARATORS_NO_ENV,
        config.comparatorInstancesNo()),
        no -> new ComparatorMessageListener("Comparator-" + no, comparatorDispatcher,
            jmsConnection, queueName, QueuesConstant.COMPARATOR.getResultsQueueName()));
  }

  @Deactivate
  void deactivate() {
    LOGGER.info("Closing Workers Listeners with: {}", consumers);
    consumers.forEach(WorkerMessageListener::close);
  }

}
