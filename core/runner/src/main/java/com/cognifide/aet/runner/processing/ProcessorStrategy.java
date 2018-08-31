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
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.cognifide.aet.runner.processing.data.RunIndexWrapper;
import com.cognifide.aet.vs.StorageException;
import java.util.concurrent.Callable;
import javax.jms.JMSException;
import org.slf4j.Logger;
import javax.jms.Destination;

abstract class ProcessorStrategy<T> implements Callable<String> {

  protected static Logger LOGGER;
  protected final Destination jmsReplyTo;
  protected final SuiteDataService suiteDataService;
  protected final RunnerConfiguration runnerConfiguration;
  protected final SuiteExecutionFactory suiteExecutionFactory;

  protected RunIndexWrapper indexedSuite;

  protected MessagesSender messagesSender;
  protected SuiteProcessor suiteProcessor;

  protected Run objectToRunWrapper;

  public ProcessorStrategy(Destination jmsReplyTo,
      SuiteDataService suiteDataService, RunnerConfiguration runnerConfiguration,
      SuiteExecutionFactory suiteExecutionFactory, Logger LOGGER) {
    this.jmsReplyTo = jmsReplyTo;
    this.suiteDataService = suiteDataService;
    this.runnerConfiguration = runnerConfiguration;
    this.suiteExecutionFactory = suiteExecutionFactory;
    this.LOGGER = LOGGER;
  }

  @Override
  public String call() {
    try {
      prepareSuiteWrapper();
      init();
      process();
      save();
    } catch (StorageException | JMSException | ValidatorException e) {
      LOGGER.error("Error during processing suite {}", getObjectToRunWrapper(), e);
      FinishedSuiteProcessingMessage message = new FinishedSuiteProcessingMessage(Status.FAILED,
          objectToRunWrapper.getCorrelationId());
      message.addError(e.getMessage());
      messagesSender.sendMessage(message);
    } finally {
      cleanup();
    }
    return objectToRunWrapper.getCorrelationId();
  }

  protected void init() throws JMSException {
    LOGGER.debug("Initializing suite processors {}", getObjectToRunWrapper());
    messagesSender = suiteExecutionFactory.newMessagesSender(jmsReplyTo);
    suiteProcessor = new SuiteProcessor(suiteExecutionFactory, indexedSuite, runnerConfiguration,
        messagesSender);
  }

  protected void process() throws JMSException {
    LOGGER.info("Start processing: {}", indexedSuite.get());
    suiteProcessor.startProcessing();
  }

  protected void cleanup() {
    LOGGER.debug("Cleaning up suite {}", getObjectToRunWrapper());
    messagesSender.close();
    suiteProcessor.cleanup();
  }

  protected abstract T getObjectToRunWrapper();

  abstract void prepareSuiteWrapper() throws StorageException;

  abstract void save() throws ValidatorException, StorageException;

}
