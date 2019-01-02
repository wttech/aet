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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Url;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SuiteComparatorTest {

  private static final String ID_1 = "1";

  private static final String ID_2 = "2";

  private static final String NAME_1 = "name1";

  private static final String NAME_2 = "name2";

  private static final String NAME_3 = "name3";

  private static final String URL_1 = "url1";

  private static final String STEP_NAME_1 = "stepName1";

  private static final String COMPARATOR_TYPE_1 = "comparatorType1";

  private static final String COMPARATOR_TYPE_2 = "comparatorType2";

  private static final String STEP_TYPE_1 = "stepType1";

  private static final String STEP_TYPE_2 = "stepType2";

  private static final String URL_2 = "url2";

  @Mock
  private Suite suite1;

  @Mock
  private Suite suite2;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test1;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test2;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test3;

  @Mock
  private Url url1;

  @Mock
  private Url url2;

  @Mock
  private Step step1;

  @Mock
  private Step step2;

  @Mock
  private Comparator comparator1;

  @Mock
  private Comparator comparator2;

  @Before
  public void setup() {
    when(suite1.getCorrelationId()).thenReturn(ID_1);
    when(suite2.getCorrelationId()).thenReturn(ID_2);

    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Collections.singletonList(test2));

    when(test1.getName()).thenReturn(NAME_1);
    when(test2.getName()).thenReturn(NAME_2);
    when(test3.getName()).thenReturn(NAME_3);

    when(test1.getUrls()).thenReturn(Collections.singleton(url1));
    when(test2.getUrls()).thenReturn(Collections.singleton(url2));

    when(url1.getName()).thenReturn(URL_1);
    when(url2.getName()).thenReturn(URL_1);
    when(url1.getSteps()).thenReturn(Collections.singletonList(step1));
    when(url2.getSteps()).thenReturn(Collections.singletonList(step2));

    when(step1.getName()).thenReturn(STEP_NAME_1);
    when(step2.getName()).thenReturn(STEP_NAME_1);
    when(step1.getType()).thenReturn(STEP_TYPE_1);
    when(step2.getType()).thenReturn(STEP_TYPE_1);
    when(step1.getParameters()).thenReturn(Collections.emptyMap());
    when(step2.getParameters()).thenReturn(Collections.emptyMap());
    when(step1.getComparators()).thenReturn(Collections.singleton(comparator1));
    when(step2.getComparators()).thenReturn(Collections.singleton(comparator2));

    when(comparator1.getType()).thenReturn(COMPARATOR_TYPE_1);
    when(comparator2.getType()).thenReturn(COMPARATOR_TYPE_1);
    when(comparator1.getParameters()).thenReturn(Collections.emptyMap());
    when(comparator2.getParameters()).thenReturn(Collections.emptyMap());
  }

  @Test
  public void shouldReturnNoDifferences_WhenNoTestsInSuites() {
    when(suite1.getTests()).thenReturn(null);
    when(suite2.getTests()).thenReturn(Collections.emptyList());

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(0));
  }

  @Test
  public void shouldReturnNoDifferences_WhenSameTestsInSuites() {
    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Collections.singletonList(test1));

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(0));
  }

  @Test
  public void shouldReturnDifferences_WhenNoTestsInOneSuite() {
    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Collections.emptyList());

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifferences_WhenMoreTestsInOneSuite() {
    when(suite1.getTests()).thenReturn(Collections.singletonList(test1));
    when(suite2.getTests()).thenReturn(Arrays.asList(test2, test3));

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifferences_WhenDifferentTestsInSuites() {
    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnNoDifference_WhenAllStepsEqual() {
    when(test2.getName()).thenReturn(NAME_1);

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(0));
  }

  @Test
  public void shouldReturnDifference_WhenParamsInStepsDifferent() {
    when(test2.getName()).thenReturn(NAME_1);
    when(step1.getParameters()).thenReturn(ImmutableMap.of("key", "foo"));

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifference_WhenTypesInStepsDifferent() {
    when(test2.getName()).thenReturn(NAME_1);
    when(step1.getType()).thenReturn(STEP_TYPE_2);

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifference_WhenNoStepsInOneTestPresent() {
    when(test2.getName()).thenReturn(NAME_1);
    when(url1.getSteps()).thenReturn(null);

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifference_WhenDifferentUrlsInTests() {
    when(test2.getName()).thenReturn(NAME_1);
    when(url1.getName()).thenReturn(URL_2);

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifference_WhenNoComparatorsInOneStep() {
    when(test2.getName()).thenReturn(NAME_1);
    when(step2.getComparators()).thenReturn(null);

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifference_WhenDifferentComparatorTypesInSteps() {
    when(test2.getName()).thenReturn(NAME_1);
    when(comparator2.getType()).thenReturn(COMPARATOR_TYPE_2);

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifference_WhenDifferentComparatorParamsInSteps() {
    when(test2.getName()).thenReturn(NAME_1);
    when(comparator2.getParameters()).thenReturn(ImmutableMap.of("value", "foo"));

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }

  @Test
  public void shouldReturnDifference_WhenDifferentComparatorsCountInSteps() {
    when(test2.getName()).thenReturn(NAME_1);
    when(step1.getComparators()).thenReturn(Sets.newSet(comparator1, comparator2));

    List<String> comparisonMessages = SuiteComparator.compare(Arrays.asList(suite1, suite2));

    assertThat(comparisonMessages, hasSize(1));
  }
}