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
package com.cognifide.aet.common;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.jcabi.log.Logger;

public class ProcessingErrorMessageProcessor implements MessageProcessor {

  private final ProcessingErrorMessage data;

  public ProcessingErrorMessageProcessor(ProcessingErrorMessage data) {
    this.data = data;
  }

  @Override
  public void process() throws AETException {
    if (data != null && data.getProcessingError() != null) {
      Logger.warn(this, data.getProcessingError().getDescription());
    } else {
      throw new AETException("ProcessingErrorMessageProcessor has received empty message!");
    }
  }
}
