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
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;

class ProcessingErrorMessageProcessor implements MessageProcessor {

  private final ProcessingErrorMessage data;

  private final RunnerTerminator runnerTerminator;

  ProcessingErrorMessageProcessor(ProcessingErrorMessage data, RunnerTerminator runnerTerminator) {
    this.data = data;
    this.runnerTerminator = runnerTerminator;
  }

  @Override
  public SuiteStatusResult process() {
    SuiteStatusResult result;
    if (data != null && data.getProcessingError() != null) {
      result = new SuiteStatusResult(ProcessingStatus.ERROR,
          data.getProcessingError().getDescription());
    } else {
      result = new SuiteStatusResult(ProcessingStatus.FATAL_ERROR, "Empty error message received");
      runnerTerminator.finish();
    }
    return result;
  }
}
