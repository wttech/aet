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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.job.ReporterResultData;
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.messages.ReportMessage;
import com.cognifide.aet.communication.api.messages.ReportMessageBuilder;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.vs.ReportResult;
import com.cognifide.aet.vs.VersionStorage;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * SuiteAgent communicates all crucial info between testSuiteRun and client
 * 
 * @Author: Maciej Laskowski
 * @Date: 31.03.15
 */
class SuiteAgent implements Observer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuiteAgent.class);

	private final VersionStorage versionStorage;

	private final Session session;

	private final MessageProducer resultsProducer;

	private final TestSuiteRun testSuiteRun;

	private final ProcessingErrorMessageObserver processingErrorMessageObserver;

	private boolean reportGenerationForced;

	@Inject
	public SuiteAgent(VersionStorage versionStorage, JmsConnection jmsConnection, TestSuiteRun testSuiteRun,
			ProcessingErrorMessageObserver processingErrorMessageObserver, Destination resultsDestination,
			@Named("messageTimeToLive") Long messageTimeToLive) throws JMSException {
		this.versionStorage = versionStorage;
		this.session = jmsConnection.getJmsSession();
		this.testSuiteRun = testSuiteRun;
		this.processingErrorMessageObserver = processingErrorMessageObserver;

		resultsProducer = session.createProducer(resultsDestination);
		resultsProducer.setTimeToLive(messageTimeToLive);
	}

	@Override
	public void update(Observable observable, Object arg) {
		if (arg instanceof ReporterResultData) {
			sendResultsMessage((ReporterResultData) arg, getReportGenerationErrors());
		} else {
			LOGGER.warn("Unknown argument: {} from {}", arg, observable);
		}
	}

	void close() {
		JmsUtils.closeQuietly(resultsProducer);
	}

	void setReportGenerationForced() {
		this.reportGenerationForced = true;
	}

	void sendFailMessage(List<String> errors) {
		sendResultsMessage(null, errors);
	}

	void onError(ProcessingError error) {
		processingErrorMessageObserver.update(null,
				new ProcessingErrorMessage(error, testSuiteRun.getCorrelationId()));
	}

	private void sendResultsMessage(ReporterResultData reporterResultData, List<String> errors) {
		try {
			resultsProducer.send(session
					.createObjectMessage(prepareResultMessage(reporterResultData, errors)));
		} catch (JMSException e) {
			LOGGER.error("Can't send fail message!", e);
		}
	}

	private List<String> getReportGenerationErrors() {
		List<String> errors = Collections.emptyList();
		if (reportGenerationForced) {
			errors = Collections.singletonList("Report generated after timeout!");
		}
		return errors;
	}

	// @formatter:off
	private ReportMessage prepareResultMessage(ReporterResultData reporterResultData, List<String> errors) {
		ReportMessageBuilder reportMessageBuilder = ReportMessageBuilder.fromTestSuiteRun(testSuiteRun);
		List<String> reportErrors = new LinkedList<>(errors);

		ReportMessage.Status status = ReportMessage.Status.FAILED;
		if (reporterResultData != null && reporterResultData.getStatus() == JobStatus.SUCCESS) {
			try {
				ReportResult result = versionStorage.getReportNode(
						reporterResultData.getReporterNodeMetadata()).getResult(ReportResult.class);
				reportMessageBuilder.withFullReportUrl(result.getFullReportUrl()).withSaveAsFileName(
						result.getSaveAsFileName());
				status = ReportMessage.Status.OK;
			} catch (VersionStorageException e) {
				reportErrors.add(e.getMessage());
				onError(ProcessingError.reportingError(e.getMessage()));
				LOGGER.error(e.getMessage(), e);
			}
		} else if (reporterResultData != null) {
			ProcessingError processingError = reporterResultData.getProcessingError();
			reportErrors.add(processingError.getDescription());
			onError(processingError);
		}
		reportMessageBuilder.withStatus(status).withErrors(reportErrors);

		return reportMessageBuilder.build();
	}

	// @formatter:on

	public void addProcessingErrorMessagesObservable(Observable observable) {
		observable.addObserver(processingErrorMessageObserver);
	}

}
