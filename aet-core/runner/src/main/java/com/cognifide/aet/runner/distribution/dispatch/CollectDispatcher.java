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
package com.cognifide.aet.runner.distribution.dispatch;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.job.CollectorJobData;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.distribution.progress.ProgressLog;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;
import com.cognifide.aet.runner.util.MessagesManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * CollectDispatcher - divide and schedule collect work among workers
 * 
 * @author Maciej Laskowski
 */
public class CollectDispatcher extends StepManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectDispatcher.class);

	private final Integer urlPackageSize;

	private final TestSuiteRun testSuiteRun;

	private final CollectorJobScheduler collectorJobScheduler;

	private Map<String, Queue> environmentChannels;

	@Inject
	public CollectDispatcher(TimeoutWatch timeoutWatch, JmsConnection jmsConnection,
			@Named("messageTimeToLive") Long messageTimeToLive,
			@Named("urlPackageSize") Integer urlPackageSize, TestSuiteRun testSuiteRun,
			CollectorJobScheduler collectorJobScheduler) throws JMSException {
		super(timeoutWatch, jmsConnection, testSuiteRun.getCorrelationId(), messageTimeToLive);
		this.urlPackageSize = urlPackageSize;
		this.testSuiteRun = testSuiteRun;
		this.collectorJobScheduler = collectorJobScheduler;
		sender = session.createProducer(null);
		sender.setTimeToLive(messageTimeToLive);
		environmentChannels = new HashMap<>();
	}

	@Override
	public ProgressLog getProgress() {
		return null;
	}

	@Override
	public int getTotalTasksCount() {
		return 0;
	}

	public void process(TestSuiteRun testSuiteRun) throws JMSException {
		Deque<MessageWithDestination> messagesQueue = Queues.newArrayDeque();
		LOGGER.info("Starting processing new Test Suite. CorrelationId: {} ", correlationId);

		for (TestRun testRun : testSuiteRun.getTestRunMap().values()) {
			processUrlsAndGroupToPackages(messagesQueue, testRun, testSuiteRun.getEnvironment());
		}
		collectorJobScheduler.add(messagesQueue, testSuiteRun.getCorrelationId());
		LOGGER.info("MessagesQueue was added to collectorJobScheduler. CorrelationId: {} ", correlationId);
	}

	public void cancel(String correlationId) {
		collectorJobScheduler.cleanup(correlationId);
	}

	private void processUrlsAndGroupToPackages(Deque<MessageWithDestination> messagesQueue, TestRun testRun,
			String environment) throws JMSException {
		Queue environmentQueue = getEnvironmentQueue(environment);
		int msgIndex = 0;
		final int totalUrls = testRun.getUrls().size();
		List<CollectMetadata> urlNodeKeysList = Lists.newArrayList();
		for (ExtendedUrl extendedUrl : testRun.getUrls()) {
			msgIndex++;
			urlNodeKeysList.add(createNodeKey(testRun, extendedUrl));
			if (msgIndex % urlPackageSize == 0 || msgIndex == totalUrls) {
				final CollectorJobData data = getCollectorJobData(testRun, urlNodeKeysList);
				ObjectMessage message = session.createObjectMessage(data);
				message.setJMSCorrelationID(correlationId);
				messagesQueue.add(new MessageWithDestination(environmentQueue, message, urlNodeKeysList
						.size()));
				urlNodeKeysList.clear();
			}
		}
	}

	private CollectMetadata createNodeKey(TestRun testRun, ExtendedUrl extendedUrl) {
		String testedUrl;
		if (StringUtils.isNotBlank(testSuiteRun.getDomain())) {
			testedUrl = testSuiteRun.getDomain() + extendedUrl.getUrl();
		} else {
			testedUrl = extendedUrl.getUrl();
		}
		CollectMetadata collectMetadata = new CollectMetadata(testSuiteRun.getCompany(),
				testSuiteRun.getProject(), testSuiteRun.getName(), testSuiteRun.getEnvironment(),
				testSuiteRun.getCorrelationId(), testRun.getName(), testedUrl, extendedUrl.getName(), null,
				null);
		collectMetadata.setDomain(testSuiteRun.getDomain());
		collectMetadata.setDescription(extendedUrl.getDescription());
		collectMetadata.setVersion(testSuiteRun.getVersion());
		return collectMetadata;
	}

	@Override
	public void onMessage(Message message) {
		// do nothing, this is first step
	}

	private Queue getEnvironmentQueue(String environment) throws JMSException {
		Queue currentChannel;
		String queueId = MessagesManager.createFullQueueName(environment);
		if (environmentChannels.containsKey(queueId)) {
			currentChannel = environmentChannels.get(queueId);
		} else {
			currentChannel = session.createQueue(queueId);
			environmentChannels.put(queueId, currentChannel);
		}
		return currentChannel;
	}

	private CollectorJobData getCollectorJobData(TestRun testRun, List<CollectMetadata> urlNodeKeysList) {
		return new CollectorJobData(urlNodeKeysList, testRun.getCollectorSteps(), testRun.getName(),
				testRun.getUseProxy());
	}

	@Override
	protected String getQueueInName() {
		return null;
	}

	@Override
	protected String getQueueOutName() {
		return null;
	}

	@Override
	protected String getStepName() {
		return null;
	}

}
