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
package com.cognifide.aet.runner;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.configs.MessagingConfiguration;
import com.cognifide.aet.runner.configs.RunnerConfiguration;
import com.cognifide.aet.runner.processing.SuiteDataService;
import com.cognifide.aet.runner.processing.SuiteExecutionFactory;
import com.cognifide.aet.runner.processing.SuiteExecutionTask;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(SuiteExecutorService.class)
@Component(immediate = true, description = "Runner Suite Executor Service", label = "Runner Suite Executor Service")
public class SuiteExecutorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutorService.class);

  private static final int MAX_POOL_SIZE = 20;

  private ScheduledExecutorService executor;

  @Reference
  private RunnerConfiguration runnerConfiguration;

  @Reference
  private MessagingConfiguration messagingConfiguration;

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private SuiteDataService suiteDataService;

  @Reference
  private SuiteExecutionFactory suiteExecutionFactory;

  @Activate
  public void activate(Map<String, String> properties) throws JMSException {
    LOGGER.debug("Activating SuiteExecutorService");
    executor = Executors.newScheduledThreadPool(MAX_POOL_SIZE);
  }

  @Deactivate
  public void deactivate() {
    LOGGER.debug("Deactivating SuiteExecutorService");
    if (executor != null) {
      executor.shutdown();
    }
  }

  public void scheduleSuite(Suite suite, Destination jmsReplyTo, boolean isMaintenanceMessage) {
    LOGGER.debug("Scheduling {}!", suite);
    SuiteExecutionTask task = new SuiteExecutionTask(suite, jmsReplyTo, suiteDataService,
        jmsConnection, runnerConfiguration, suiteExecutionFactory);
    executor.submit(task);
  }
}
