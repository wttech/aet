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
package com.cognifide.aet.job.common.utils;

import com.cognifide.aet.job.common.modifiers.resolution.ResolutionModifier;
import java.util.LinkedList;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sampler {

  private static final Logger LOG = LoggerFactory.getLogger(ResolutionModifier.class);

  /**
   * Collects values from supplier in specified periods of time, and compares last n samples every
   * iteration. If all n samples are equal(), returns the value. If last n samples don't match
   * before max iterations threshold is reached, returns last collected sample.
   *
   * @param samplesSupplier supplier of value to wait for,
   * @param samplingPeriod milliseconds period between taking each sample,
   * @param sampleQueueSize defines the last n elements that are to be compared,
   * @param maxSamplesThreshold max number of samples before return
   * @return true if pattern is empty or if its match with given value, false otherwise
   */
  public static <T> T waitForValue(Supplier<T> samplesSupplier, int samplingPeriod,
      int sampleQueueSize, int maxSamplesThreshold) {
    LinkedList<T> samplesQueue = new LinkedList<>();

    int samplesTaken = 0;
    while (!isThresholdReached(samplesTaken, maxSamplesThreshold) &&
        !areAllSamplesEqual(samplesQueue, sampleQueueSize)) {

      CurrentThread.sleep(samplingPeriod);

      T nextSample = samplesSupplier.get();
      removeLast(samplesQueue, sampleQueueSize);
      samplesQueue.addFirst(nextSample);
      ++samplesTaken;
    }
    return samplesQueue.getFirst();
  }

  private static boolean isThresholdReached(int samplesTaken, int maxSamplesThreshold) {
    if (samplesTaken >= maxSamplesThreshold) {
      LOG.warn("Sampling reached threshold");
      return true;
    }
    return false;
  }

  private static boolean areAllSamplesEqual(LinkedList<?> samplesQueue, int sampleSize) {
    return isQueueFull(samplesQueue, sampleSize) &&
        samplesQueue.stream().allMatch(sample -> samplesQueue.get(0).equals(sample));
  }

  private static boolean isQueueFull(LinkedList<?> samplesQueue, int queueSize) {
    return samplesQueue.size() == queueSize;
  }

  private static <T> void removeLast(LinkedList<T> samplesQueue, int sampleQueueSize) {
    if (isQueueFull(samplesQueue, sampleQueueSize)) {
      samplesQueue.removeLast();
    }
  }
}
