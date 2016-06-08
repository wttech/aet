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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.config.ComparatorStep;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.job.CollectorResultData;
import com.cognifide.aet.communication.api.job.ComparatorJobData;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;
import com.cognifide.aet.runner.util.MessagesManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * CollectDispatcher - collects work from collector-workers, divides and schedules compare work among
 * compare-workers
 * 
 * @author Maciej Laskowski
 */
public class CompareDispatcher extends StepManager implements TaskFinishPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompareDispatcher.class);

	private static final String STEP_NAME = "COLLECTED";
	
	private final TestSuiteRun testSuiteRun;

	private final List<ChangeObserver> changeListeners;

	private final CollectorJobScheduler collectorJobScheduler;

	@Inject
	public CompareDispatcher(TimeoutWatch timeoutWatch, JmsConnection jmsConnection,
			TestSuiteRun testSuiteRun, @Named("messageTimeToLive") Long messageTimeToLive,
			CollectorJobScheduler collectorJobScheduler) throws JMSException {
		super(timeoutWatch, jmsConnection, testSuiteRun.getCorrelationId(), messageTimeToLive);
		this.testSuiteRun = testSuiteRun;
		this.collectorJobScheduler = collectorJobScheduler;
		this.messagesToReceive.getAndSet(testSuiteRun.getUrlsCount());
		this.changeListeners = new CopyOnWriteArrayList<>();
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			timeoutWatch.update();
			try {
				CollectorResultData collectorResultData = (CollectorResultData) ((ObjectMessage) message)
						.getObject();

				collectorJobScheduler.messageReceived(collectorResultData.getRequestMessageId(),
						message.getJMSCorrelationID());
				String testName = collectorResultData.getTestName();

				updateCounters(collectorResultData.getStatus());

				LOGGER.info(
						"Collection data message (ID: {}) with status {}! Total received successful {}, failed {} of {}. CorrelationId: {} TestName: {}",
						message.getJMSMessageID(), collectorResultData.getStatus(),
						messagesReceivedSuccess.get(), messagesReceivedFailed.get(), getTotalTasksCount(),
						correlationId, testName);

				if (collectorResultData.getStatus() == JobStatus.SUCCESS) {
					onSuccess(collectorResultData, testName);
				} else {
					onError(collectorResultData.getProcessingError());
				}

				if (isFinished()) {
					LOGGER.info("CompareDispatcher task finished.");
					finishTask();
				}
			} catch (JMSException e) {
				LOGGER.error("Error while collecting results in CompareDispatcher", e);
				onError(ProcessingError.collectingError(e.getMessage()));
			}
		}
	}

	private void finishTask() throws JMSException {
		for (ChangeObserver changeListener : changeListeners) {
			changeListener.informChangesCompleted();
		}
		LOGGER.debug("Closing consumer!");
		consumer.close();
	}

	private void onSuccess(CollectorResultData collectorResultData, String testName) throws JMSException {
		for (CollectMetadata collectNodeMetadata : collectorResultData.getCollectorNodeMetadatas()) {
			int scheduledMessagesNo = dispatch(collectNodeMetadata, testName);
			LOGGER.info("{} ComparatorJobData messages send to queue {}. CorrelationId: {} TestName: {}",
					scheduledMessagesNo, getQueueOutName(), correlationId, testName);
			for (ChangeObserver changeListener : changeListeners) {
				changeListener.updateAmountToReceive(scheduledMessagesNo);
			}
		}
	}

	public void addChangeObserver(ChangeObserver observer) {
		changeListeners.add(observer);
	}

	@Override
	public boolean isFinished() {
		return messagesReceivedSuccess.get() + messagesReceivedFailed.get() == messagesToReceive.get();
	}

	private int dispatch(CollectMetadata collectNodeMetadata, String testName) throws JMSException {
		int scheduledComparisonsNo = 0;

		TestRun testRun = testSuiteRun.getTestRunMap().get(testName);
		Collection<ComparatorStep> comparators = testRun.getComparatorSteps().get(
				collectNodeMetadata.getCollectorModule());
		if (comparators != null) {
			for (ComparatorStep comparatorStep : comparators) {
				if (comparatorStep.getCollectorName() == null
						|| comparatorStep.getCollectorName().equals(
								collectNodeMetadata.getCollectorModuleName())) {
					LOGGER.debug(
							"Sending comparatorStep message. CorrelationID: {} TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
							correlationId, testName, collectNodeMetadata.getUrlName(),
							collectNodeMetadata.getUrl(), collectNodeMetadata.getCollectorModuleName(),
							comparatorStep.getCollectorName(), comparatorStep.getModule(),
							comparatorStep.getName());
					createAndSendComparatorJobData(collectNodeMetadata, comparatorStep);
					scheduledComparisonsNo++;
				}
			}
		}

		return scheduledComparisonsNo;
	}

	private void createAndSendComparatorJobData(CollectMetadata collectNodeMetadata,
			ComparatorStep comparatorStep) throws JMSException {
		ObjectMessage message = session.createObjectMessage(new ComparatorJobData(collectNodeMetadata,
				comparatorStep));
		message.setJMSCorrelationID(correlationId);
		sender.send(message);
	}

	@Override
	protected String getQueueInName() {
		return MessagesManager.createFullQueueName("collectorResults");
	}

	@Override
	protected String getQueueOutName() {
		return MessagesManager.createFullQueueName("comparatorJobs");
	}

	@Override
	protected String getStepName() {
		return STEP_NAME;
	}
}
