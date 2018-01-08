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
package com.cognifide.aet.job.common.modifiers.sleep;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepModifier implements CollectorJob {

  public static final String NAME = "sleep";

  private static final Logger LOG = LoggerFactory.getLogger(SleepModifier.class);

  private static final String DURATION_PARAM = "duration";

  private static final int MAX_SLEEP_TIME = 3600000;

  private int milliseconds;


  @Override
  public CollectorStepResult collect() throws ProcessingException {
    LOG.debug("Sleeping for {} milliseconds", milliseconds);
    sleepInMillis(milliseconds);
    return CollectorStepResult.newModifierResult();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    milliseconds = NumberUtils.toInt(params.get(DURATION_PARAM));
    ParametersValidator
        .checkRange(milliseconds, 1, MAX_SLEEP_TIME, "Duration time should be greater than 0");
  }

  private void sleepInMillis(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}
