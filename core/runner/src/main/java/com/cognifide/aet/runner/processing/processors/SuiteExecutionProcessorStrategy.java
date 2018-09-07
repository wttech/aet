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
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.wrappers.SuiteRunWrapper;
import com.cognifide.aet.runner.processing.data.RunIndexWrappers.RunIndexWrapperFactory;
import com.cognifide.aet.vs.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuiteExecutionProcessorStrategy extends ProcessorStrategy {

  protected static final Logger LOGGER = LoggerFactory
      .getLogger(SuiteExecutionProcessorStrategy.class);

  public SuiteExecutionProcessorStrategy() {
    setLogger(LOGGER);
  }

  void prepareSuiteWrapper() {
    LOGGER.debug("Fetching suite patterns {}", getObjectToRun());
    try {
      Suite realSuite = objectToRunWrapper.getRealSuite();
      Suite mergedSuite = suiteDataService.enrichWithPatterns(realSuite);
      SuiteRunWrapper suiteRunWrapper = new SuiteRunWrapper(mergedSuite);
      runIndexWrapper = RunIndexWrapperFactory.createInstance(suiteRunWrapper);
    } catch (StorageException e) {
      e.printStackTrace();
    }
  }

  void save() {
    LOGGER.debug("Persisting suite {}", getObjectToRun());
    try {
      suiteDataService.saveSuite(runIndexWrapper.get().getRealSuite());
    } catch (ValidatorException | StorageException e) {
      e.printStackTrace();
    }
    messagesSender.sendMessage(
        new FinishedSuiteProcessingMessage(FinishedSuiteProcessingMessage.Status.OK,
            objectToRunWrapper.getCorrelationId()));
  }

  @Override
  protected Suite getObjectToRun() {
    return (Suite) objectToRunWrapper.getObjectToRun();
  }

}
