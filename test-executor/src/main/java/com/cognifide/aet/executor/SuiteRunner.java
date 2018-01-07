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
package com.cognifide.aet.executor;

import com.cognifide.aet.communication.api.execution.ProcessingStatus;
import com.cognifide.aet.communication.api.execution.SuiteStatusResult;
import com.cognifide.aet.communication.api.messages.MessageType;
import com.cognifide.aet.communication.api.messages.TaskMessage;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.executor.common.MessageProcessor;
import com.cognifide.aet.executor.common.ProcessorFactory;
import com.cognifide.aet.executor.common.RunnerTerminator;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for running a single test suite and checking the processing status.
 */
public class SuiteRunner implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRunner.class);

  private Session session;

  private MessageProducer messageProducer;

  private MessageConsumer messageConsumer;

  private SuiteStatusHandler suiteStatusHandler;

  private RunnerTerminator runnerTerminator;

  private SuiteCacheUpdater suiteCacheUpdater;

  private Suite suite;

  private String inQueueName;

  private long messageReceiveTimeout;

  public SuiteRunner(Session session, CacheUpdater cacheUpdater,
      SuiteStatusHandler suiteStatusHandler, Suite suite, String inQueueName,
      long messageReceiveTimeout) {
    this.session = session;
    this.suiteStatusHandler = suiteStatusHandler;
    this.runnerTerminator = new RunnerTerminator();
    this.suite = suite;
    this.inQueueName = inQueueName;
    this.messageReceiveTimeout = messageReceiveTimeout;
    this.suiteCacheUpdater = new SuiteCacheUpdater(cacheUpdater, runnerTerminator, suite);
  }

  /**
   * Runs the test suite processing and starts a thread which checks the processing status.
   */
  public void runSuite() throws JMSException {
    messageProducer = session.createProducer(session.createQueue(inQueueName));
    Destination outRunnerDestination = session.createTemporaryQueue();
    messageConsumer = session.createConsumer(outRunnerDestination);

    TaskMessage taskMessage = new TaskMessage<>(MessageType.RUN, suite);
    ObjectMessage message = session.createObjectMessage(taskMessage);
    message.setJMSReplyTo(outRunnerDestination);
    messageProducer.send(message);

    startStatusUpdate();
    suiteCacheUpdater.start();
  }

  /**
   * Closes the temporary JMS queue.
   */
  public void close() {
    try {
      if (messageConsumer != null) {
        messageConsumer.close();
      }
      if (messageProducer != null) {
        messageProducer.close();
      }
      if (session != null) {
        session.close();
      }
    } catch (JMSException e) {
      LOGGER.error("Failed to close suite runner", e);
    }
  }

  /**
   * Terminates thread checking the suite processing status.
   */
  public void terminate() {
    runnerTerminator.finish();
  }

  /**
   * Checks the suite processing status.
   */
  @Override
  public void run() {
    while (runnerTerminator.isActive()) {
      try {
        SuiteStatusResult status = getSuiteStatus();
        if (status != null) {
          suiteStatusHandler.handle(suite.getCorrelationId(), status);
        } else {
          handleFatalError("Timeout was reached while receiving status message");
        }
      } catch (JMSException e) {
        LOGGER.error("Failed to receive status message", e);
        handleFatalError(e.getMessage());
      }
    }
    close();
  }

  private void startStatusUpdate() {
    Thread statusUpdateThread = new Thread(this);
    statusUpdateThread.start();
  }

  private SuiteStatusResult getSuiteStatus() throws JMSException {
    SuiteStatusResult status = null;
    Message statusMessage = messageConsumer.receive(messageReceiveTimeout);
    if (statusMessage != null) {
      MessageProcessor processor = ProcessorFactory.produce(statusMessage, runnerTerminator);
      if (processor != null) {
        status = processor.process();
      } else {
        status = new SuiteStatusResult(ProcessingStatus.UNKNOWN);
      }
    }
    return status;
  }

  private void handleFatalError(String errorMessage) {
    SuiteStatusResult status = new SuiteStatusResult(ProcessingStatus.FATAL_ERROR, errorMessage);
    suiteStatusHandler.handle(suite.getCorrelationId(), status);
    runnerTerminator.finish();
  }
}
