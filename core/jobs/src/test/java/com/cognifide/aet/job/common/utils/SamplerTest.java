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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.function.Supplier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SamplerTest {

  private static final int MAX_SAMPLES_THRESHOLD = 5;
  private static final int SAMPLE_QUEUE_SIZE = 3;
  private static final int SAMPLING_PERIOD = 1;

  @Mock
  private Supplier<Integer> supplier;

  @Test
  public void sampleChangingValueTest_AllSamplesMatch() {
    when(supplier.get()).thenReturn(0);

    Integer finalSample = Sampler
        .waitForValue(supplier, SAMPLING_PERIOD, SAMPLE_QUEUE_SIZE, MAX_SAMPLES_THRESHOLD);

    assertEquals((int) finalSample, 0);
  }

  @Test
  public void sampleChangingValueTest_AllSamplesDiffer_ThresholdReached() {
    Random random = new Random();
    when(supplier.get())
        .thenReturn(random.nextInt())
        .thenReturn(random.nextInt())
        .thenReturn(random.nextInt())
        .thenReturn(random.nextInt())
        .thenReturn(random.nextInt());

    Sampler.waitForValue(supplier, 1, 3, 5);

    verify(supplier, times(5)).get();
  }
}