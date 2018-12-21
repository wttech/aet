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
package com.cognifide.aet.runner.processing.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SuiteComparatorTest {

  private static final String ID_1 = "1";

  private static final String ID_2 = "2";

  private static final String NAME_1 = "name1";

  private static final String NAME_2 = "name2";

  private static final String NAME_3 = "name3";

  @Mock
  private Suite suite1;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test1;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test2;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test3;

  @Mock
  private Suite suite2;

  @Before
  public void setup() {
    when(suite1.getCorrelationId()).thenReturn(ID_1);
    when(suite2.getCorrelationId()).thenReturn(ID_2);

    when(test1.getName()).thenReturn(NAME_1);
    when(test2.getName()).thenReturn(NAME_2);
    when(test3.getName()).thenReturn(NAME_3);
  }

  @Test
  public void shouldReturnNoDifferences_WhenNoTestsInSuites() {
    when(suite1.getTests()).thenReturn(null);
    when(suite2.getTests()).thenReturn(Collections.emptyList());

    List<String> comparisonMessages = SuiteComparator.compare(Sets.newHashSet(suite1, suite2));

    assertThat(comparisonMessages, hasSize(0));
  }

  @Test
  public void shouldReturnNoDifferences_WhenSameTestsInSuites() {
    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Collections.singletonList(test1));

    List<String> comparisonMessages = SuiteComparator.compare(Sets.newHashSet(suite1, suite2));

    assertThat(comparisonMessages, hasSize(0));
  }

  @Test
  public void shouldReturnDifferences_WhenNoTestsInOneSuite() {
    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Collections.emptyList());

    List<String> comparisonMessages = SuiteComparator.compare(Sets.newHashSet(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifferences_WhenMoreTestsInOneSuite() {
    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Arrays.asList(test2, test3));

    List<String> comparisonMessages = SuiteComparator.compare(Sets.newHashSet(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifferences_WhenDifferentTestsInSuites() {
    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Collections.singletonList(test2));

    List<String> comparisonMessages = SuiteComparator.compare(Sets.newHashSet(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }
}