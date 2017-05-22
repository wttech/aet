/**
 * Automated Exploratory Tests
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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import com.cognifide.aet.runner.conversion.SuiteMergeStrategy;
import com.cognifide.aet.runner.distribution.dispatch.CollectorJobScheduler;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;

/**
 * The main dispatcher, gets messages from Web application and distributes it among slave-dispatcher depending
 * on available test steps
 */
@Singleton
public class TestRunProcessor {

  private static final int MAX_POOL_SIZE = 20;

  private final ScheduledExecutorService executor;

  private final TestSuiteTaskFactory testSuiteTaskFactory;

  private final MetadataDAO metadataDAO;

  private final CollectorJobScheduler collectorJobScheduler;

  private final Future<?> collectorJobSchedulerFeature;

  @Inject
  public TestRunProcessor(TestSuiteTaskFactory testSuiteTaskFactory, CollectorJobScheduler collectorJobScheduler, MetadataDAO metadataDAO) {
    this.collectorJobScheduler = collectorJobScheduler;
    this.testSuiteTaskFactory = testSuiteTaskFactory;
    this.metadataDAO = metadataDAO;
    this.executor = Executors.newScheduledThreadPool(MAX_POOL_SIZE);

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    collectorJobSchedulerFeature = executorService.submit(collectorJobScheduler);
  }

  /**
   * Start processing given test suite. All processing results will be sent to the given destination.
   *
   * @param currentRun - test suite to process.
   * @param resultsDestination - processing results destination.
   * @param isMaintenanceMessage - flag that says if this message was sent to maintenance queue
   */
  void process(final Suite currentRun, Destination resultsDestination, boolean isMaintenanceMessage) throws StorageException {
    final SimpleDBKey dbKey = new SimpleDBKey(currentRun);
    Suite lastVersion = metadataDAO.getLatestRun(dbKey, currentRun.getName());
    Suite pattern;
    if (currentRun.getPatternCorrelationId() != null) {
      pattern = metadataDAO.getSuite(dbKey, currentRun.getPatternCorrelationId());
    } else {
      pattern = lastVersion;
    }
    final Suite suite = SuiteMergeStrategy.merge(currentRun, lastVersion, pattern);

    TestSuiteTask testSuitTask = testSuiteTaskFactory.create(new SuiteIndexWrapper(suite), resultsDestination, isMaintenanceMessage);
    executor.submit(testSuitTask);
  }

  /**
   *
   * @param force if set to true all executed tasks will be forced to quit, otherwise they will continue
   * until they end or timeout comes, whichever happens first.
   *
   * @throws InterruptedException if interrupted while waiting for tasks to end
   */
  public void quit(boolean force) throws InterruptedException {
    collectorJobScheduler.quit();
    collectorJobSchedulerFeature.cancel(force);
    if (!force) {
      executor.awaitTermination(30, TimeUnit.SECONDS);
    } else {
      executor.shutdown();
    }
  }

  void cancel(String correlationID) {
    collectorJobScheduler.cleanup(correlationID);
  }
}
