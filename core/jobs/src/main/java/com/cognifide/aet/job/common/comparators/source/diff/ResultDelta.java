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

public class ResultDelta implements Serializable {

  private static final long serialVersionUID = 5507408432566479491L;

  /**
   * The original chunk.
   */
  private ResultChunk original;

  /**
   * The revised chunk.
   */
  private ResultChunk revised;

  private TYPE type;

  public enum TYPE {
    /**
     * No change detected.
     */
    NO_CHANGE,
    /**
     * A change in the original.
     */
    CHANGE,
    /**
     * A delete from the original.
     */
    DELETE,
    /**
     * An insert into the original.
     */
    INSERT
  }

  public ResultDelta(TYPE type, ResultChunk original, ResultChunk revised) {
    this.original = original;
    this.revised = revised;
    this.type = type;
  }

  public ResultChunk getOriginal() {
    return original;
  }

  public void setOriginal(ResultChunk original) {
    this.original = original;
  }

  public ResultChunk getRevised() {
    return revised;
  }

  public void setRevised(ResultChunk revised) {
    this.revised = revised;
  }

  public TYPE getType() {
    return type;
  }

  public void setType(TYPE type) {
    this.type = type;
  }

}
