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
package com.cognifide.aet.runner.processing.steps;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.job.ComparatorResultData;
import com.cognifide.aet.communication.api.metadata.Statistics;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Suite.Timestamp;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.runner.MessagesManager;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.TimeoutWatch;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.google.common.base.Optional;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComparisonResultsRouter extends StepManager implements ChangeObserver,
    TaskFinishPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(ComparisonResultsRouter.class);

  private static final String STEP_NAME = "COMPARED";

  private final SuiteIndexWrapper suite;

  private final ExecutionTimer timer;

  private boolean collectingFinished;

  private boolean aborted;

  public ComparisonResultsRouter(TimeoutWatch timeoutWatch, JmsConnection jmsConnection,
      RunnerConfiguration runnerConfiguration, SuiteIndexWrapper suite) throws JMSException {
    super(timeoutWatch, jmsConnection, suite.get().getCorrelationId(),
        runnerConfiguration.getMttl());
    this.suite = suite;
    timer = ExecutionTimer.createAndRun("comparison");
  }

  @Override
  public void updateAmountToReceive(int additionalCount) {
    messagesToReceive.addAndGet(additionalCount);
  }

  @Override
  public void informChangesCompleted() {
    collectingFinished = true;
    persistMetadataIfFinished();
  }

  @Override
  public void onMessage(Message message) {
    if (message instanceof ObjectMessage) {
      timeoutWatch.update();
      try {
        ComparatorResultData comparatorResultData = (ComparatorResultData) ((ObjectMessage) message)
            .getObject();

        updateCounters(comparatorResultData.getStatus());
        LOGGER.info("Compare result message (ID: {}) {}! Results received successful {}, " +
                "failed {} of {} total. CorrelationId: {}",
            message.getJMSMessageID(), comparatorResultData, messagesReceivedSuccess.get(),
            messagesReceivedFailed.get(), getTotalTasksCount(), correlationId);

        addComparatorToSuite(comparatorResultData);
        if (comparatorResultData.getStatus() != JobStatus.SUCCESS) {
          onError(comparatorResultData.getProcessingError());
        }
      } catch (JMSException e) {
        LOGGER.error("Error while collecting results in CollectionResultsRouter. CorrelationId: {}",
            correlationId, e);
        onError(ProcessingError.comparingError(e.getMessage()));
      } finally {
        persistMetadataIfFinished();
      }
    }
  }

  private void persistMetadataIfFinished() {
    if (allResultsReceived()) {
      LOGGER.info("All results received ({})! Persisting metadata. CorrelationId: {}",
          messagesToReceive.get(), correlationId);
      final Suite currentSuite = this.suite.get();
      timer.finishAndLog(currentSuite.getName());
      currentSuite.setFinishedTimestamp(new Timestamp(System.currentTimeMillis()));
      long delta = currentSuite.getFinishedTimestamp().get() - currentSuite.getRunTimestamp().get();
      currentSuite.setStatistics(new Statistics(delta));
    }
  }

  private void addComparatorToSuite(ComparatorResultData comparisonResult) {
    final Optional<Url> urlOptional = suite
        .getTestUrl(comparisonResult.getTestName(), comparisonResult.getUrlName());
    if (urlOptional.isPresent()) {
      final Url url = urlOptional.get();
      final Step step = url.getSteps().get(comparisonResult.getStepIndex());
      if (step != null) {
        step.addComparator(comparisonResult.getComparisonResult());
      } else {
        LOGGER.error("Fatal error while saving comparison result: {} of suite.", comparisonResult,
            suite.get().getCorrelationId());
      }
    }
  }

  private boolean allResultsReceived() {
    return collectingFinished
        && messagesToReceive.get() == messagesReceivedSuccess.get() + messagesReceivedFailed.get();
  }

  /**
   * This task is finished, when all comparisons are received or was aborted
   */
  @Override
  public boolean isFinished() {
    return aborted || allResultsReceived();
  }

  public void abort() {
    if (!isFinished()) {
      LOGGER.warn("Suite {} aborted!. Still waiting for {} of {} comparisons!",
          suite.get().getCorrelationId(),
          messagesToReceive.get() - messagesReceivedSuccess.get() - messagesReceivedFailed.get(),
          messagesToReceive.get());
    }
    aborted = true;
  }

  @Override
  protected String getQueueInName() {
    return MessagesManager.createFullQueueName("comparatorResults");
  }

  @Override
  protected String getQueueOutName() {
    return null;
  }

  @Override
  protected String getStepName() {
    return STEP_NAME;
  }

}
