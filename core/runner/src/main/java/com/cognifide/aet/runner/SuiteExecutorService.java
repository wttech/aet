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
package com.cognifide.aet.runner;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.runner.processing.SuiteExecutionFactory;
import com.cognifide.aet.runner.processing.SuiteExecutionTask;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jms.Destination;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = SuiteExecutorService.class, immediate = true)
public class SuiteExecutorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutorService.class);

  private ListeningExecutorService executor;

  private ExecutorService callbackExecutor;

  private Set<String> scheduledSuites;

  @Reference
  private RunnerConfiguration runnerConfiguration;

  @Reference
  private SuiteDataService suiteDataService;

  @Reference
  private SuiteExecutionFactory suiteExecutionFactory;

  @Activate
  public void activate() {
    LOGGER.debug("Activating SuiteExecutorService");
    executor = MoreExecutors.listeningDecorator(
        Executors.newFixedThreadPool(runnerConfiguration.getMaxConcurrentSuitesCount()));
    callbackExecutor = Executors.newSingleThreadExecutor();
    scheduledSuites = Sets.newConcurrentHashSet();
  }

  @Deactivate
  public void deactivate() {
    LOGGER.debug("Deactivating SuiteExecutorService");
    if (executor != null) {
      executor.shutdown();
    }
    if (callbackExecutor != null) {
      callbackExecutor.shutdown();
    }
    scheduledSuites.clear();
  }

  void scheduleSuite(Suite suite, Destination jmsReplyTo) {
    LOGGER.debug("Scheduling {}!", suite);
    final ListenableFuture<String> suiteExecutionTask = executor
        .submit(new SuiteExecutionTask(suite, jmsReplyTo, suiteDataService, runnerConfiguration,
            suiteExecutionFactory));
    scheduledSuites.add(suite.getCorrelationId());
    Futures.addCallback(suiteExecutionTask, new SuiteFinishedCallback(suite.getCorrelationId()),
        callbackExecutor);
    LOGGER.debug(
        "Currently {} suites are scheduled in the system (max number of concurrent suites: {})",
        scheduledSuites.size(), runnerConfiguration.getMaxConcurrentSuitesCount());
  }

  private class SuiteFinishedCallback implements FutureCallback<String> {

    private final String correlationId;

    SuiteFinishedCallback(String correlationId) {
      this.correlationId = correlationId;
    }

    @Override
    public void onSuccess(String result) {
      scheduledSuites.remove(result);
      LOGGER.info("Suite {} processing finished successfully! (Total {}/{} scheduled)",
          result, scheduledSuites.size(), runnerConfiguration.getMaxConcurrentSuitesCount());
    }

    @Override
    public void onFailure(Throwable t) {
      scheduledSuites.remove(correlationId);
      LOGGER.error("Suite {} processing finished successfully! (Total {}/{} scheduled)",
          correlationId, scheduledSuites.size(), runnerConfiguration.getMaxConcurrentSuitesCount(),
          t);
    }
  }
}
