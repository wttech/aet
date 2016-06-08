/*
 * Cognifide AET :: Runner
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.runner.distribution;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.messages.BasicMessage;
import com.cognifide.aet.communication.api.messages.TaskMessage;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Listens to incoming runner queue messages. When message received, starts TestSuiteRun process.
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
		BasicMessage message = JmsUtils.getFromMessage(wrapperMessage, BasicMessage.class);
		if (message != null) {
			switch (message.getMessageType()) {
				case RUN:
					processTestSuite(wrapperMessage, (TaskMessage) message);
					break;
				case CANCEL:
					TestSuiteRun suiteToCancel = (TestSuiteRun) ((TaskMessage) message).getData();
					cancelSuiteExecution(suiteToCancel.getCorrelationId());
					break;
				default:
					LOGGER.error("Unknown message type: {}!", message.getMessageType());
			}

		}
	}

	private void processTestSuite(Message wrapperMessage, TaskMessage message) {
		TestSuiteRun suite = (TestSuiteRun) message.getData();
		try {
			boolean isMaintenanceMessage = StringUtils.endsWith(
					wrapperMessage.getJMSDestination().toString(), maintenanceDestinationName);
			testRunProcessor.process(suite, wrapperMessage.getJMSReplyTo(), isMaintenanceMessage);
		} catch (JMSException e) {
			LOGGER.error("Error wile processing RUN {}: ", suite.getCorrelationId(), e);
		}
	}

	private void cancelSuiteExecution(String correlationID) {
		testRunProcessor.cancel(correlationID);
	}

}
