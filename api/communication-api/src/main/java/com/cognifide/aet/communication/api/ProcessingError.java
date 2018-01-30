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
package com.cognifide.aet.communication.api;

import com.google.common.base.MoreObjects;
import java.io.Serializable;

/**
 * Error that may happen during test processing. Contains description and phase of processing.
 *
 * @author lukasz.wieczorek
 */
public final class ProcessingError implements Serializable {

  private static final long serialVersionUID = 3316639542860063365L;

  private final String description;

  private final Phase phase;

  public ProcessingError(String description, Phase phase) {
    this.description = description;
    this.phase = phase;
  }

  public static ProcessingError comparingError(String description) {
    return new ProcessingError(description, Phase.COMPARING);
  }

  public static ProcessingError collectingError(String description) {
    return new ProcessingError(description, Phase.COLLECTING);
  }

  public static ProcessingError reportingError(String description) {
    return new ProcessingError(description, Phase.REPORTING);
  }

  /**
   * @return error description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return phase during which error occurred.
   */
  public Phase getPhase() {
    return phase;
  }

  /**
   * Processing phases.
   */
  public enum Phase {
    COLLECTING, COMPARING, REPORTING
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("phase", phase).add("description", description)
        .toString();
  }
}
