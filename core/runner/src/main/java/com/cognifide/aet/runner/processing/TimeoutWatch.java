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
package com.cognifide.aet.runner.processing;

import java.util.concurrent.TimeUnit;

/**
 * TimeoutWatch - used to measure gaps between updates from the suite processing done by workers.
 */
public class TimeoutWatch {

  private long lastUpdateTimestamp;

  public synchronized void update() {
    lastUpdateTimestamp = System.nanoTime();
  }

  /**
   * @param acceptedSecondsDifference accepted difference in seconds
   * @return true if last update was done more than acceptedSecondsDifference seconds from now
   */
  synchronized boolean isTimedOut(long acceptedSecondsDifference) {
    return TimeUnit.NANOSECONDS.toSeconds(getLastUpdateDifference()) > acceptedSecondsDifference;
  }

  /**
   * @return last update timestamp and now difference in nanoseconds
   */
  long getLastUpdateDifference() {
    return System.nanoTime() - lastUpdateTimestamp;
  }
}
