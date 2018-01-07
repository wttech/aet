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
package com.cognifide.aet.job.common.comparators.accessibility;


import static org.junit.Assert.assertEquals;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.common.ArtifactDAOMock;
import com.cognifide.aet.job.common.comparators.AbstractComparatorTest;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityComparatorTest extends AbstractComparatorTest {

  private AccessibilityComparator accessibilityComparator;

  @Before
  public void setUp() throws Exception {
    artifactDaoMock = new ArtifactDAOMock(AccessibilityComparator.class);
    comparatorProperties = new ComparatorProperties(TEST_COMPANY, TEST_PROJECT, null,
        "data-result.json");
    accessibilityComparator = new AccessibilityComparator(artifactDaoMock, comparatorProperties,
        new ArrayList<DataFilterJob>());
  }

  @Test
  public void testCompare_with_report_level_WARN() throws Exception {
    accessibilityComparator.setParameters(ImmutableMap.of("report-level", "WARN"));
    result = accessibilityComparator.compare();
    assertEqualsToSavedArtifact("report-level-warn-result.json");
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void testCompare_with_report_level_ERROR() throws Exception {
    accessibilityComparator.setParameters(ImmutableMap.of("report-level", "ERROR"));
    result = accessibilityComparator.compare();
    assertEqualsToSavedArtifact("report-level-error-result.json");
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }
}
