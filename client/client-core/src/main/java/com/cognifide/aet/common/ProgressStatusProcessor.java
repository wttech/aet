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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Deprecated
class ProgressStatusProcessor implements StatusProcessor {

  private static final String DATE_FORMAT = "HH:mm:ss.SSS";

  private static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>() {
    @Override
    protected DateFormat initialValue() {
      return new SimpleDateFormat(DATE_FORMAT);
    }
  };

  private final SuiteStatusResult suiteStatusResult;

  ProgressStatusProcessor(SuiteStatusResult suiteStatusResult) {
    this.suiteStatusResult = suiteStatusResult;
  }

  @Override
  public void process() throws AETException {
    Logger.info(this, "[%s]: %s", DATE_FORMATTER.get().format(new Date()),
        suiteStatusResult.getMessage());
  }
}
