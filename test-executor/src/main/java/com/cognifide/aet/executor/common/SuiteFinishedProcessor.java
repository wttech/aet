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
package com.cognifide.aet.executor.common;

import com.cognifide.aet.communication.api.execution.ProcessingStatus;
import com.cognifide.aet.communication.api.execution.SuiteStatusResult;
import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SuiteFinishedProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteFinishedProcessor.class);

  private final FinishedSuiteProcessingMessage data;

  private final RunnerTerminator runnerTerminator;

  SuiteFinishedProcessor(FinishedSuiteProcessingMessage data, RunnerTerminator runnerTerminator) {
    this.data = data;
    this.runnerTerminator = runnerTerminator;
  }

  @Override
  public SuiteStatusResult process() {
    LOGGER.info("Received report message: {}", data);
    SuiteStatusResult result = null;
    if (data.getStatus() == FinishedSuiteProcessingMessage.Status.OK) {
      result = processSuccess();
    } else if (data.getStatus() == FinishedSuiteProcessingMessage.Status.FAILED) {
      result = processError();
    }
    runnerTerminator.finish();
    return result;
  }

  private SuiteStatusResult processSuccess() {
    return new SuiteStatusResult(ProcessingStatus.FINISHED, "Suite processing finished");
  }

  private SuiteStatusResult processError() {
    StringBuilder errorBuilder = new StringBuilder();
    for (String error : data.getErrors()) {
      errorBuilder.append(error).append(System.lineSeparator());
    }
    return new SuiteStatusResult(ProcessingStatus.FATAL_ERROR, errorBuilder.toString());
  }

}
