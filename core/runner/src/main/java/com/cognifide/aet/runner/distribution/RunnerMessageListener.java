/**
 * AET
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
package com.cognifide.aet.runner.distribution;

import com.cognifide.aet.communication.api.messages.BasicMessage;
import com.cognifide.aet.communication.api.messages.FatalErrorMessage;
import com.cognifide.aet.communication.api.messages.TaskMessage;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.vs.StorageException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens to incoming runner queue messages. When message received, starts suite process.
 */
@Singleton
public class RunnerMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerMessageListener.class);

  private final String maintenanceDestinationName;

  private final TestRunProcessor testRunProcessor;

  private final MessageConsumer inConsumer;

  private final MessageConsumer maintenanceConsumer;

  private final Session session;

  @Inject
  public RunnerMessageListener(JmsConnection jmsConnection, @Named("API in") String inDestinationName,
                               @Named("MAINTENANCE in") String maintenanceDestinationName, TestRunProcessor testRunProcessor,
                               RunnerMode runnerMode) throws JMSException {
    this.maintenanceDestinationName = maintenanceDestinationName;
    this.testRunProcessor = testRunProcessor;
    session = jmsConnection.getJmsSession();
    if (runnerMode != RunnerMode.OFFLINE) {
      LOGGER.info("Start listening on {} queue.", inDestinationName);
      inConsumer = session.createConsumer(session.createQueue(inDestinationName));
      inConsumer.setMessageListener(this);
    } else {
      inConsumer = null;
    }
    LOGGER.info("Start listening on {} queue.", maintenanceDestinationName);
    maintenanceConsumer = session.createConsumer(session.createQueue(maintenanceDestinationName));
    maintenanceConsumer.setMessageListener(this);
  }

  public void close() {
    JmsUtils.closeQuietly(maintenanceConsumer);
    JmsUtils.closeQuietly(inConsumer);
    JmsUtils.closeQuietly(session);
  }

  @Override
  public void onMessage(Message wrapperMessage) {
    BasicMessage message = null;
    try {
      message = JmsUtils.getFromMessage(wrapperMessage, BasicMessage.class);
    } catch (Exception e) {
      sendFatalMessage(wrapperMessage, "There was problem understanding a message. Please check if newest " +
              "version of AET client is used.");
      LOGGER.error("Fatal error on Runner Message Listener onMessage", e);
    }
    if (message != null) {
      switch (message.getMessageType()) {
        case RUN:
          processTestSuite(wrapperMessage, (TaskMessage) message);
          break;
        case CANCEL:
          //This step is not implemented on client
          Suite suiteToCancel = (Suite) ((TaskMessage) message).getData();
          cancelSuiteExecution(suiteToCancel.getCorrelationId());
          break;
        default:
          LOGGER.error("Unknown message type: {}!", message.getMessageType());
      }
    }
  }

  private void processTestSuite(Message wrapperMessage, TaskMessage message) {
    Suite suite = (Suite) message.getData();
    try {
      boolean isMaintenanceMessage = StringUtils.endsWith(
              wrapperMessage.getJMSDestination().toString(), maintenanceDestinationName);
      testRunProcessor.process(suite, wrapperMessage.getJMSReplyTo(), isMaintenanceMessage);
    } catch (JMSException e) {
      LOGGER.error("Error wile processing RUN {}: ", suite.getCorrelationId(), e);
      sendFatalMessage(wrapperMessage, e.getMessage());
    } catch (StorageException e) {
      LOGGER.error("Failed to process test suite", e);
      sendFatalMessage(wrapperMessage, e.getMessage());
    }
  }

  private void sendFatalMessage(Message wrapperMessage, String message) {
    try {
      final Destination jmsReplyTo = wrapperMessage.getJMSReplyTo();
      if (jmsReplyTo != null) {
        final MessageProducer producer = session.createProducer(jmsReplyTo);
        final FatalErrorMessage errorMessage =
                new FatalErrorMessage("Failed to process test suite: " + message,
                        wrapperMessage.getJMSCorrelationID());
        producer.send(session.createObjectMessage(errorMessage));
        producer.close();
      }
    } catch (Exception e) {
      LOGGER.error("Fatal exception, can't deliver message {}!", message, e);
    }
  }

  private void cancelSuiteExecution(String correlationID) {
    testRunProcessor.cancel(correlationID);
  }

}
