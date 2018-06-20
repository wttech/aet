/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.runner.processing;

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage;
import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage.Status;
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.messages.ProgressMessage;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.runner.CollectorJobScheduler;
import com.cognifide.aet.runner.configs.RunnerConfiguration;
import com.cognifide.aet.vs.StorageException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuiteExecutionTask implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutionTask.class);

  private final Suite suite;
  private final Destination jmsReplyTo;
  private final SuiteDataService suiteDataService;
  private final JmsConnection jmsConnection;
  private final RunnerConfiguration runnerConfiguration;
  private final CollectorJobScheduler collectorJobScheduler;

  private boolean processed;

  private SuiteIndexWrapper indexedSuite;

  private TimeoutWatch timeoutWatch;
  private CollectDispatcher collectDispatcher;
  private CollectionResultsRouter collectionResultsRouter;
  private ComparisonResultsRouter comparisonResultsRouter;
  private MessagesSender messagesSender;

  public SuiteExecutionTask(Suite suite, Destination jmsReplyTo,
      SuiteDataService suiteDataService,
      JmsConnection jmsConnection, RunnerConfiguration runnerConfiguration,
      CollectorJobScheduler collectorJobScheduler) {
    this.suite = suite;
    this.jmsReplyTo = jmsReplyTo;
    this.suiteDataService = suiteDataService;
    this.jmsConnection = jmsConnection;
    this.runnerConfiguration = runnerConfiguration;
    this.collectorJobScheduler = collectorJobScheduler;
  }

  @Override
  public void run() {
    try {
      prepareSuiteWrapper();
      init();
      process();
      save();
    } catch (StorageException | JMSException | ValidatorException e) {
      LOGGER.error("Error during processing suite {}", suite, e);
    } finally {
      cleanup();
    }
  }

  private void prepareSuiteWrapper() throws StorageException {
    LOGGER.debug("Fetching suite patterns {}", suite);
    indexedSuite = new SuiteIndexWrapper(suiteDataService.enrichWithPatterns(suite));
  }

  private void init() throws JMSException {
    LOGGER.debug("Initializing suite processors {}", suite);
    timeoutWatch = new TimeoutWatch();
    collectDispatcher = new CollectDispatcher(timeoutWatch, jmsConnection, runnerConfiguration,
        collectorJobScheduler, indexedSuite);
    collectionResultsRouter = new CollectionResultsRouter(timeoutWatch, jmsConnection,
        runnerConfiguration, collectorJobScheduler, indexedSuite);
    comparisonResultsRouter = new ComparisonResultsRouter(timeoutWatch, jmsConnection,
        runnerConfiguration, indexedSuite);
    messagesSender = new MessagesSender(jmsReplyTo, jmsConnection);

    collectionResultsRouter.addObserver(messagesSender);
    comparisonResultsRouter.addObserver(messagesSender);
    collectionResultsRouter.addChangeObserver(comparisonResultsRouter);
  }

  private void process() {
    try {
      ExecutionTimer timer = ExecutionTimer.createAndRun("RUNNER");
      LOGGER.info("Start lifecycle of test suite run: {}", indexedSuite.get());
      timeoutWatch.update();
      process(timer);
    } catch (JMSException | AETException e) {
      LOGGER.error("Can't process suite {}!", suite.getCorrelationId(), e);
      FinishedSuiteProcessingMessage message = new FinishedSuiteProcessingMessage(Status.FAILED,
          suite.getCorrelationId());
      message.addError(e.getMessage());
      messagesSender.sendMessage(message);
    }
  }

  private void process(ExecutionTimer timer)
      throws JMSException, AETException {
    if (tryProcess()) {
      checkStatusUntilFinishedOrTimedOut();
      if (comparisonResultsRouter.isFinished()) {
        timer.finish();
        LOGGER.info("Finished lifecycle of test run: {}. Task finished in {} ms ({}).",
            indexedSuite.get().getCorrelationId(), timer.getExecutionTimeInMillis(),
            timer.getExecutionTimeInMMSS());
        LOGGER.info("Total tasks finished in steps: collect: {}; compare: {}.",
            collectionResultsRouter.getTotalTasksCount(),
            comparisonResultsRouter.getTotalTasksCount());
      } else if (suiteIsTimedOut()) {
        timer.finish();
        LOGGER.warn(
            "Lifecycle of run {} interrupted after {} ms ({}). Last message received: {} seconds ago... Trying to force report generation.",
            indexedSuite.get().getCorrelationId(), timer.getExecutionTimeInMillis(),
            timer.getExecutionTimeInMMSS(),
            TimeUnit.NANOSECONDS.toSeconds(timeoutWatch.getLastUpdateDifference()));
        forceFinishSuite();
        collectDispatcher.cancel(indexedSuite.get().getCorrelationId());
      }
    }
  }

  private boolean suiteIsTimedOut() {
    return timeoutWatch.isTimedOut(runnerConfiguration.getFt());
  }

  private void checkStatusUntilFinishedOrTimedOut() {
    String logMessage = "";
    while (!comparisonResultsRouter.isFinished() && !timeoutWatch
        .isTimedOut(runnerConfiguration.getFt())) {
      try {
        String currentLog = composeProgressLog();
        if (!currentLog.equals(logMessage)) {
          logMessage = currentLog;
          LOGGER.info("[{}]: {}", indexedSuite.get().getCorrelationId(), logMessage);
          messagesSender.sendMessage(new ProgressMessage(logMessage));
        }
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private String composeProgressLog() {
    ProgressLog compareLog = collectionResultsRouter.getProgress();
    ProgressLog reportLog = comparisonResultsRouter.getProgress();
    return StringUtils.join(Arrays.asList(compareLog, reportLog), " ::: ");
  }

  private synchronized boolean tryProcess() throws JMSException, AETException {
    boolean result = !processed;
    if (!processed) {
      collectDispatcher.process();
      processed = true;
    }
    return result;
  }

  private void forceFinishSuite() {
    messagesSender.sendMessage(new ProcessingErrorMessage(ProcessingError
        .reportingError("Report will be generated after timeout - some results might be missing!"),
        suite.getCorrelationId()));
    timeoutWatch.update();
    checkStatusUntilFinishedOrTimedOut();
    if (!comparisonResultsRouter.isFinished()) {
      messagesSender.sendMessage(new ProcessingErrorMessage(ProcessingError
          .reportingError(
              "Report will be generated after timeout - some results might be missing!"),
          suite.getCorrelationId()));
    }
  }

  private void save() throws ValidatorException, StorageException {
    LOGGER.debug("Persisting suite {}", suite);
    suiteDataService.saveSuite(indexedSuite.get());
    messagesSender.sendMessage(
        new FinishedSuiteProcessingMessage(FinishedSuiteProcessingMessage.Status.OK,
            suite.getCorrelationId()));
  }

  private void cleanup() {
    LOGGER.debug("Cleaning up suite {}", suite);
    comparisonResultsRouter.closeConnections();
    collectionResultsRouter.closeConnections();
    collectDispatcher.closeConnections();
    messagesSender.close();
  }
}
