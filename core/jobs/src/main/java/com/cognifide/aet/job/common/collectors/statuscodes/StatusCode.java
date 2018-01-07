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
package com.cognifide.aet.job.common.collectors.statuscodes;

import com.cognifide.aet.job.common.Excludable;
import java.io.Serializable;

public class StatusCode implements Serializable, Excludable {

  private static final long serialVersionUID = -4480994340099872733L;

  private final Integer code;

  private final String url;

  /**
   * If status code is excluded from results, i.e. is listed but not taken into account when
   * computed results.
   */
  private boolean excluded;

  public StatusCode(Integer code, String url) {
    this.code = code;
    this.url = url;
  }

  public Integer getCode() {
    return code;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public boolean isExcluded() {
    return excluded;
  }

  @Override
  public void exclude() {
    this.excluded = true;
  }

}
