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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.config.ReporterStep;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.job.ComparatorResultData;
import com.cognifide.aet.communication.api.job.ReporterJobData;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;
import com.cognifide.aet.runner.util.MessagesManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author lukasz.wieczorek
 */
public class ReportsDispatcher extends StepManager implements ChangeObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResultsDispatcher.class);

	private static final String STEP_NAME = "COMPARED";
	
	private final TestSuiteRun testSuiteRun;

	private final List<NodeMetadata> resultNodeMetadatas;

	private boolean collectingFinished;

	private volatile boolean reportsRequested;

	@Inject
	public ReportsDispatcher(TimeoutWatch timeoutWatch, JmsConnection jmsConnection,
			TestSuiteRun testSuiteRun, @Named("messageTimeToLive") Long messageTimeToLive)
			throws JMSException {
		super(timeoutWatch, jmsConnection, testSuiteRun.getCorrelationId(), messageTimeToLive);
		this.testSuiteRun = testSuiteRun;
		resultNodeMetadatas = Collections.synchronizedList(new ArrayList<NodeMetadata>());
	}

	@Override
	public void updateAmountToReceive(int additionalCount) {
		messagesToReceive.addAndGet(additionalCount);
	}

	@Override
	public void informChangesCompleted() {
		collectingFinished = true;
		scheduleReportsRequestIfFinished();
	}

	public void scheduleReportsRequestIfFinished() {
		if (allResultsReceived()) {
			LOGGER.info("All results received ({})! Scheduling reports request. CorrelationId: {}",
					messagesToReceive.get(), correlationId);
			JmsUtils.closeQuietly(consumer);
			scheduleReportsRequest();
		}
	}

	/**
	 * Schedules reports request. This method may be executed only once per ReportDispatcher life. All other
	 * attempts will be ignored and properly logged with error status.
	 *
	 * @return <b>true</b> if and only if request was sent into queue, <b>false</b> otherwise.
	 */
	public synchronized boolean scheduleReportsRequest() {
		boolean requestSent = false;
		if (!reportsRequested) {
			LOGGER.info(
					"Requesting reports creation! All results received: {}. Reports steps: {} CorrelationId: {}",
					allResultsReceived(), testSuiteRun.getReporterSteps().size(), correlationId);
			reportsRequested = true;
			for (ReporterStep reporterStep : testSuiteRun.getReporterSteps()) {
				createAndSendReporterJobData(reporterStep);
			}
			requestSent = true;
		} else {
			LOGGER.error("Reports creation already requested! CorrelationId: {}", correlationId);
		}
		return requestSent;
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			timeoutWatch.update();
			try {
				ComparatorResultData comparatorResultData = (ComparatorResultData) ((ObjectMessage) message)
						.getObject();

				updateCounters(comparatorResultData.getStatus());
				LOGGER.info(
						"Compare result message (ID: {}) with status {}! Total received successful {}, failed {} of {}. CorrelationId: {}",
						message.getJMSMessageID(), comparatorResultData.getStatus(),
						messagesReceivedSuccess.get(), messagesReceivedFailed.get(), getTotalTasksCount(),
						correlationId);

				if (comparatorResultData.getStatus() == JobStatus.SUCCESS) {
					resultNodeMetadatas.add(comparatorResultData.getComparatorNodeMetadata());
				} else {
					onError(comparatorResultData.getProcessingError());
				}
			} catch (JMSException e) {
				LOGGER.error("Error while collecting results in CompareDispatcher. CorrelationId: {}",
						correlationId, e);
				onError(ProcessingError.comparingError(e.getMessage()));
			} finally {
				scheduleReportsRequestIfFinished();
			}
		}
	}

	private void createAndSendReporterJobData(ReporterStep reporterStep) {
		try {
			ReporterJobData jobData = new ReporterJobData(resultNodeMetadatas, reporterStep, testSuiteRun.getTestRunMap());
			ObjectMessage message = session.createObjectMessage(jobData);
			message.setJMSCorrelationID(correlationId);
			sender.send(message);
			LOGGER.info("Sending reportJob message. CorrelationId: {} ReportModule: {}", correlationId,
					reporterStep.getModule());
		} catch (JMSException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private boolean allResultsReceived() {
		return collectingFinished
				&& messagesToReceive.get() == messagesReceivedSuccess.get() + messagesReceivedFailed.get();
	}

	@Override
	protected String getQueueInName() {
		return MessagesManager.createFullQueueName("comparatorResults");
	}

	@Override
	protected String getQueueOutName() {
		return MessagesManager.createFullQueueName("reporterJobs");
	}

	@Override
	protected String getStepName() {
		return STEP_NAME;
	}
}
