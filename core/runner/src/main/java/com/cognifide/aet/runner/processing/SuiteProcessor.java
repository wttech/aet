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
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.messages.ProgressMessage;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.cognifide.aet.runner.processing.steps.CollectDispatcher;
import com.cognifide.aet.runner.processing.steps.CollectionResultsRouter;
import com.cognifide.aet.runner.processing.steps.ComparisonResultsRouter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the actual logic around runner suite processing.
 */
class SuiteProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteProcessor.class);

  private final ExecutionTimer timer;
  private boolean processed;
  private final TimeoutWatch timeoutWatch;
  private final CollectDispatcher collectDispatcher;
  private final CollectionResultsRouter collectionResultsRouter;
  private final ComparisonResultsRouter comparisonResultsRouter;
  private final SuiteIndexWrapper indexedSuite;
  private final RunnerConfiguration runnerConfiguration;
  private final MessagesSender messagesSender;

  SuiteProcessor(SuiteExecutionFactory suiteExecutionFactory,
      SuiteIndexWrapper indexedSuite, RunnerConfiguration runnerConfiguration,
      MessagesSender messagesSender) throws JMSException {
    this.indexedSuite = indexedSuite;
    this.runnerConfiguration = runnerConfiguration;
    this.messagesSender = messagesSender;
    this.timeoutWatch = new TimeoutWatch();
    timer = ExecutionTimer.createAndRun("RUNNER");
    collectDispatcher = suiteExecutionFactory.newCollectDispatcher(timeoutWatch, this.indexedSuite);
    collectionResultsRouter = suiteExecutionFactory
        .newCollectionResultsRouter(timeoutWatch, this.indexedSuite);
    comparisonResultsRouter = suiteExecutionFactory
        .newComparisonResultsRouter(timeoutWatch, this.indexedSuite);
    collectionResultsRouter.addObserver(messagesSender);
    comparisonResultsRouter.addObserver(messagesSender);
    collectionResultsRouter.addChangeObserver(comparisonResultsRouter);
  }

  void startProcessing() throws JMSException {
    timeoutWatch.update();
    if (tryProcess()) {
      checkStatusUntilFinishedOrTimedOut();
      if (comparisonResultsRouter.isFinished()) {
        timer.finish();
        LOGGER.info("Finished suite run: {}. Task finished in {} ms ({}).",
            indexedSuite.get().getCorrelationId(), timer.getExecutionTimeInMillis(),
            timer.getExecutionTimeInMMSS());
        LOGGER.info("Total tasks finished in steps: collect: {}; compare: {}.",
            collectionResultsRouter.getTotalTasksCount(),
            comparisonResultsRouter.getTotalTasksCount());
      } else if (suiteIsTimedOut()) {
        timer.finish();
        LOGGER.warn(
            "Running {} interrupted after {} ms ({}). Last message received: {} seconds ago... Trying to force report generation.",
            indexedSuite.get().getCorrelationId(), timer.getExecutionTimeInMillis(),
            timer.getExecutionTimeInMMSS(),
            TimeUnit.NANOSECONDS.toSeconds(timeoutWatch.getLastUpdateDifference()));
        forceFinishSuite();
      }
    }
  }

  void cleanup() {
    comparisonResultsRouter.closeConnections();
    collectionResultsRouter.closeConnections();
    collectDispatcher.closeConnections();
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

  private synchronized boolean tryProcess() throws JMSException {
    boolean result = !processed;
    if (!processed) {
      collectDispatcher.process();
      processed = true;
    }
    return result;
  }

  private void forceFinishSuite() {
    messagesSender.sendMessage(new ProcessingErrorMessage(ProcessingError
        .reportingError(
            "Report will be generated after timeout - some results might be missing!"),
        indexedSuite.get().getCorrelationId()));
    timeoutWatch.update();
    checkStatusUntilFinishedOrTimedOut();
    if (!comparisonResultsRouter.isFinished()) {
      comparisonResultsRouter.abort();
      messagesSender.sendMessage(new ProcessingErrorMessage(ProcessingError
          .reportingError(
              "Report will be generated after timeout - some results might be missing!"),
          indexedSuite.get().getCorrelationId()));
    }
    collectDispatcher.cancel(indexedSuite.get().getCorrelationId());
  }
}
