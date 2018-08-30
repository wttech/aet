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
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.cognifide.aet.vs.StorageException;
import java.util.concurrent.Callable;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExecutionProcessorStrategy extends ProcessorStrategy {

  protected static final Logger LOGGER = LoggerFactory.getLogger(TestExecutionProcessorStrategy.class);

  public TestExecutionProcessorStrategy(Run objectToRun, Destination jmsReplyTo,
      SuiteDataService suiteDataService, RunnerConfiguration runnerConfiguration,
      SuiteExecutionFactory suiteExecutionFactory) {
    super(jmsReplyTo, suiteDataService, runnerConfiguration, suiteExecutionFactory);
    this.objectToRun = objectToRun;
  }

  void prepareSuiteWrapper() throws StorageException {
    LOGGER.debug("Fetching suite patterns {}", getObjectToRun());
    try {
      indexedSuite = new SuiteIndexWrapper(suiteDataService.enrichWithPatterns(getObjectToRun()));
    } catch (StorageException e) {
      e.printStackTrace();
    }
  }

  void save() throws ValidatorException, StorageException {
    LOGGER.debug("Persisting suite {}", getObjectToRun());
    try {
      suiteDataService.saveSuite(indexedSuite.get());
    } catch (ValidatorException | StorageException e) {
      e.printStackTrace();
    }
    messagesSender.sendMessage(
        new FinishedSuiteProcessingMessage(FinishedSuiteProcessingMessage.Status.OK,
            objectToRun.getCorrelationId()));
  }

  protected Test getObjectToRun(){
    return (Test) objectToRun.getObjectToRun();
  }
}
