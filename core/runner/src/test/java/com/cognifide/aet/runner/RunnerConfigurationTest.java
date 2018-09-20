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
