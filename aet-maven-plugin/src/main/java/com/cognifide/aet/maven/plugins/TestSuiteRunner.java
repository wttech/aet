/*
 * Cognifide AET :: Maven Plugin
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
package com.cognifide.aet.maven.plugins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.MessageType;
import com.cognifide.aet.communication.api.messages.TaskMessage;
import com.jcabi.log.Logger;

class TestSuiteRunner {

	private static final String DATE_FORMAT = "HH:mm:ss.SSS";

	private static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT);
		}
	};

	private static final String TIMEOUT_EXCEPTION_MESSAGE = "Timeout: no update received from the system since %d "
			+ "seconds. Suite execution will be aborted.";

	private final Connection connection;

	private final Session session;

	private final MessageProducer producer;

	private final Destination outRunnerDestination;

	private final long timeout;

	private final String buildDirectory;

	private MessageConsumer consumer;

	public TestSuiteRunner(String brokerURL, String userName, String password, String inQueueName,
			String buildDirectory, long timeout) throws JMSException {
		this.buildDirectory = buildDirectory;
		this.timeout = timeout;

		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(userName,
				password, brokerURL);
		activeMQConnectionFactory.setTrustAllPackages(true);
		connection = activeMQConnectionFactory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(session.createQueue(inQueueName));
		outRunnerDestination = session.createTemporaryQueue();
		connection.start();
	}

	public void runTestSuite(final TestSuiteRun testSuiteRun) throws AETException {
		RunnerTerminator runnerTerminator = new RunnerTerminator(testSuiteRun);
		try {
			TaskMessage taskMessage = new TaskMessage<>(MessageType.RUN, testSuiteRun);
			ObjectMessage message = session.createObjectMessage(taskMessage);
			message.setJMSReplyTo(outRunnerDestination);
			consumer = session.createConsumer(outRunnerDestination);
			producer.send(message);

			String now = DATE_FORMATTER.get().format(new Date());
			Logger.info(this, String.format("CorrelationID: %s", testSuiteRun.getCorrelationId()));
			Logger.info(this,
					"********************************************************************************");
			Logger.info(this,
					"********************** Job Setup finished at " + now + ".**********************");
			Logger.info(this,
					"*** Suite is now processed by the system, progress will be available below. ****");
			Logger.info(this,
					"********************************************************************************");
			while (runnerTerminator.isActive()) {
				Message received = consumer.receive(timeout);
				if (received != null) {
					MessageProcessor processor = ProcessorFactory.produce(received, buildDirectory,
							runnerTerminator);
					if (processor != null) {
						processor.process();
					}
				} else {
					throw new AETException(String.format(TIMEOUT_EXCEPTION_MESSAGE,
							TimeUnit.MILLISECONDS.toSeconds(timeout)));
				}
			}
		} catch (JMSException e) {
			throw new AETException("JMS error", e);
		} finally {
			close();
			Logger.info(this, String.format("Total: %d of %d reports received.",
					runnerTerminator.getMessagesReceived(), runnerTerminator.getMessagesToReceive()));
		}
	}

	private void close() throws AETException {
		try {
			if (consumer != null) {
				consumer.close();
			}
			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			throw new AETException("JMS Failed to close connections", e);
		}
	}

}
