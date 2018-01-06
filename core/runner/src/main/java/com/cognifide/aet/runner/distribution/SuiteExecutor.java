/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.runner.distribution;

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.ProgressMessage;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import com.cognifide.aet.runner.distribution.dispatch.CollectDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.CollectionResultsRouter;
import com.cognifide.aet.runner.distribution.dispatch.ComparisonResultsRouter;
import com.cognifide.aet.runner.distribution.dispatch.MetadataPersister;
import com.cognifide.aet.runner.distribution.progress.ProgressLog;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SuiteExecutor - executes suite, execution consists of three steps: preparation, validation and
 * processing
 */
class SuiteExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutor.class);

  private static final String SYSTEM_IN_MAINTENANCE_MESSAGE = "The AET System is currently down for maintenance. We’ll be back soon!";

  private static final String SYSTEM_RUNNING_NORMALLY_MESSAGE = "Maintenance mode is switched off. Please run the test in normal mode.";

  private static final String RUNNING_TASK_IN_MAINTENANCE_MODE = "Running task in maintenance mode!";

  private final TimeoutWatch timeoutWatch;

  private final SuiteIndexWrapper suite;

  private final CollectDispatcher collectDispatcher;

  private final CollectionResultsRouter collectionResultsRouter;

  private final ComparisonResultsRouter comparisonResultsRouter;

  private final SuiteAgent suiteAgent;

  private final ProgressMessageObserver progressMessageObserver;

  private final Long runTimeoutInSeconds;

  private final SuiteExecutionSettings suiteExecutionSettings;

  private final MetadataPersister metadataPersister;

  private boolean processed;

  @Inject
  SuiteExecutor(SuiteExecutionSettings suiteExecutionSettings, TimeoutWatch timeoutWatch,
                SuiteIndexWrapper suite, CollectDispatcher collectDispatcher,
                CollectionResultsRouter collectionResultsRouter, ComparisonResultsRouter comparisonResultsRouter,
                SuiteAgent suiteAgent, ProgressMessageObserver progressMessageObserver,
                MetadataPersister metadataPersister, @Named("failureTimeout") long runTimeoutInSeconds) {
    this.suiteExecutionSettings = suiteExecutionSettings;
    this.timeoutWatch = timeoutWatch;
    this.suite = suite;
    this.collectDispatcher = collectDispatcher;
    this.collectionResultsRouter = collectionResultsRouter;
    this.comparisonResultsRouter = comparisonResultsRouter;
    this.suiteAgent = suiteAgent;
    this.progressMessageObserver = progressMessageObserver;
    this.metadataPersister = metadataPersister;
    this.runTimeoutInSeconds = runTimeoutInSeconds;

    suiteAgent.addProcessingErrorMessagesObservable(collectionResultsRouter);
    suiteAgent.addProcessingErrorMessagesObservable(comparisonResultsRouter);
    collectionResultsRouter.addChangeObserver(comparisonResultsRouter);
    comparisonResultsRouter.setMetadataPersister(metadataPersister);
    metadataPersister.addObserver(suiteAgent);
  }

  void execute() {
    if (suiteExecutionSettings.shouldExecute()) {
      executeSuite();
    } else {
      finishWithoutExecution();
    }
  }

  void cleanup() {
    comparisonResultsRouter.closeConnections();
    collectionResultsRouter.closeConnections();
    collectDispatcher.closeConnections();
    suiteAgent.close();
  }

  /**
   * Immediately aborts all AET tasks for current life cycle.
   */
  synchronized void abort() {
    if (processed) {
      if (!comparisonResultsRouter.isFinished()) {
        LOGGER.warn("Consumer removed - aborting TestLifeCycle for task: {}",
                suite.get().getCorrelationId());
      }
      comparisonResultsRouter.abort();
      collectDispatcher.cancel(suite.get().getCorrelationId());
    } else {
      processed = true;
      LOGGER.warn("Consumer removed - aborting TestLifeCycle for task: {} before processing",
              suite.get().getCorrelationId());
    }
  }

  private void executeSuite() {
    if (suiteExecutionSettings.isMaintenanceMessage()) {
      LOGGER.info(RUNNING_TASK_IN_MAINTENANCE_MODE);
      progressMessageObserver.update(null, new ProgressMessage(RUNNING_TASK_IN_MAINTENANCE_MODE));
    }
    try {
      ExecutionTimer timer = ExecutionTimer.createAndRun("RUNNER");
      LOGGER.info("Start lifecycle of test suite run: {}", suite.get());
      timeoutWatch.update();
      process(timer);
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

  private void process(ExecutionTimer timer)
          throws JMSException, AETException {
    if (tryProcess()) {
      checkStatusUntilFinishedOrTimedOut();
      if (comparisonResultsRouter.isFinished()) {
        timer.finish();
        LOGGER.info("Finished lifecycle of test run: {}. Task finished in {} ms ({}).",
                suite.get().getCorrelationId(), timer.getExecutionTimeInMillis(),
                timer.getExecutionTimeInMMSS());
        LOGGER.info("Total tasks finished in steps: collect: {}; compare: {}.",
                collectionResultsRouter.getTotalTasksCount(),
                comparisonResultsRouter.getTotalTasksCount());
      } else if (suiteIsTimedOut()) {
        timer.finish();
        LOGGER.warn(
                "Lifecycle of run {} interrupted after {} ms ({}). Last message received: {} seconds ago... Trying to force report generation.",
                suite.get().getCorrelationId(), timer.getExecutionTimeInMillis(),
                timer.getExecutionTimeInMMSS(),
                TimeUnit.NANOSECONDS.toSeconds(timeoutWatch.getLastUpdateDifference()));
        forceFinishSuite();
        collectDispatcher.cancel(suite.get().getCorrelationId());
      }
    }
  }

  private boolean suiteIsTimedOut() {
    return timeoutWatch.isTimedOut(runTimeoutInSeconds);
  }

  private void checkStatusUntilFinishedOrTimedOut() {
    String logMessage = "";
    while (!comparisonResultsRouter.isFinished() && !timeoutWatch.isTimedOut(runTimeoutInSeconds)) {
      try {
        String currentLog = composeProgressLog();
        if (!currentLog.equals(logMessage)) {
          logMessage = currentLog;
          LOGGER.info("[{}]: {}", suite.get().getCorrelationId(), logMessage);
          progressMessageObserver.update(null, new ProgressMessage(logMessage));
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
    suiteAgent.onError(ProcessingError
            .reportingError("Report will be generated after timeout - some results might be missing!"));
    suiteAgent.enforceSuiteFinish();
    timeoutWatch.update();
    metadataPersister.persistMetadata();
    checkStatusUntilFinishedOrTimedOut();
    if (!comparisonResultsRouter.isFinished()) {
      suiteAgent.sendFailMessage(Collections
              .singletonList("Failed to generate reports because it took too much time!"));
    }
  }

}
