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
package com.cognifide.aet.job.api.collector;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class JsErrorLogTest {

  private JsErrorLog jsErrorLog;

  @Before
  public void setUp() throws Exception {
    jsErrorLog = new JsErrorLog("Error message", "source.js", 1);
  }

  @Test
  public void testEqualsWithTheSameObjectThenExpectTrue() throws Exception {
    assertTrue(jsErrorLog.equals(jsErrorLog));
  }

  @Test
  public void testEqualsWithEqualObjectThenExpectTrue() throws Exception {
    assertTrue(jsErrorLog.equals(new JsErrorLog("Error message", "source.js", 1)));
  }

  @Test
  public void testEqualsWithNullThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(null));
  }

  @Test
  public void testEqualsWithOtherObjectThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals("xxxxxx"));
  }

  @Test
  public void testEqualsWhenOnlyErrorMessageDiffersThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(new JsErrorLog("Other error message", "source.js", 1)));
  }

  @Test
  public void testEqualsWhenOnlySourceDiffersThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(new JsErrorLog("Error message", "other_source.js", 1)));
  }

  @Test
  public void testEqualsWhenOnlyLineNumberDiffersThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(new JsErrorLog("Error message", "source.js", 2)));
  }

  @Test
  public void testEqualsWhenErrorMessageAndSourceDifferThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(new JsErrorLog("Other error message", "other_source.js", 1)));
  }

  @Test
  public void testEqualsWhenErrorMessageAndLineNumberDifferThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(new JsErrorLog("Other error message", "source.js", 2)));
  }

  @Test
  public void testEqualsWhenSourceAndLineNumberDifferThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(new JsErrorLog("Error message", "other_source.js", 2)));
  }

  @Test
  public void testEqualsWhenEverythingDiffersThenExpectFalse() throws Exception {
    assertFalse(jsErrorLog.equals(new JsErrorLog("Other error message", "other_source.js", 2)));
  }
}
