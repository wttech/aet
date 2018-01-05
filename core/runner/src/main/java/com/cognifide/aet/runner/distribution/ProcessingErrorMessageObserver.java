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

import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Observable;
import java.util.Observer;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessingErrorMessageObserver implements Observer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingErrorMessageObserver.class);

  private final Session session;

  private final MessageProducer resultsProducer;

  @Inject
  public ProcessingErrorMessageObserver(Destination resultsDestination, JmsConnection jmsConnection,
                                        @Named("messageTimeToLive") Long messageTimeToLive) throws JMSException {
    this.session = jmsConnection.getJmsSession();
    resultsProducer = session.createProducer(resultsDestination);
    resultsProducer.setTimeToLive(messageTimeToLive);
  }

  @Override
  public void update(Observable o, Object data) {
    if (canProcess(data)) {
      try {
        ProcessingErrorMessage message = (ProcessingErrorMessage) data;
        LOGGER.debug("Error: {}, phase: {}, correlationId: {}", message.getProcessingError()
                .getDescription(), message.getProcessingError().getPhase(), message
                .getCorrelationId());
        resultsProducer.send(session.createObjectMessage(message));
      } catch (JMSException e) {
        LOGGER.warn("Failed to send ProcessingErrorMessage", e);
      }
    }
  }

  private boolean canProcess(Object data) {
    return data instanceof ProcessingErrorMessage;
  }

}
