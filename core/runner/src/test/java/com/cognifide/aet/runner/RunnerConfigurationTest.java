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
package com.cognifide.aet.runner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import com.cognifide.aet.runner.configuration.RunnerConfigurationConf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RunnerConfigurationTest {

  private RunnerConfiguration runnerConfiguration;

  @Mock
  private RunnerConfigurationConf config;

  @Test
  public void getMttl_whenOsgiConfigReturnsSeconds_expectMilliseconds() {
    long mttlInSeconds = 300L;
    long mttlInMilliseconds = 300000L;

    when(config.mttl()).thenReturn(mttlInSeconds);
    runnerConfiguration = new RunnerConfiguration();
    runnerConfiguration.activate(config);

    assertThat(runnerConfiguration.getMttl(), equalTo(mttlInMilliseconds));
  }
}
