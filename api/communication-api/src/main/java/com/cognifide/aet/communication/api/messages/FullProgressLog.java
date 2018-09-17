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
package com.cognifide.aet.communication.api.messages;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class FullProgressLog implements Serializable {

  private ProgressLog compareLog;
  private ProgressLog collectLog;

  public FullProgressLog(ProgressLog collectLog, ProgressLog compareLog) {
    this.collectLog = collectLog;
    this.compareLog = compareLog;
  }

  public ProgressLog getCompareLog() {
    return compareLog;
  }

  public ProgressLog getCollectLog() {
    return collectLog;
  }

  @Override
  public String toString() {
    return StringUtils.join(Arrays.asList(compareLog, collectLog), " ::: ");
  }
}
