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
package com.cognifide.aet.rest.helpers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import com.cognifide.aet.rest.helpers.configuration.ReportConfigurationManagerConf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReportConfigurationManagerTest {

  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Mock
  private ReportConfigurationManagerConf config;

  @Test
  public void activate_whenNoReportDomainSettingProvided_expectDefaultValue() {
    final ReportConfigurationManager manager = new ReportConfigurationManager();
    when(config.reportDomain()).thenReturn("");
    manager.activate(config);
    assertThat(manager.getReportDomain(), is("http://aet-vagrant"));
  }

  @Test
  public void activate_whenOnlyOsgiConfigProvided_expectConfiguredValue() {
    final ReportConfigurationManager manager = new ReportConfigurationManager();
    when(config.reportDomain()).thenReturn("http://custom-report-domain");
    manager.activate(config);
    assertThat(manager.getReportDomain(), is("http://custom-report-domain"));
  }

  @Test
  public void activate_whenOnlyEnvConfigProvided_expectConfiguredValue() {
    environmentVariables.set("REPORT_DOMAIN", "http://env-set-domain");
    final ReportConfigurationManager manager = new ReportConfigurationManager();
    when(config.reportDomain()).thenReturn("");
    manager.activate(config);
    assertThat(manager.getReportDomain(), is("http://env-set-domain"));
  }

  @Test
  public void activate_whenOsgiAndEnvConfigProvided_expectOsgiConfigValue() {
    environmentVariables.set("REPORT_DOMAIN", "http://env-set-domain");
    final ReportConfigurationManager manager = new ReportConfigurationManager();
    when(config.reportDomain()).thenReturn("http://custom-report-domain");
    manager.activate(config);
    assertThat(manager.getReportDomain(), is("http://custom-report-domain"));
  }
}