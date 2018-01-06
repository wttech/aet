/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.runner.distribution;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author maciej.laskowski
 *         Created: 2015-04-28
 */
@RunWith(MockitoJUnitRunner.class)
public class SuiteExecutionSettingsTest {


  @Test
  public void shouldExecute_whenRunningInOnlineModeAndExecutingBasicMessage_expectTrue() throws
          Exception {
    SuiteExecutionSettings suiteExecutionSettings = new SuiteExecutionSettings(RunnerMode.ONLINE, false);
    assertTrue(suiteExecutionSettings.shouldExecute());
  }

  @Test
  public void shouldExecute_whenRunningInOnlineModeAndExecutingMaintenanceMessage_expectFalse() throws
          Exception {
    SuiteExecutionSettings suiteExecutionSettings = new SuiteExecutionSettings(RunnerMode.ONLINE, true);
    assertFalse(suiteExecutionSettings.shouldExecute());
  }

  @Test
  public void shouldExecute_whenRunningInMaintenanceModeAndExecutingBasicMessage_expectFalse() throws
          Exception {
    SuiteExecutionSettings suiteExecutionSettings = new SuiteExecutionSettings(RunnerMode.MAINTENANCE, false);
    assertFalse(suiteExecutionSettings.shouldExecute());
  }

  @Test
  public void shouldExecute_whenRunningInMaintenanceModeAndExecutingMaintenanceMessage_expectTrue() throws
          Exception {
    SuiteExecutionSettings suiteExecutionSettings = new SuiteExecutionSettings(RunnerMode.MAINTENANCE, true);
    assertTrue(suiteExecutionSettings.shouldExecute());
  }

  @Test
  public void shouldExecute_whenRunningInOfflineModeAndExecutingBasicMessage_expectFalse() throws
          Exception {
    SuiteExecutionSettings suiteExecutionSettings = new SuiteExecutionSettings(RunnerMode.OFFLINE, false);
    assertFalse(suiteExecutionSettings.shouldExecute());
  }

  @Test
  public void shouldExecute_whenRunningInOfflineModeAndExecutingMaintenanceMessage_expectTrue() throws
          Exception {
    SuiteExecutionSettings suiteExecutionSettings = new SuiteExecutionSettings(RunnerMode.OFFLINE, true);
    assertTrue(suiteExecutionSettings.shouldExecute());
  }

}