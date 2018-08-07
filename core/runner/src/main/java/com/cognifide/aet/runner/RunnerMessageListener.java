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

import com.cognifide.aet.communication.api.messages.BasicMessage;
import com.cognifide.aet.communication.api.messages.FatalErrorMessage;
import com.cognifide.aet.communication.api.messages.TaskMessage;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.runner.scheduler.CollectorJobSchedulerService;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens to incoming runner queue messages. When message received, schedules suite processing.
 */
@Component(immediate = true)
public class RunnerMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerMessageListener.class);

  private static final String API_QUEUE_IN = MessagesManager
      .createFullQueueName("runner-in");

  private static final String MAINTENANCE_QUEUE_IN = MessagesManager
      .createFullQueueName("maintenance-in");

  private MessageConsumer inConsumer;

  private MessageConsumer maintenanceConsumer;

  private Session session;

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private RunnerConfiguration runnerConfiguration;

  @Reference
  private SuiteExecutorService suiteExecutorService;

  @Reference
  private CollectorJobSchedulerService collectorJobSchedulerService;

  @Activate
  public void activate() {
    LOGGER.debug("Activating RunnerMessageListener");
    try {
      session = jmsConnection.getJmsSession();
      LOGGER.info("Start listening on {} queue.", API_QUEUE_IN);
      inConsumer = session.createConsumer(session.createQueue(API_QUEUE_IN));
      inConsumer.setMessageListener(this);
      LOGGER.info("Start listening on {} queue.", MAINTENANCE_QUEUE_IN);
      maintenanceConsumer = session.createConsumer(session.createQueue(MAINTENANCE_QUEUE_IN));
      maintenanceConsumer.setMessageListener(this);
    } catch (JMSException e) {
      LOGGER.error("Failed to connect to messages broker ", e);
    }
  }

  @Deactivate
  public void deactivate() {
    LOGGER.debug("Deactivating RunnerMessageListener");
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
      sendFatalMessage(wrapperMessage,
          "There was problem understanding a message. Please check if newest " +
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
          collectorJobSchedulerService.cancel(suiteToCancel.getCorrelationId());
          break;
        default:
          LOGGER.error("Unknown message type: {}!", message.getMessageType());
      }
    }
  }

  private void processTestSuite(Message wrapperMessage, TaskMessage message) {
    Suite suite = (Suite) message.getData();
    try {
      suiteExecutorService.scheduleSuite(suite, wrapperMessage.getJMSReplyTo());
    } catch (JMSException e) {
      LOGGER.error("Error wile processing RUN {}: ", suite.getCorrelationId(), e);
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
}
