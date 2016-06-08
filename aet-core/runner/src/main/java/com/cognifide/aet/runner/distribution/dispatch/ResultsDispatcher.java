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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.job.ReporterResultData;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;
import com.cognifide.aet.runner.util.MessagesManager;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author lukasz.wieczorek
 */
public class ResultsDispatcher extends StepManager implements TaskFinishPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportsDispatcher.class);

	private static final String STEP_NAME = "REPORTS";
	
	private boolean aborted;

	@Inject
	public ResultsDispatcher(TimeoutWatch timeoutWatch, JmsConnection jmsConnection,
			TestSuiteRun testSuiteRun, @Named("messageTimeToLive") Long messageTimeToLive)
			throws JMSException {
		super(timeoutWatch, jmsConnection, testSuiteRun.getCorrelationId(), messageTimeToLive);
		messagesToReceive.getAndSet(testSuiteRun.getReporterSteps().size());
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			timeoutWatch.update();
			try {
				ReporterResultData reporterResultData = (ReporterResultData) ((ObjectMessage) message)
						.getObject();

				updateCounters(reporterResultData.getStatus());
				LOGGER.info(
						"Reporter result message (ID: {}) with status {}! Total received successful {}, failed {} of {}. CorrelationId: {}",
						message.getJMSMessageID(), reporterResultData.getStatus(),
						messagesReceivedSuccess.get(), messagesReceivedFailed.get(), getTotalTasksCount(),
						correlationId);
				setChanged();
				notifyObservers(reporterResultData);
				if (reporterResultData.getStatus() == JobStatus.ERROR) {
					onError(reporterResultData.getProcessingError());
				}
			} catch (JMSException e) {
				LOGGER.error("Error while collecting results in CompareDispatcher", e);
				onError(ProcessingError.reportingError(e.getMessage()));
			}
		}
	}

	/**
	 * This task is finished, when all reports are received or was aborted
	 */
	@Override
	public boolean isFinished() {
		return messagesReceivedSuccess.get() + messagesReceivedFailed.get() == messagesToReceive.get()
				|| aborted;
	}

	@Override
	protected String getQueueInName() {
		return MessagesManager.createFullQueueName("reporterResults");
	}

	@Override
	protected String getQueueOutName() {
		return null;
	}

	public void abort() {
		if (!isFinished()) {
			LOGGER.warn("ResultsDispatcher aborted!. Still waiting for {} of {} reports!",
					messagesToReceive.get() - messagesReceivedSuccess.get() - messagesReceivedFailed.get(),
					messagesToReceive.get());
		}
		aborted = true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("aborted", aborted)
				.add("messagesToReceive", messagesToReceive)
				.add("messagesReceivedSuccess", messagesReceivedSuccess)
				.add("messagesReceivedFailed", messagesReceivedFailed).toString();
	}

	@Override
	protected String getStepName() {
		return STEP_NAME;
	}
}
