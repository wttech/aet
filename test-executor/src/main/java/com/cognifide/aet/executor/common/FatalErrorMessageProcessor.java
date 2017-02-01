/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.executor.common;

import com.cognifide.aet.communication.api.messages.FatalErrorMessage;
import com.cognifide.aet.executor.ProcessingStatus;
import com.cognifide.aet.executor.SuiteStatusResult;


class FatalErrorMessageProcessor implements MessageProcessor {

  private final FatalErrorMessage errorMessage;

  private final ConsumerRemover consumerRemover;

  FatalErrorMessageProcessor(FatalErrorMessage errorMessage, ConsumerRemover consumerRemover) {
    this.errorMessage = errorMessage;
    this.consumerRemover = consumerRemover;
  }

  @Override
  public SuiteStatusResult process() {
    consumerRemover.remove();
    return new SuiteStatusResult(ProcessingStatus.FATAL_ERROR, errorMessage.getMessage());
  }
}
