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

import static com.google.common.testing.GuavaAsserts.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class TimeoutWatchTest {

  private TimeoutWatch tested;

  @Before
  public void setUp() {
    tested = new TimeoutWatch();
  }

  @Test
  public void update_expectTimestampUpdated() {
    long beforeUpdate = tested.getLastUpdateDifference();
    tested.update();
    long afterUpdate = tested.getLastUpdateDifference();
    assertTrue(beforeUpdate > afterUpdate);
  }

  @Test
  public void isTimedOut_beforeAcceptedDifference_expectFalse() throws Exception {
    tested.update();
    Thread.sleep(1000);
    assertFalse(tested.isTimedOut(10));
  }

  @Test
  public void isTimedOut_afterAcceptedDifference_expectTrue() throws Exception {
    tested.update();
    Thread.sleep(2000);
    assertTrue(tested.isTimedOut(0));
  }
}
