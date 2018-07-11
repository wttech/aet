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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.executor.model.TestSuiteRun;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SuiteFactoryTest {

  private static final String COMPANY = "company";
  private static final String PROJECT = "project";
  private static final String SUITE_NAME = "suite-name";
  private static final String CORRELATION_ID = "company-project-suite-name-123456789";

  private SuiteFactory suiteFactory;

  @Mock
  private MetadataDAO metadataDAO;

  @Mock
  private TestSuiteRun testSuiteRun;

  @Mock
  private Suite patternSuite;

  @Before
  public void setUp() {
    suiteFactory = new SuiteFactory(metadataDAO);
    when(testSuiteRun.getCompany()).thenReturn(COMPANY);
    when(testSuiteRun.getProject()).thenReturn(PROJECT);
    when(testSuiteRun.getName()).thenReturn(SUITE_NAME);
    when(testSuiteRun.getCorrelationId()).thenReturn(CORRELATION_ID);
  }

  @Test
  public void suiteFromTestSuiteRun_whenValidTestSuite_expectSameFieldsInResult() {
    String patternCorrelationId = "company-project-other-suite-012345678";
    when(testSuiteRun.getPatternCorrelationId()).thenReturn(patternCorrelationId);

    Suite suite = suiteFactory.suiteFromTestSuiteRun(testSuiteRun);
    assertThat(suite.getCompany(), equalTo(COMPANY));
    assertThat(suite.getProject(), equalTo(PROJECT));
    assertThat(suite.getName(), equalTo(SUITE_NAME));
    assertThat(suite.getCorrelationId(), equalTo(CORRELATION_ID));
    assertThat(suite.getPatternCorrelationId(), equalTo(patternCorrelationId));
  }

  @Test
  public void suiteFromTestSuiteRun_whenPatternSuiteNameProvided_expectFetchingPatternIdFromDb()
      throws StorageException {
    String patternCorrelationId = "company-project-other-suite-012345678";
    String patternSuiteName = "other-suite";
    when(testSuiteRun.getPatternSuite()).thenReturn(patternSuiteName);
    when(metadataDAO.getLatestRun(any(), eq(patternSuiteName))).thenReturn(patternSuite);
    when(patternSuite.getCorrelationId()).thenReturn(patternCorrelationId);

    Suite suite = suiteFactory.suiteFromTestSuiteRun(testSuiteRun);
    assertThat(suite.getPatternCorrelationId(), equalTo(patternCorrelationId));
  }

}
