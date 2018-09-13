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
import com.jcabi.log.Logger;

@Deprecated
class SuiteFinishedProcessor implements StatusProcessor {

  private final RunnerTerminator runnerTerminator;

  private final String reportUrl;

  private final RedirectWriter redirectWriter;

  public SuiteFinishedProcessor(String reportUrl, RedirectWriter redirectWriter,
      RunnerTerminator runnerTerminator) {
    this.reportUrl = reportUrl;
    this.redirectWriter = redirectWriter;
    this.runnerTerminator = runnerTerminator;
  }

  @Override
  public void process() throws AETException {
    Logger.info(this, "Report is available at " + reportUrl);
    redirectWriter.write(reportUrl);
    runnerTerminator.finish();
  }
}
