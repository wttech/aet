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

import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage;
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * SuiteAgent communicates all crucial info between suite and client
 */
class SuiteAgent implements Observer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteAgent.class);

  private final Session session;

  private final MessageProducer resultsProducer;

  private final ProcessingErrorMessageObserver processingErrorMessageObserver;

  private final SuiteIndexWrapper suite;

  private boolean suiteFinishForced;

  @Inject
  public SuiteAgent(JmsConnection jmsConnection, SuiteIndexWrapper suite,
                    ProcessingErrorMessageObserver processingErrorMessageObserver, Destination resultsDestination,
                    @Named("messageTimeToLive") Long messageTimeToLive) throws JMSException {
    this.suite = suite;
    this.session = jmsConnection.getJmsSession();
    this.processingErrorMessageObserver = processingErrorMessageObserver;

    resultsProducer = session.createProducer(resultsDestination);
    resultsProducer.setTimeToLive(messageTimeToLive);
  }

  @Override
  public void update(Observable observable, Object arg) {
    sendResultsMessage(getReportGenerationErrors());
  }

  void close() {
    JmsUtils.closeQuietly(resultsProducer);
  }

  void enforceSuiteFinish() {
    this.suiteFinishForced = true;
  }

  void sendFailMessage(List<String> errors) {
    sendResultsMessage(errors);
  }

  void onError(ProcessingError error) {
    processingErrorMessageObserver.update(null,
            new ProcessingErrorMessage(error, suite.get().getCorrelationId()));
  }

  private void sendResultsMessage(List<String> errors) {
    try {
      resultsProducer.send(session.createObjectMessage(prepareResultMessage(errors)));
    } catch (JMSException e) {
      LOGGER.error("Can't send fail message!", e);
    }
  }

  private List<String> getReportGenerationErrors() {
    List<String> errors = Collections.emptyList();
    if (suiteFinishForced) {
      errors = Collections.singletonList("Suite processing finish was forced after timeout!");
    }
    return errors;
  }

  // @formatter:off
	private FinishedSuiteProcessingMessage prepareResultMessage(List<String> errors) {
		final String correlationId = suite.get().getCorrelationId();
		FinishedSuiteProcessingMessage message =
				new FinishedSuiteProcessingMessage(FinishedSuiteProcessingMessage.Status.OK, correlationId);
		message.addErrors(errors);
		return message;
	}

	// @formatter:on

  public void addProcessingErrorMessagesObservable(Observable observable) {
    observable.addObserver(processingErrorMessageObserver);
  }

}
