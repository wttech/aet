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
package com.cognifide.aet.job.common.comparators.source.diff;

import java.io.Serializable;

public class ResultChunk implements Serializable {

  private static final long serialVersionUID = -3896146920562584104L;

  private int position;

  private String prettyHtml;

  public ResultChunk(int position, String prettyHtml) {
    this.prettyHtml = prettyHtml;
    this.position = position;
  }

  public int getPosition() {
    return position;
  }

  public String getPrettyHtml() {
    return prettyHtml;
  }

}
