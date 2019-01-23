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
import com.cognifide.aet.runner.processing.data.wrappers.RunIndexWrapper;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.cognifide.aet.vs.StorageException;
import java.util.concurrent.Callable;
import javax.jms.JMSException;
import org.slf4j.Logger;
import javax.jms.Destination;

public abstract class ProcessorStrategy<T> implements Callable<String> {

  protected Logger internalLogger;
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
      internalLogger.error("Error during processing suite {}", getObjectToRun(), e);
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
    internalLogger.debug("Initializing suite processors {}", getObjectToRun());
    messagesSender = suiteExecutionFactory.newMessagesSender(jmsReplyTo);
    if (suiteProcessor == null) {
      suiteProcessor = new SuiteProcessor(suiteExecutionFactory, runIndexWrapper, runnerConfiguration,
          messagesSender);
    }
  }

  protected void process() throws JMSException {
    internalLogger.info("Start processing: {}", runIndexWrapper);
    suiteProcessor.startProcessing();
  }

  protected void cleanup() {
    internalLogger.debug("Cleaning up {}", runIndexWrapper);
    if(messagesSender != null){
      messagesSender.close();
    }
    if(suiteProcessor != null){
      suiteProcessor.cleanup();
    }
  }

  //for unit tests
  public void setSuiteProcessor(SuiteProcessor suiteProcessor) {
    this.suiteProcessor = suiteProcessor;
  }

  protected void setLogger(Logger logger) {
    this.internalLogger = logger;
  }

  protected abstract T getObjectToRun();

  abstract void prepareSuiteWrapper() throws StorageException;

  abstract void save() throws ValidatorException, StorageException;

}
