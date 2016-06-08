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

import com.cognifide.aet.comments.api.manager.CommentsManager;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.ProgressMessage;
import com.cognifide.aet.communication.api.messages.ReportMessage;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.communication.api.node.builders.NodeMetadataBuilder;
import com.cognifide.aet.communication.api.node.builders.UrlNodeMetadataBuilder;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.runner.distribution.dispatch.CollectDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.CompareDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.ReportsDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.ResultsDispatcher;
import com.cognifide.aet.runner.distribution.progress.ProgressLog;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;
import com.cognifide.aet.runner.validation.ExtendedUrlsValidator;
import com.cognifide.aet.vs.VersionStorage;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SuiteExecutor - executes testSuiteRun, execution consists of three steps: preparation, validation and
 * processing
 *
 * @Author: Maciej Laskowski
 * @Date: 31.03.15
 */
class SuiteExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutor.class);

	private static final String SYSTEM_IN_MAINTENANCE_MESSAGE = "The AET System is currently down for maintenance. We’ll be back soon!";

	private static final String SYSTEM_RUNNING_NORMALLY_MESSAGE = "Maintenance mode is switched off. Please run the test in normal mode.";

	private static final String RUNNING_TASK_IN_MAINTENANCE_MODE = "Running task in maintenance mode!";

	private final VersionStorage versionStorage;

	private final CommentsManager commentsManager;

	private final TimeoutWatch timeoutWatch;

	private final TestSuiteRun testSuiteRun;

	private final CollectDispatcher collectDispatcher;

	private final CompareDispatcher compareDispatcher;

	private final ReportsDispatcher reportsDispatcher;

	private final ResultsDispatcher resultsDispatcher;

	private final SuiteAgent suiteAgent;

	private final ProgressMessageObserver progressMessageObserver;

	private final Long runTimeoutInSeconds;

	private final SuiteExecutionSettings suiteExecutionSettings;

	private boolean processed;

	@Inject SuiteExecutor(SuiteExecutionSettings suiteExecutionSettings, VersionStorage versionStorage,
			CommentsManager commentsManager,
			TimeoutWatch timeoutWatch, TestSuiteRun testSuiteRun, CollectDispatcher collectDispatcher,
			CompareDispatcher compareDispatcher, ReportsDispatcher reportsDispatcher,
			ResultsDispatcher resultsDispatcher, SuiteAgent suiteAgent,
			ProgressMessageObserver progressMessageObserver,
			@Named("failureTimeout") long runTimeoutInSeconds) {
		this.suiteExecutionSettings = suiteExecutionSettings;
		this.versionStorage = versionStorage;
		this.commentsManager = commentsManager;
		this.timeoutWatch = timeoutWatch;
		this.testSuiteRun = testSuiteRun;
		this.collectDispatcher = collectDispatcher;
		this.compareDispatcher = compareDispatcher;
		this.reportsDispatcher = reportsDispatcher;
		this.resultsDispatcher = resultsDispatcher;
		this.suiteAgent = suiteAgent;
		this.progressMessageObserver = progressMessageObserver;
		this.runTimeoutInSeconds = runTimeoutInSeconds;

		suiteAgent.addProcessingErrorMessagesObservable(compareDispatcher);
		suiteAgent.addProcessingErrorMessagesObservable(reportsDispatcher);
		suiteAgent.addProcessingErrorMessagesObservable(resultsDispatcher);
		resultsDispatcher.addObserver(suiteAgent);
		compareDispatcher.addChangeObserver(reportsDispatcher);
	}

	void execute() {
		if (suiteExecutionSettings.shouldExecute()) {
			executeSuite();
		} else {
			finishWithoutExecution();
		}
	}

	void cleanup() {
		clearCommentsIfReportGenerationFailed();
		reportsDispatcher.closeConnections();
		resultsDispatcher.closeConnections();
		compareDispatcher.closeConnections();
		collectDispatcher.closeConnections();
		suiteAgent.close();
	}

	private void clearCommentsIfReportGenerationFailed() {
		UrlNodeMetadataBuilder urlNodeMetadataBuilder = UrlNodeMetadataBuilder.getUrlNodeMetadata()
				.withCompany(testSuiteRun.getCompany()).withProject(testSuiteRun.getProject())
				.withTestSuiteName(testSuiteRun.getName()).withEnvironment(testSuiteRun.getEnvironment())
				.withCorrelationId(testSuiteRun.getCorrelationId());
		UrlNodeMetadata urlNodeMetadata = urlNodeMetadataBuilder.build();
		ReportMetadata reportMetadata = ReportMetadata.fromUrlNodeMetadata(urlNodeMetadata, null);
		if (versionStorage.getReportsExecutions(reportMetadata).isEmpty()) {
			commentsManager.deleteComments(urlNodeMetadata);
		}
	}

	/**
	 * Immediately aborts all AET tasks for current life cycle.
	 */
	synchronized void abort() {
		if (processed) {
			if (!resultsDispatcher.isFinished()) {
				LOGGER.warn("Consumer removed - aborting TestLifeCycle for task: {}",
						testSuiteRun.getCorrelationId());
			}
			resultsDispatcher.abort();
			collectDispatcher.cancel(testSuiteRun.getCorrelationId());
		} else {
			processed = true;
			LOGGER.warn("Consumer removed - aborting TestLifeCycle for task: {} before processing",
					testSuiteRun.getCorrelationId());
		}
	}

	private void executeSuite() {
		if (suiteExecutionSettings.isMaintenanceMessage()) {
			LOGGER.info(RUNNING_TASK_IN_MAINTENANCE_MODE);
			progressMessageObserver.update(null, new ProgressMessage(RUNNING_TASK_IN_MAINTENANCE_MODE));
		}
		try {
			ExecutionTimer timer = ExecutionTimer.createAndRun("RUNNER");
			LOGGER.info("Start lifecycle of test suite run: {}", testSuiteRun);
			timeoutWatch.update();
			prepare();
			List<String> errors = validate();
			process(timer, errors);
		} catch (JMSException | AETException e) {
			LOGGER.error("Can't run TestLifeCycle!", e);
			suiteAgent.sendFailMessage(Collections.singletonList(e.getMessage()));
		}
	}

	private void finishWithoutExecution() {
		String failMessage;
		if (suiteExecutionSettings.getRunnerMode() == RunnerMode.MAINTENANCE) {
			failMessage = SYSTEM_IN_MAINTENANCE_MESSAGE;
		} else {
			failMessage = SYSTEM_RUNNING_NORMALLY_MESSAGE;
		}
		LOGGER.warn(failMessage);
		suiteAgent.sendFailMessage(Collections.singletonList(failMessage));
	}

	private List<String> validate() {
		List<String> errors = Lists.newArrayList();
		for (TestRun testRun : testSuiteRun.getTestRunMap().values()) {
			errors.addAll(ExtendedUrlsValidator.validate(testRun.getUrls()));
		}
		return errors;
	}

	private void prepare() throws AETException {
		NodeMetadataBuilder nodeMetadataBuilder = NodeMetadataBuilder.getNodeMetadata()
				.withCompany(testSuiteRun.getCompany()).withProject(testSuiteRun.getProject())
				.withTestSuiteName(testSuiteRun.getName()).withEnvironment(testSuiteRun.getEnvironment())
				.withDomain(testSuiteRun.getDomain());
		Long lastTestSuiteVersion = versionStorage.getLastTestSuiteVersion(nodeMetadataBuilder.build());
		testSuiteRun.setVersion(++lastTestSuiteVersion);
	}

	private void process(ExecutionTimer timer, List<String> validationErrors)
			throws JMSException, AETException {
		if (!validationErrors.isEmpty()) {
			LOGGER.debug("Sending report message with status: {} caused by validation errors.",
					ReportMessage.Status.FAILED);
			suiteAgent.sendFailMessage(validationErrors);
		} else if (tryProcess()) {
			checkStatusUntilFinishedOrTimedOut();
			if (resultsDispatcher.isFinished()) {
				timer.finish();
				LOGGER.info("Finished lifecycle of test run: {}. Task finished in {} ms ({}).",
						testSuiteRun.getCorrelationId(), timer.getExecutionTimeInMillis(),
						timer.getExecutionTimeInMMSS());
				LOGGER.info("Total tasks finished in steps: collect: {}; compare: {}.",
						compareDispatcher.getTotalTasksCount(), resultsDispatcher.getTotalTasksCount());
			} else if (reportTimedOutAndNotFinished()) {
				timer.finish();
				LOGGER.warn(
						"Lifecycle of run {} interrupted after {} ms ({}). Last message received: {} seconds ago... Trying to force report generation.",
						testSuiteRun.getCorrelationId(), timer.getExecutionTimeInMillis(),
						timer.getExecutionTimeInMMSS(),
						TimeUnit.NANOSECONDS.toSeconds(timeoutWatch.getLastUpdateDifference()));
				forceReportGeneration();
				collectDispatcher.cancel(testSuiteRun.getCorrelationId());
			}
		}
	}

	private boolean reportTimedOutAndNotFinished() {
		return timeoutWatch.isTimedOut(runTimeoutInSeconds) && !resultsDispatcher.isFinished();
	}

	private void checkStatusUntilFinishedOrTimedOut() {
		String logMessage = "";
		while (!resultsDispatcher.isFinished() && !timeoutWatch.isTimedOut(runTimeoutInSeconds)) {
			try {

				String currentLog = composeProgressLog();
				if (!currentLog.equals(logMessage)) {
					logMessage = currentLog;
					LOGGER.info("[{}]: {}", testSuiteRun.getCorrelationId(), logMessage);
					progressMessageObserver.update(null, new ProgressMessage(logMessage));
				}
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private String composeProgressLog() {
		ProgressLog compareLog = compareDispatcher.getProgress();
		ProgressLog reportLog = reportsDispatcher.getProgress();
		ProgressLog resultLog = resultsDispatcher.getProgress();
		return StringUtils.join(Arrays.asList(compareLog, reportLog, resultLog), " ::: ");
	}

	private synchronized boolean tryProcess() throws JMSException, AETException {
		boolean result = !processed;
		if (!processed) {
			copyComments();
			collectDispatcher.process(testSuiteRun);
			processed = true;
		}
		return result;
	}

	private void copyComments() throws  AETException {
		UrlNodeMetadataBuilder urlNodeMetadataBuilder = UrlNodeMetadataBuilder.getUrlNodeMetadata()
				.withCompany(testSuiteRun.getCompany()).withProject(testSuiteRun.getProject())
				.withTestSuiteName(testSuiteRun.getName()).withEnvironment(testSuiteRun.getEnvironment())
				.withDomain(testSuiteRun.getDomain());
		UrlNodeMetadata urlNodeMetadata = urlNodeMetadataBuilder.build();
		String lastReportCorrelationId = versionStorage.getLastReportCorrelationId(urlNodeMetadata);
		urlNodeMetadata.setCorrelationId(lastReportCorrelationId);
		urlNodeMetadata.setDomain(null);
		urlNodeMetadata.setVersion(null);
		commentsManager.copyReportComments(urlNodeMetadata, testSuiteRun.getCorrelationId());
	}

	private void forceReportGeneration() {
		suiteAgent.onError(ProcessingError
				.reportingError("Report will be generated after timeout - some results might be missing!"));
		suiteAgent.setReportGenerationForced();
		timeoutWatch.update();
		reportsDispatcher.scheduleReportsRequest();
		checkStatusUntilFinishedOrTimedOut();
		if (!resultsDispatcher.isFinished()) {
			suiteAgent.sendFailMessage(Collections
					.singletonList("Failed to generate reports because it took too much time!"));
		}
	}

}
