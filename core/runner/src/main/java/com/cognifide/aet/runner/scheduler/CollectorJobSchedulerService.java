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

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.MessagesManager;
import com.cognifide.aet.runner.RunnerConfiguration;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.jms.JMSException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = CollectorJobSchedulerService.class)
public class CollectorJobSchedulerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectorJobSchedulerService.class);

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private MessagesManager messagesManager;

  @Reference
  private RunnerConfiguration runnerConfiguration;

  private CollectorJobScheduler collectorJobScheduler;

  private Future<?> collectorJobSchedulerFeature;

  @Activate
  public void activate() throws JMSException {
    LOGGER.debug("Activating CollectorJobSchedulerService");
    collectorJobScheduler = new CollectorJobScheduler(jmsConnection,
        runnerConfiguration.getMaxMessagesInCollectorQueue(), messagesManager);
    collectorJobSchedulerFeature = Executors.newSingleThreadExecutor()
        .submit(collectorJobScheduler);
  }

  @Deactivate
  public void deactivate() {
    LOGGER.debug("Deactivating CollectorJobSchedulerService");
    if (collectorJobScheduler != null) {
      collectorJobScheduler.quit();
      collectorJobSchedulerFeature.cancel(true);
    }
  }

  public void cancel(String correlationID) {
    collectorJobScheduler.cleanup(correlationID);
  }

  public void add(Queue<MessageWithDestination> messagesQueue, String correlationID) {
    collectorJobScheduler.add(messagesQueue, correlationID);
  }

  public void messageReceived(String requestJMSMessageID, String correlationID) {
    collectorJobScheduler.messageReceived(requestJMSMessageID, correlationID);
  }
}
