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
package com.cognifide.aet.common;

import com.cognifide.aet.communication.api.execution.SuiteStatusResult;

@Deprecated
final class ProcessorFactory {

  private ProcessorFactory() {
    // private
  }

  static StatusProcessor produce(SuiteStatusResult suiteStatusResult, String reportUrl,
      RedirectWriter redirectWriter, RunnerTerminator runnerTerminator) {
    StatusProcessor processor = null;
    if (suiteStatusResult != null) {
      switch (suiteStatusResult.getStatus()) {
        case PROGRESS:
          processor = new ProgressStatusProcessor(suiteStatusResult);
          break;
        case ERROR:
          processor = new ProcessingErrorStatusProcessor(suiteStatusResult);
          break;
        case FATAL_ERROR:
          processor = new FatalErrorStatusProcessor(suiteStatusResult);
          break;
        case FINISHED:
          processor = new SuiteFinishedProcessor(reportUrl, redirectWriter, runnerTerminator);
          break;
        case UNKNOWN:
          // There is no need to handle this message.
          break;
        default:
          processor = new UnexpectedStatusProcessor(suiteStatusResult);
          break;
      }
    }
    return processor;
  }

}
