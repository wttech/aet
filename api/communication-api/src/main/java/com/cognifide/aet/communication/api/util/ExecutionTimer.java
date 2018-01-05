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
package com.cognifide.aet.communication.api.util;

import com.cognifide.aet.communication.api.metadata.Statistics;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExecutionTimer which measures execution time.
 */
public final class ExecutionTimer {

  private static final double NANOSECONDS_IN_SECOND = 1000000000;

  private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimer.class);

  private static final DecimalFormat FORMAT = new DecimalFormat("#.####");

  private long start;

  private long end;

  private String moduleName;

  private ExecutionTimer(long start, String moduleName) {
    this.start = start;
    this.moduleName = moduleName;
  }

  /**
   * @param moduleName - name of module.
   * @return new instance of ExecutionTimer which has been already started.
   */
  public static ExecutionTimer createAndRun(String moduleName) {
    return new ExecutionTimer(System.nanoTime(), moduleName);
  }

  /**
   * @return Execution Timer
   */
  public ExecutionTimer finish() {
    end = System.nanoTime();
    return this;
  }

  /**
   * Stops execution time and logs in DEBUG mode execution time.
   *
   * @param taskName - task name that will be logged.
   */
  public void finishAndLog(String taskName) {
    finish();
    long executionTime = getExecutionTimeInNanos();
    double durationInSeconds = (double) executionTime / NANOSECONDS_IN_SECOND;
    LOGGER.debug("#TIMER-{}#: '{}' took {} ns ({} seconds)", moduleName, taskName, executionTime,
        FORMAT.format(durationInSeconds));
  }

  /**
   * @return formatted execution time in mm:ss.ms format.
   */
  public String getExecutionTimeInMMSS() {
    long executionTime = getExecutionTimeInNanos();
    return String.format(
        "%02d:%02d.%02d",
        TimeUnit.NANOSECONDS.toMinutes(executionTime),
        TimeUnit.NANOSECONDS.toSeconds(executionTime)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(executionTime)),
        TimeUnit.NANOSECONDS.toMillis(executionTime)
            - TimeUnit.SECONDS.toMillis(TimeUnit.NANOSECONDS.toSeconds(executionTime)));
  }

  public long getExecutionTimeInMillis() {
    return TimeUnit.NANOSECONDS.toMillis(getExecutionTimeInNanos());
  }

  private long getExecutionTimeInNanos() {
    return end - start;
  }

  /**
   * @return timestamp of execution start
   */
  public long getStart() {
    return start;
  }

  /**
   * @return timestamp of execution end or 0 if execution still in progress.
   */
  public long getEnd() {
    return end;
  }

  public Statistics toStatistics() {
    return new Statistics(getExecutionTimeInMillis());
  }
}
