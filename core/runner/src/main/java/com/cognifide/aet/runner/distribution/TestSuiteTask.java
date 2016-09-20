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
import com.google.inject.Key;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import com.cognifide.aet.runner.testsuitescope.TestSuiteScope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.jms.Destination;

public class TestSuiteTask implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestSuiteTask.class);

  private final Provider<TestLifeCycle> testLifeCycleProvider;

  private final TestSuiteScope scope;

  private final SuiteIndexWrapper suite;

  private final Destination resultsDestination;

  private final SuiteExecutionSettings suiteExecutionSettings;

  @Inject
  public TestSuiteTask(Provider<TestLifeCycle> testLifeCycleProvider,
                       @Named("testSuiteScope") TestSuiteScope scope, RunnerMode runnerMode,
                       @Assisted SuiteIndexWrapper suite, @Assisted Destination resultsDestination,
                       @Assisted boolean maintenanceMessage) {
    this.testLifeCycleProvider = testLifeCycleProvider;
    this.scope = scope;
    this.suite = suite;
    this.resultsDestination = resultsDestination;
    this.suiteExecutionSettings = new SuiteExecutionSettings(runnerMode, maintenanceMessage);
  }

  @Override
  public void run() {
    scope.enter();
    try {
      scope.seed(Key.get(Destination.class), resultsDestination);
      scope.seed(Key.get(SuiteIndexWrapper.class), suite);
      scope.seed(Key.get(SuiteExecutionSettings.class), suiteExecutionSettings);

      TestLifeCycle testLifeCycle = testLifeCycleProvider.get();
      Thread thread = new Thread(testLifeCycle);
      thread.start();
    } catch (Exception e) {
      LOGGER.error("Error while starting TestLifeCycle for: {}.", suite.get().getCorrelationId(), e);
    } finally {
      scope.exit();
    }
  }

}
