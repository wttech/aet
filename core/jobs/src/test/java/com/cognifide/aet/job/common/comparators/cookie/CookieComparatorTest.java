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
package com.cognifide.aet.job.common.comparators.cookie;

import static org.junit.Assert.assertEquals;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.Pattern;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.common.ArtifactDAOMock;
import com.cognifide.aet.job.common.comparators.AbstractComparatorTest;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CookieComparatorTest extends AbstractComparatorTest {

  private CookieComparator tested;

  @Before
  public void setUp() throws Exception {
    artifactDaoMock = new ArtifactDAOMock(CookieComparator.class);
    comparatorProperties = new ComparatorProperties(TEST_COMPANY, TEST_PROJECT,
        Collections.singleton(new Pattern("pattern-result.json", null)), "data-result.json");
    tested = new CookieComparator(comparatorProperties, artifactDaoMock);
  }

  @Test
  public void testCompare_list() throws Exception {
    tested.setParameters(ImmutableMap.of("action", "list"));
    result = tested.compare();

    assertEqualsToSavedArtifact("expected-list-result.json");
    assertEquals(ComparatorStepResult.Status.PASSED, result.get(0).getStatus());
    assertEquals(1, result.size());
  }

  @Test
  public void testCompare_testSuccess() throws Exception {
    tested.setParameters(ImmutableMap.of("action", "test", "cookie-name", "name1"));
    result = tested.compare();

    assertEqualsToSavedArtifact("expected-test-success-result.json");
    assertEquals(ComparatorStepResult.Status.PASSED, result.get(0).getStatus());
    assertEquals(1, result.size());
  }

  @Test
  public void testCompare_testFailed() throws Exception {
    tested.setParameters(ImmutableMap.of("action", "test", "cookie-name", "name3"));
    result = tested.compare();

    assertEqualsToSavedArtifact("expected-test-failed-result.json");
    assertEquals(result.get(0).getStatus(), ComparatorStepResult.Status.FAILED);
    assertEquals(1, result.size());
  }

  @Test
  public void testCompare_compareSuccess() throws Exception {
    comparatorProperties = new ComparatorProperties(TEST_COMPANY, TEST_PROJECT,
        Collections.singleton(new Pattern("identical-pattern-result.json", null)),
        "data-result.json");
    tested = new CookieComparator(comparatorProperties, artifactDaoMock);
    tested.setParameters(ImmutableMap.of("action", "compare", "showMatched", "true"));
    result = tested.compare();

    assertEqualsToSavedArtifact("expected-compare-success-result.json");
    assertEquals(ComparatorStepResult.Status.PASSED, result.get(0).getStatus());
    assertEquals(1, result.size());
  }

  @Test
  public void testCompare_compareFailed() throws Exception {
    tested.setParameters(ImmutableMap.of("action", "compare"));
    result = tested.compare();

    assertEqualsToSavedArtifact("expected-compare-failed-result.json");
    assertEquals(ComparatorStepResult.Status.FAILED, result.get(0).getStatus());
    assertEquals(1, result.size());
  }

}
