/**
 * Automated Exploratory Tests
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
package com.cognifide.aet.plugins.maven;

import com.google.common.collect.Lists;

import com.cognifide.aet.common.TestSuiteRunner;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.model.CollectorStep;
import com.cognifide.aet.model.ComparatorStep;
import com.cognifide.aet.model.TestRun;
import com.cognifide.aet.model.TestSuiteRun;
import com.cognifide.aet.xmlparser.api.ParseException;
import com.cognifide.aet.xmlparser.api.TestSuiteParser;
import com.cognifide.aet.xmlparser.xml.XmlTestSuiteParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;


@Mojo(name = "run", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class RunTestSuiteMojo extends AbstractMojo {

  private static final String SCREEN = "screen";

  @Parameter(property = "testSuite", required = true, defaultValue = "suite.xml")
  private File testSuite;

  @Parameter(property = "endpointDomain", defaultValue = "http://localhost:8181")
  private String endpointDomain;

  @Parameter(property = "inQueue", defaultValue = "AET.runner-in")
  private String inQueue;

  @Parameter(property = "domain")
  private String domain;

  @Parameter(property = "timeout", defaultValue = "300000")
  private long timeout;

  @Parameter(property = "xUnit", defaultValue = "false")
  private boolean xUnit;

  @Component
  private MavenProject mavenProject;

  @Override
  public void execute() throws MojoExecutionException {
    validateConfiguration();
    TestSuiteParser xmlFileParser = new XmlTestSuiteParser();
    TestSuiteRunner testSuiteRunner;
    try {
      TestSuiteRun testSuiteRun = xmlFileParser.parse(testSuite);
      testSuiteRun = overrideDomainIfDefined(testSuiteRun);
      validateTestSuiteRun(testSuiteRun);

      testSuiteRunner = new TestSuiteRunner(endpointDomain, inQueue, mavenProject.getBuild().getDirectory(),
              timeout, xUnit);
      testSuiteRunner.runTestSuite(testSuiteRun);

    } catch (AETException | ParseException | IOException | JMSException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private TestSuiteRun overrideDomainIfDefined(TestSuiteRun testSuiteRun) {
    TestSuiteRun localTestSuiteRun = testSuiteRun;
    if (StringUtils.isNotBlank(domain)) {
      List<TestRun> tests = Lists.newArrayList(localTestSuiteRun.getTestRunMap().values());
      localTestSuiteRun = new TestSuiteRun(localTestSuiteRun, domain, tests);
    }
    return localTestSuiteRun;
  }

  private void validateConfiguration() throws MojoExecutionException {
    if (!testSuite.exists()) {
      throw new MojoExecutionException(
              String.format("Test suite '%s' does not exist", testSuite.getName()));
    }
  }

  private void validateTestSuiteRun(TestSuiteRun testSuiteRun) throws MojoExecutionException {
    for (TestRun testRun : testSuiteRun.getTestRunMap().values()) {
      if (hasScreenCollector(testRun) && !hasScreenComparator(testRun)) {
        throw new MojoExecutionException(String.format(
                "Test suite '%s' does not contain screen comparator for screen collector in '%s' test, please fix it",
                testSuite.getName(), testRun.getName()));
      }
    }
  }

  private boolean hasScreenCollector(TestRun testRun) {
    for (CollectorStep collectorStep : testRun.getCollectorSteps()) {
      if (SCREEN.equalsIgnoreCase(collectorStep.getModule())) {
        return true;
      }
    }
    return false;
  }

  private boolean hasScreenComparator(TestRun testRun) {
    for (List<ComparatorStep> comparatorSteps : testRun.getComparatorSteps().values()) {
      for (ComparatorStep comparatorStep : comparatorSteps) {
        if (SCREEN.equalsIgnoreCase(comparatorStep.getType())) {
          return true;
        }
      }
    }
    return false;
  }

}
