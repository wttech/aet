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
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.runner.processing.data.RunIndexWrappers.RunIndexWrapperFactory;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExecutionProcessorStrategy extends ProcessorStrategy {

  protected static final Logger LOGGER = LoggerFactory
      .getLogger(TestExecutionProcessorStrategy.class);

  public TestExecutionProcessorStrategy() {
    setLogger(LOGGER);
  }

  void prepareSuiteWrapper() throws StorageException {
    LOGGER.debug("Fetching suite patterns {}", getObjectToRun());
    try {
      Suite mergedSuite = suiteDataService.enrichWithPatterns(objectToRunWrapper.getRealSuite());
      objectToRunWrapper.setRealSuite(mergedSuite);
      Test test = (Test) objectToRunWrapper.getObjectToRun();
      String testName = test.getName();
      objectToRunWrapper.setObjectToRun(mergedSuite.getTest(testName));
      runIndexWrapper = RunIndexWrapperFactory.createInstance(objectToRunWrapper);
    } catch (StorageException e) {
      e.printStackTrace();
    }
  }

  void save() throws ValidatorException, StorageException {
    LOGGER.debug("Persisting suite {}", getObjectToRun());
    try {
      Suite oldSuite = suiteDataService.getSuite(new SimpleDBKey(objectToRunWrapper.getRealSuite()),
          objectToRunWrapper.getCorrelationId());
      suiteDataService.replaceSuite(oldSuite, objectToRunWrapper.getRealSuite());
    } catch (StorageException e) {
      e.printStackTrace();
    }
    messagesSender.sendMessage(
        new FinishedSuiteProcessingMessage(FinishedSuiteProcessingMessage.Status.OK,
            objectToRunWrapper.getCorrelationId()));
  }
  @Override
  protected Test getObjectToRun() {
    return (Test) objectToRunWrapper.getObjectToRun();
  }
}
