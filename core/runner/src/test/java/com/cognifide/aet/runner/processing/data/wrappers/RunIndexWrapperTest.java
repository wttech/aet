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
package com.cognifide.aet.runner.processing.data.wrappers;

import static com.cognifide.aet.runner.processing.data.wrappers.RunIndexWrapper.cleanUrlFromExecutionData;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Operation;
import com.cognifide.aet.communication.api.metadata.Statistics;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.wrappers.Run;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RunIndexWrapperTest {

  private RunIndexWrapper runIndexWrapper;

  @Mock
  private Run objectToRunWrapper;

  @Mock
  private Suite suite;

  private Optional<com.cognifide.aet.communication.api.metadata.Test> test;

  private Optional<Url> url;

  @Mock
  private Step step;

  @Before
  public void setUp() {
    when(objectToRunWrapper.getRealSuite()).thenReturn(suite);
    runIndexWrapper = new SuiteRunIndexWrapper(objectToRunWrapper);
    test = Optional
        .of(new com.cognifide.aet.communication.api.metadata.Test("testName", "proxy", "chrome"));
    url = Optional.of(new Url("urlName", "urlUrl", "urlDomain"));
    test.get().addUrl(url.get());
  }

  @Test
  public void cleanUrlFromExecutionData_whenUrlIsRerunning_expectClearedUrl() {
    Url realUrl = new Url("urlName", "urlUrl", "urlDomain");

    realUrl.setCollectionStats(new Statistics(10));
    realUrl.addStep(step);

    Comparator comparator = new Comparator("comparatorType");
    comparator.setStepResults(Collections.singletonList(
        new ComparatorStepResult("comparatorStepResultName", Status.PASSED)));
    ArrayList<Operation> listOperation = new ArrayList<>();
    listOperation.add(new Operation("operationType"));
    comparator.setFilters(listOperation);

    Set<Comparator> comparators = new HashSet<>();
    comparators.add(comparator);

    when(step.getComparators()).thenReturn(comparators);

    cleanUrlFromExecutionData(realUrl);

    assertEquals(realUrl.getName(), "urlName");
    assertEquals(realUrl.getUrl(), "urlUrl");
    assertEquals(realUrl.getDomain(), "urlDomain");
    assertNull(realUrl.getCollectionStats());
    verify(step, times(1)).setStepResult(null);
    assertNull(comparator.getStepResults());
    assertEquals(comparator.getFilters().size(), 1);
  }

  @Test
  public void cleanUrlFromExecutionData_whenUrlIsClear_expectTheSameUrl() {
    Url realUrl = new Url("urlName", "urlUrl", "urlDomain");
    realUrl.setCollectionStats(null);
    realUrl.addStep(step);

    Comparator comparator = new Comparator("comparatorType");
    Set<Comparator> comparators = new HashSet<>();
    ArrayList<Operation> listOperation = new ArrayList<>();
    listOperation.add(new Operation("operationType"));
    comparator.setFilters(listOperation);
    comparators.add(comparator);

    when(step.getComparators()).thenReturn(comparators);
    cleanUrlFromExecutionData(realUrl);

    assertEquals(realUrl.getName(), "urlName");
    assertEquals(realUrl.getUrl(), "urlUrl");
    assertEquals(realUrl.getDomain(), "urlDomain");
    assertNull(realUrl.getCollectionStats());
    assertEquals(realUrl.getSteps().size(), 1);
    verify(step, times(1)).setStepResult(null);
    assertNull(comparator.getStepResults());
    assertEquals(comparator.getFilters().size(), 1);
  }

  @Test
  public void getTest_whenSuiteHasNotTest_expectNull() {
    when(suite.getTest("testName")).thenReturn(null);
    when(objectToRunWrapper.getRealSuite()).thenReturn(suite);
    assertNull(runIndexWrapper.getTest("testName"));
  }

  @Test
  public void getTest_whenSuiteHasTest_expectTest() {
    when(suite.getTest("testName")).thenReturn(test);
    assertThat(runIndexWrapper.getTest("testName"), is(test));
  }

  @Test
  public void getTestUrl_whenSuiteHasNotTest_expectNull() {
    when(suite.getTest("testName")).thenReturn(Optional.ofNullable(null));
    when(objectToRunWrapper.getRealSuite()).thenReturn(suite);
    assertFalse(runIndexWrapper.getTestUrl("testName", "urlName").isPresent());
  }

  @Test
  public void getTestUrl_whenTestHasNotUrl_expectNull() {
    Set<Url> urls = new HashSet<>();
    test.get().setUrls(urls);
    when(suite.getTest("testName")).thenReturn(test);
    when(objectToRunWrapper.getRealSuite()).thenReturn(suite);
    assertFalse(runIndexWrapper.getTestUrl("testName", "urlName").isPresent());
  }

  @Test
  public void getTestUrl_whenTestHasUrl_expectOptionalOfUrl() {
    when(suite.getTest("testName")).thenReturn(test);
    assertThat(runIndexWrapper.getTestUrl("testName", "urlName"), is(url));
  }

}