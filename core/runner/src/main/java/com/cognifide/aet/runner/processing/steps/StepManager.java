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

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.runner.processing.ProgressLog;
import com.cognifide.aet.runner.processing.TimeoutWatch;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * StepManager - creates send/receive point for specified Test run step
 */
public abstract class StepManager extends Observable implements MessageListener {

  private static final String JMS_CORRELATION_ID_PATTERN = "JMSCorrelationID = '%s'";

  protected final Session session;

  private final Queue queueOut;

  protected MessageConsumer consumer;

  protected MessageProducer sender;

  protected String correlationId;

  protected TimeoutWatch timeoutWatch;

  protected final AtomicInteger messagesReceivedSuccess;

  protected final AtomicInteger messagesReceivedFailed;

  protected final AtomicInteger messagesToReceive;

  public StepManager(TimeoutWatch timeoutWatch, JmsConnection jmsConnection, String correlationId,
      long messageTimeToLive) throws JMSException {
    this.timeoutWatch = timeoutWatch;
    this.session = jmsConnection.getJmsSession();
    this.correlationId = correlationId;
    if (getQueueInName() != null) {
      Queue queueIn = session.createQueue(getQueueInName());
      consumer = session.createConsumer(queueIn,
          String.format(JMS_CORRELATION_ID_PATTERN, correlationId));
      consumer.setMessageListener(this);
    }
    if (getQueueOutName() != null) {
      queueOut = session.createQueue(getQueueOutName());
      sender = session.createProducer(queueOut);
      sender.setTimeToLive(messageTimeToLive);
    } else {
      queueOut = null;
    }
    this.messagesReceivedSuccess = new AtomicInteger(0);
    this.messagesReceivedFailed = new AtomicInteger(0);
    this.messagesToReceive = new AtomicInteger(0);
  }

  protected void onError(ProcessingError processingError) {
    setChanged();
    notifyObservers(new ProcessingErrorMessage(processingError, correlationId));
  }

  public void closeConnections() {
    JmsUtils.closeQuietly(session);
    JmsUtils.closeQuietly(consumer);
    JmsUtils.closeQuietly(sender);
  }

  public int getTotalTasksCount() {
    return messagesToReceive.get();
  }

  protected void updateCounters(JobStatus status) {
    if (status == JobStatus.SUCCESS) {
      messagesReceivedSuccess.getAndIncrement();
    } else {
      messagesReceivedFailed.getAndIncrement();
    }
  }

  public ProgressLog getProgress() {
    return new ProgressLog(getStepName(), messagesReceivedSuccess.get(),
        messagesReceivedFailed.get(),
        messagesToReceive.get());
  }

  protected Queue getQueueOut() {
    return queueOut;
  }

  protected abstract String getQueueInName();

  protected abstract String getQueueOutName();

  protected abstract String getStepName();

}
