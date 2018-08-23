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

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.MessagesManager;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.cognifide.aet.runner.processing.steps.CollectDispatcher;
import com.cognifide.aet.runner.processing.steps.CollectionResultsRouter;
import com.cognifide.aet.runner.processing.steps.ComparisonResultsRouter;
import com.cognifide.aet.runner.scheduler.CollectorJobSchedulerService;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = SuiteExecutionFactory.class)
public class SuiteExecutionFactory {

  @Reference
  private RunnerConfiguration runnerConfiguration;

  @Reference
  private MessagesManager messagesManager;

  @Reference
  private CollectorJobSchedulerService collectorJobSchedulerService;

  @Reference
  private JmsConnection jmsConnection;

  CollectDispatcher newCollectDispatcher(TimeoutWatch timeoutWatch,
      SuiteIndexWrapper suite)
      throws JMSException {
    return new CollectDispatcher(timeoutWatch, jmsConnection, runnerConfiguration,
        collectorJobSchedulerService, suite);
  }

  CollectionResultsRouter newCollectionResultsRouter(TimeoutWatch timeoutWatch,
      SuiteIndexWrapper suite)
      throws JMSException {
    return new CollectionResultsRouter(timeoutWatch, jmsConnection,
        runnerConfiguration, collectorJobSchedulerService, suite);
  }

  ComparisonResultsRouter newComparisonResultsRouter(TimeoutWatch timeoutWatch,
      SuiteIndexWrapper suite) throws JMSException {
    return new ComparisonResultsRouter(timeoutWatch, jmsConnection,
        runnerConfiguration, suite);
  }

  MessagesSender newMessagesSender(Destination jmsReplyTo) throws JMSException {
    return new MessagesSender(jmsReplyTo, jmsConnection);
  }
}
