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
package com.cognifide.aet.runner.processing.steps;

import com.cognifide.aet.communication.api.job.CollectorJobData;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.scheduler.CollectorJobSchedulerService;
import com.cognifide.aet.runner.scheduler.MessageWithDestination;
import com.cognifide.aet.runner.processing.ProgressLog;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.cognifide.aet.runner.processing.TimeoutWatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CollectDispatcher - divide and schedule collect work among workers
 */
public class CollectDispatcher extends StepManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectDispatcher.class);

  private final Integer urlPackageSize;

  private final CollectorJobSchedulerService collectorJobScheduler;

  private final SuiteIndexWrapper suite;

  public CollectDispatcher(TimeoutWatch timeoutWatch, JmsConnection jmsConnection,
      RunnerConfiguration runnerConfiguration,
      CollectorJobSchedulerService collectorJobScheduler, SuiteIndexWrapper suite) throws JMSException {
    super(timeoutWatch, jmsConnection, suite.get().getCorrelationId(), runnerConfiguration.getMttl());
    this.urlPackageSize = runnerConfiguration.getUrlPackageSize();
    this.collectorJobScheduler = collectorJobScheduler;
    this.suite = suite;
    sender = session.createProducer(null);
    sender.setTimeToLive(runnerConfiguration.getMttl());
  }

  @Override
  public ProgressLog getProgress() {
    return null;
  }

  @Override
  public int getTotalTasksCount() {
    return 0;
  }

  public void process() throws JMSException {
    Deque<MessageWithDestination> messagesQueue = Queues.newArrayDeque();
    LOGGER.info("Starting processing new Test Suite. CorrelationId: {} ", correlationId);

    for (Test test : suite.get().getTests()) {
      processUrlsAndGroupToPackages(messagesQueue, test);
    }
    collectorJobScheduler.add(messagesQueue, suite.get().getCorrelationId());
    LOGGER.info("MessagesQueue was added to collectorJobScheduler. CorrelationId: {} ",
        correlationId);
  }

  public void cancel(String correlationId) {
    collectorJobScheduler.cancel(correlationId);
  }

  private void processUrlsAndGroupToPackages(Deque<MessageWithDestination> messagesQueue, Test test)
      throws JMSException {
    int msgIndex = 0;
    final int totalUrls = test.getUrls().size();
    List<Url> urlsToSend = Lists.newArrayList();
    for (Url testUrl : test.getUrls()) {
      msgIndex++;
      urlsToSend.add(testUrl);
      if (msgIndex % urlPackageSize == 0 || msgIndex == totalUrls) {
        final CollectorJobData data = new CollectorJobData(suite.get().getCompany(),
            suite.get().getProject(), suite.get().getName(), test.getName(), urlsToSend,
            test.getProxy(), test.getPreferredBrowserId());
        ObjectMessage message = session.createObjectMessage(data);
        message.setJMSCorrelationID(correlationId);
        messagesQueue.add(new MessageWithDestination(getQueueOut(), message, urlsToSend.size()));
        urlsToSend.clear();
      }
    }
  }

  @Override
  public void onMessage(Message message) {
    // do nothing, this is the first step
  }

  @Override
  protected String getQueueInName() {
    return null;
  }

  @Override
  protected String getQueueOutName() {
    return "AET.collectorJobs";
  }

  @Override
  protected String getStepName() {
    return null;
  }

}
