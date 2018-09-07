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
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.runner.processing.data.RunIndexWrappers.RunIndexWrapperFactory;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlExecutionProcessorStrategy extends ProcessorStrategy {

  protected static final Logger LOGGER = LoggerFactory
      .getLogger(TestExecutionProcessorStrategy.class);

  public UrlExecutionProcessorStrategy() {
    setLogger(LOGGER);
  }

  void prepareSuiteWrapper() throws StorageException {
    LOGGER.debug("Fetching suite patterns {}", getObjectToRun());
    try {
      Suite mergedSuite = suiteDataService.enrichWithPatterns(objectToRunWrapper.getRealSuite());
      objectToRunWrapper.setRealSuite(mergedSuite);

      Url url = (Url) objectToRunWrapper.getObjectToRun();
      String urlName = url.getName();
      String testName = objectToRunWrapper.getTestName();
      objectToRunWrapper.setObjectToRun(mergedSuite.getTest(testName).getUrl(urlName));
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
  protected Url getObjectToRun() {
    return (Url) objectToRunWrapper.getObjectToRun();
  }
}
