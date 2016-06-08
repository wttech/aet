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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import com.cognifide.aet.queues.JmsUtils;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.RemoveInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.google.inject.Inject;

/**
 * TestLifeCycle - this runnable represents complete lifecycle of a single Suite Run
 * 
 * @author Maciej Laskowski
 */
public class TestLifeCycle implements Runnable, MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestLifeCycle.class);

	private final TestSuiteRun testSuiteRun;

	private final SuiteExecutor suiteExecutor;

	private final MessageConsumer topicConsumer;

	private final Session session;

	@Inject
	public TestLifeCycle(TestSuiteRun testSuiteRun, JmsConnection jmsConnection,
			Destination resultsDestination, SuiteExecutor suiteExecutor) throws JMSException {
		this.testSuiteRun = testSuiteRun;
		this.suiteExecutor = suiteExecutor;

		if (resultsDestination instanceof ActiveMQDestination) {
			ActiveMQTopic advisoryTopic = AdvisorySupport.getConsumerAdvisoryTopic(resultsDestination);
			session = jmsConnection.getJmsSession();
			topicConsumer = session.createConsumer(advisoryTopic);
			topicConsumer.setMessageListener(this);
		} else {
			topicConsumer = null;
			session = null;
		}
	}

	@Override
	public void run() {
		try {
			suiteExecutor.execute();
		} catch (Exception e) {
			LOGGER.error("Fatal exception while executing TestLifeCycle: {}", testSuiteRun, e);
		} finally {
			suiteExecutor.cleanup();
			JmsUtils.closeQuietly(topicConsumer);
			JmsUtils.closeQuietly(session);
		}
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof ActiveMQMessage
				&& ((ActiveMQMessage) message).getDataStructure() instanceof RemoveInfo) {
			RemoveInfo removeInfo = (RemoveInfo) ((ActiveMQMessage) message).getDataStructure();
			if (removeInfo.isConsumerRemove()) {
				suiteExecutor.abort();
			}
		}
	}

}
