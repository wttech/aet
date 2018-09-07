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
package com.cognifide.aet.runner.processing.processors;

import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage;
import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage.Status;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.MessagesSender;
import com.cognifide.aet.runner.processing.SuiteExecutionFactory;
import com.cognifide.aet.runner.processing.SuiteProcessor;
import com.cognifide.aet.runner.processing.data.RunIndexWrappers.RunIndexWrapper;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.cognifide.aet.vs.StorageException;
import java.util.concurrent.Callable;
import javax.jms.JMSException;
import org.slf4j.Logger;
import javax.jms.Destination;

abstract public class ProcessorStrategy<T> implements Callable<String> {

  protected Logger LOGGER;

  protected Destination jmsReplyTo;
  protected SuiteDataService suiteDataService;
  protected RunnerConfiguration runnerConfiguration;
  protected SuiteExecutionFactory suiteExecutionFactory;
  protected RunIndexWrapper runIndexWrapper;
  protected MessagesSender messagesSender;

  protected SuiteProcessor suiteProcessor;
  protected Run objectToRunWrapper;

  @Override
  public String call() {
    try {
      prepareSuiteWrapper();
      init();
      process();
      save();
    } catch (StorageException | JMSException | ValidatorException e) {
      LOGGER.error("Error during processing suite {}", getObjectToRun(), e);
      FinishedSuiteProcessingMessage message = new FinishedSuiteProcessingMessage(Status.FAILED,
          objectToRunWrapper.getCorrelationId());
      message.addError(e.getMessage());
      messagesSender.sendMessage(message);
    } finally {
      cleanup();
    }
    return objectToRunWrapper.getCorrelationId();
  }

  public void setParameters(Run objectToRunWrapper, Destination jmsReplyTo,
      SuiteDataService suiteDataService, RunnerConfiguration runnerConfiguration,
      SuiteExecutionFactory suiteExecutionFactory) {
    this.objectToRunWrapper = objectToRunWrapper;
    this.jmsReplyTo = jmsReplyTo;
    this.suiteDataService = suiteDataService;
    this.runnerConfiguration = runnerConfiguration;
    this.suiteExecutionFactory = suiteExecutionFactory;
  }

  protected void init() throws JMSException {
    LOGGER.debug("Initializing suite processors {}", getObjectToRun());
    messagesSender = suiteExecutionFactory.newMessagesSender(jmsReplyTo);
    suiteProcessor = new SuiteProcessor(suiteExecutionFactory, runIndexWrapper, runnerConfiguration,
        messagesSender);
  }

  protected void process() throws JMSException {
    LOGGER.info("Start processing: {}", runIndexWrapper.get());
    suiteProcessor.startProcessing();
  }

  protected void cleanup() {
    LOGGER.debug("Cleaning up suite {}", runIndexWrapper.get());
    messagesSender.close();
    suiteProcessor.cleanup();
  }

  protected void setLogger(Logger LOGGER) {
    this.LOGGER = LOGGER;
  }

  protected abstract T getObjectToRun();

  abstract void prepareSuiteWrapper() throws StorageException;

  abstract void save() throws ValidatorException, StorageException;

}
