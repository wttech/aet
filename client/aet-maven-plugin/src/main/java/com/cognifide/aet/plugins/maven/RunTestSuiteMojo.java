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
package com.cognifide.aet.plugins.maven;

import com.cognifide.aet.common.TestSuiteRunner;
import com.cognifide.aet.communication.api.exceptions.AETException;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


@Mojo(name = "run", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
@Deprecated
public class RunTestSuiteMojo extends AbstractMojo {

  private static final String SCREEN = "screen";

  @Parameter(property = "testSuite", required = true, defaultValue = "suite.xml")
  private File testSuite;

  @Parameter(property = "endpointDomain", defaultValue = "http://localhost:8181")
  private String endpointDomain;

  @Parameter(property = "name")
  private String name;

  @Parameter(property = "domain")
  private String domain;

  @Parameter(property = "pattern")
  private String patternCorrelationId;

  @Parameter(property = "patternSuite")
  private String patternSuite;

  @Parameter(property = "timeout", defaultValue = "300000")
  private int timeout;

  @Parameter(property = "xUnit", defaultValue = "false")
  private boolean xUnit;

  @Component
  private MavenProject mavenProject;

  @Override
  public void execute() throws MojoExecutionException {
    validateConfiguration();
    try {
      TestSuiteRunner testSuiteRunner = new TestSuiteRunner(endpointDomain,
          mavenProject.getBuild().getDirectory(), timeout, name, domain, patternCorrelationId, patternSuite, xUnit);
      testSuiteRunner.runTestSuite(testSuite);

    } catch (AETException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private void validateConfiguration() throws MojoExecutionException {
    if (!testSuite.exists()) {
      throw new MojoExecutionException(
          String.format("Test suite '%s' does not exist", testSuite.getName()));
    }
  }

}
