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
package com.cognifide.aet.job.common.comparators.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.common.ArtifactDAOMock;
import com.cognifide.aet.job.common.comparators.AbstractComparatorTest;
import com.cognifide.aet.job.common.comparators.source.diff.DiffParser;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SourceComparatorTest extends AbstractComparatorTest {

  private SourceComparator tested;

  @Before
  public void setUp() {
    artifactDaoMock = new ArtifactDAOMock(SourceComparator.class);
  }

  /**
   * Compares two files. Filenames paths should be relative to <code>.../src/test/resources/mock/SourceComparator</code>.
   *
   * @param patternFilename pattern Html file
   * @param dataFilename data Html file that will be compared to pattern
   * @param comparatorParams additional parameters for source comparator
   * @return result of comparison
   * @throws Exception for incorrect parameters
   */
  private ComparatorStepResult compare(String patternFilename, String dataFilename,
      Map<String, String> comparatorParams)
      throws Exception {
    ComparatorProperties properties = new ComparatorProperties(TEST_COMPANY, TEST_PROJECT,
        patternFilename, dataFilename);
    Sources sources = new Sources(artifactDaoMock, properties, new ArrayList<>(),
        new CodeFormatter());
    tested = new SourceComparator(artifactDaoMock, properties, new DiffParser(), sources);
    tested.setParameters(comparatorParams);
    return tested.compare();
  }

  @Test
  public void testCompareOfValidInput() throws Exception {
    result = compare("pattern-source.html", "data-source.html", new HashMap<>());

    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
    assertFalse(result.isRebaseable());
    // artifact is saved - with formatted sources of pattern and tested data, even if they are equal
    assertEqualsToSavedArtifact("expected-result-with-sources.json");
  }

  @Test
  public void testCompareOfInvalidInput() throws Exception {
    result = compare("pattern-source.html", "data-invalid-source.html",
        new HashMap<>());

    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
    assertEqualsToSavedArtifact("expected-invalid-result.json");
  }

  @Test
  public void testCompareMarkupWithDifferentText() throws Exception {
    result = compare("markup/pattern-source.html", "markup/data-source.html",
        ImmutableMap.of("compareType", "markup"));
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void testCompareMarkupWithDifferentAttributeName() throws Exception {
    result = compare("markup/pattern-source.html",
        "markup/data-source-with-different-attribute-value.html",
        ImmutableMap.of("compareType", "markup"));
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void shouldRecognizeLineEndings() throws Exception {
    //When
    result = compare("formatting/empty-lines-windows.html",
        "formatting/empty-lines-linux.html",
        ImmutableMap.of("compareType", "all"));
    //Then
    assertEquals(Status.FAILED, result.getStatus());
  }

  @Test
  public void shouldIgnoreLineEndingsForAllFormatted() throws Exception {
    //When
    result = compare("formatting/empty-lines-windows.html",
        "formatting/empty-lines-linux.html",
        ImmutableMap.of("compareType", "allformatted"));
    //Then
    assertEquals(Status.PASSED, result.getStatus());
  }

  @Test
  public void shouldIgnoreWhitespacesForAllFormatted() throws Exception {
    //When
    result = compare("formatting/formatted.html",
        "formatting/not-formatted.html",
        ImmutableMap.of("compareType", "allformatted"));
    //Then
    assertEquals(Status.PASSED, result.getStatus());
  }
}
