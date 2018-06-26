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

import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage;
import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage.Status;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.cognifide.aet.vs.StorageException;
import java.util.concurrent.Callable;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuiteExecutionTask implements Callable<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutionTask.class);

  private final Suite suite;
  private final Destination jmsReplyTo;
  private final SuiteDataService suiteDataService;
  private final RunnerConfiguration runnerConfiguration;
  private final SuiteExecutionFactory suiteExecutionFactory;

  private SuiteIndexWrapper indexedSuite;

  private MessagesSender messagesSender;
  private SuiteProcessor suiteProcessor;

  public SuiteExecutionTask(Suite suite, Destination jmsReplyTo,
      SuiteDataService suiteDataService, RunnerConfiguration runnerConfiguration,
      SuiteExecutionFactory suiteExecutionFactory) {
    this.suite = suite;
    this.jmsReplyTo = jmsReplyTo;
    this.suiteDataService = suiteDataService;
    this.runnerConfiguration = runnerConfiguration;
    this.suiteExecutionFactory = suiteExecutionFactory;
  }

  @Override
  public String call() {
    try {
      prepareSuiteWrapper();
      init();
      process();
      save();
    } catch (StorageException | JMSException | ValidatorException e) {
      LOGGER.error("Error during processing suite {}", suite, e);
      FinishedSuiteProcessingMessage message = new FinishedSuiteProcessingMessage(Status.FAILED,
          suite.getCorrelationId());
      message.addError(e.getMessage());
      messagesSender.sendMessage(message);
    } finally {
      cleanup();
    }
    return suite.getCorrelationId();
  }

  private void prepareSuiteWrapper() throws StorageException {
    LOGGER.debug("Fetching suite patterns {}", suite);
    indexedSuite = new SuiteIndexWrapper(suiteDataService.enrichWithPatterns(suite));
  }

  private void init() throws JMSException {
    LOGGER.debug("Initializing suite processors {}", suite);
    messagesSender = suiteExecutionFactory.newMessagesSender(jmsReplyTo);
    suiteProcessor = new SuiteProcessor(suiteExecutionFactory, indexedSuite, runnerConfiguration,
        messagesSender);
  }

  private void process() throws JMSException {
    LOGGER.info("Start processing: {}", indexedSuite.get());
    suiteProcessor.startProcessing();
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
    messagesSender.close();
    suiteProcessor.cleanup();
  }

}
