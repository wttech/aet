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

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.execution.SuiteStatusResult;
import com.jcabi.log.Logger;

@Deprecated
public class UnexpectedStatusProcessor implements StatusProcessor {

  private final SuiteStatusResult suiteStatusResult;

  UnexpectedStatusProcessor(SuiteStatusResult suiteStatusResult) {
    this.suiteStatusResult = suiteStatusResult;
  }

  @Override
  public void process() throws AETException {
    Logger.warn(this, "Received unexpected status: %s. Message: %s.",
        suiteStatusResult.getStatus(), suiteStatusResult.getMessage());
  }
}
