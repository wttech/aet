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
/*
 * Copyright [2016] [http://bmp.lightbody.net/]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.browsermob.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {

  public static String dumpAllThreads() {
    ThreadInfo[] dumps = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
    StringBuilder out = new StringBuilder("Thread Dump\n");
    for (ThreadInfo dump : dumps) {
      out.append("-------------------------\n").append(dump);
    }

    return out.toString();
  }

  public static interface WaitCondition {

    public boolean checkCondition(long elapsedTimeInMs);
  }

  public static void sleep(TimeUnit timeUnit, long duration) {
    try {
      timeUnit.sleep(duration);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  public static boolean waitFor(WaitCondition condition) {
    boolean result = false;
    if (condition != null) {
      long startTime = System.currentTimeMillis();

      while (!(result = condition.checkCondition(System.currentTimeMillis() - startTime))) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
    }

    return result;
  }

  public static boolean waitFor(WaitCondition condition, TimeUnit timeUnit, long timeoutDuration) {
    long timeout = timeUnit.toMillis(timeoutDuration);

    boolean result = false;
    if (condition != null) {
      long startTime = System.currentTimeMillis();
      long curTime = startTime;

      while (!(result = condition.checkCondition(curTime - startTime)) && (curTime - startTime
          < timeout)) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
        curTime = System.currentTimeMillis();
      }
    }

    return result;
  }

  public static boolean waitFor(WaitCondition condition, TimeUnit timeUnitTimeout,
      long timeoutDuration, TimeUnit timeUnitSleep, long sleepDuration) {
    long timeout = timeUnitTimeout.toMillis(timeoutDuration);
    long sleepBetween = timeUnitSleep.toMillis(sleepDuration);

    boolean result = false;
    if (condition != null) {
      long startTime = System.currentTimeMillis();
      long curTime = startTime;

      while (!(result = condition.checkCondition(curTime - startTime)) && (curTime - startTime
          < timeout)) {
        try {
          Thread.sleep(sleepBetween);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
        curTime = System.currentTimeMillis();
      }
    }

    return result;
  }
}
