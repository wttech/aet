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
package com.cognifide.aet.executor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.SuiteBuilder;
import com.cognifide.aet.executor.model.CollectorStep;
import com.cognifide.aet.executor.model.ComparatorStep;
import com.cognifide.aet.executor.model.TestRun;
import com.cognifide.aet.executor.model.TestSuiteRun;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SuiteValidatorTest {

  private static final String COMPANY = "company";
  private static final String PROJECT = "project";
  private static final String TEST_NAME = "testName";

  private SuiteValidator suiteValidator;

  @Mock
  private MetadataDAO metadataDAO;

  @Mock
  private TestSuiteRun testSuiteRun;

  @Mock
  private TestRun testRun;

  @Before
  public void setUp() {
    suiteValidator = new SuiteValidator(metadataDAO);
    when(testSuiteRun.getCompany()).thenReturn(COMPANY);
    when(testSuiteRun.getProject()).thenReturn(PROJECT);
  }

  @Test
  public void validateTestSuiteRun_whenPatternIdFromDifferentProject_expectError() {
    String patternCorrelationId = "company1-project1-suite-name-123456789";
    when(testSuiteRun.getPatternCorrelationId()).thenReturn(patternCorrelationId);

    String errorMessage = String.format(
        "Incorrect pattern: '%s'. Must belong to same company (%s) and project (%s).",
        patternCorrelationId, COMPANY, PROJECT);
    assertThat(suiteValidator.validateTestSuiteRun(testSuiteRun), equalTo(errorMessage));
  }

  @Test
  public void validateTestSuiteRun_whenPatternIdNotInDb_expectError() throws StorageException {
    String patternCorrelationId = "company-project-suite-name-123456789";
    when(testSuiteRun.getPatternCorrelationId()).thenReturn(patternCorrelationId);

    String errorMessage = String.format(
        "Incorrect pattern: correlationId='%s', suiteName='%s'. Not found in database.",
        patternCorrelationId, null);
    assertThat(suiteValidator.validateTestSuiteRun(testSuiteRun), equalTo(errorMessage));
    verify(metadataDAO).getSuite(any(), eq(patternCorrelationId));
  }

  @Test
  public void validateTestSuiteRun_whenPatternSuiteNotInDb_expectError() throws StorageException {
    String patternSuite = "suite-name";
    when(testSuiteRun.getPatternSuite()).thenReturn(patternSuite);

    String errorMessage = String.format(
        "Incorrect pattern: correlationId='%s', suiteName='%s'. Not found in database.",
        null, patternSuite);
    assertThat(suiteValidator.validateTestSuiteRun(testSuiteRun), equalTo(errorMessage));
    verify(metadataDAO).getLatestRun(any(), eq(patternSuite));
  }

  @Test
  public void validateTestSuiteRun_whenScreenCollectorWithoutComparator_expectError() {
    givenSuiteHasTestWithScreenCollector();
    String errorMessage = String.format(
        "Test suite does not contain screen comparator for screen collector in '%s' test, please fix it",
        TEST_NAME);
    assertThat(suiteValidator.validateTestSuiteRun(testSuiteRun), equalTo(errorMessage));
  }

  @Test
  public void validateTestSuiteRun_whenValidSuite_expectSuccess() {
    assertNull(suiteValidator.validateTestSuiteRun(testSuiteRun));
  }

  @Test
  public void validateTestSuiteRun_whenValidSuiteWithPatternId_expectSuccess()
      throws StorageException {
    String patternCorrelationId = "company-project-suite-name-123456789";
    Suite patternSuite = new SuiteBuilder().setCorrelationId(patternCorrelationId).setCompany(COMPANY).setProject(PROJECT).setName("suite-name").setPatternCorrelationId(null)
        .createSuite();
    when(testSuiteRun.getPatternCorrelationId()).thenReturn(patternCorrelationId);
    when(metadataDAO.getSuite(any(), eq(patternCorrelationId))).thenReturn(patternSuite);

    assertNull(suiteValidator.validateTestSuiteRun(testSuiteRun));
  }

  @Test
  public void validateTestSuiteRun_whenValidSuiteWithScreenCollectorAndComparator_expectSuccess() {
    givenSuiteHasTestWithScreenCollector();
    ComparatorStep screenComparator = mock(ComparatorStep.class);
    when(screenComparator.getType()).thenReturn("screen");
    when(testRun.getComparatorSteps())
        .thenReturn(ImmutableMap.of("screen", Collections.singletonList(screenComparator)));

    assertNull(suiteValidator.validateTestSuiteRun(testSuiteRun));
  }

  private void givenSuiteHasTestWithScreenCollector() {
    CollectorStep screenCollectorStep = new CollectorStep("screen", "screen", Maps.newHashMap());
    when(testSuiteRun.getTestRunMap()).thenReturn(ImmutableMap.of(TEST_NAME, testRun));
    when(testRun.getName()).thenReturn(TEST_NAME);
    when(testRun.getCollectorSteps()).thenReturn(Collections.singletonList(screenCollectorStep));
    when(testRun.getComparatorSteps()).thenReturn(Collections.emptyMap());
  }

}
