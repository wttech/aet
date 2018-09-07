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
package com.cognifide.aet.runner.processing.data.RunIndexWrappers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.wrappers.MetadataRunDecorator;
import com.cognifide.aet.communication.api.wrappers.Run;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SuiteRunIndexWrapperTest {

  private SuiteRunIndexWrapper suiteRunIndexWrapper;

  @Mock
  Run objectToRunWrapper;

  @Mock
  private Suite suite;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test2;

  @Mock
  private Url url;

  @Mock
  private Url url2;

  @Before
  public void setUp() throws Exception {
    suiteRunIndexWrapper = new SuiteRunIndexWrapper(objectToRunWrapper);
    when(objectToRunWrapper.getRealSuite()).thenReturn(suite);
    when(objectToRunWrapper.getObjectToRun()).thenReturn(suite);
  }

  @Test
  public void getUrls_expectZero() {
    prepareZeroUrls();

    ArrayList<MetadataRunDecorator> urlsResult = suiteRunIndexWrapper
        .getUrls();
    assertThat(urlsResult.size(), is(0));
  }

  @Test
  public void getUrls_expectThree() {
    prepareThreeUrls();

    ArrayList<MetadataRunDecorator> urlsResult = suiteRunIndexWrapper
        .getUrls();
    assertThat(urlsResult.size(), is(3));
  }

  @Test
  public void countUrls_expectZero(){
    prepareZeroUrls();

    assertThat(suiteRunIndexWrapper.countUrls(), is(0));
  }

  @Test
  public void countUrls_expectThree() {
    prepareThreeUrls();

    assertThat(suiteRunIndexWrapper.countUrls(), is(3));
  }

  private void prepareZeroUrls(){
    List<com.cognifide.aet.communication.api.metadata.Test> tests = new ArrayList<>();
    tests.add(test);
    tests.add(test2);
    when(suite.getTests()).thenReturn(tests);
  }

  private void prepareThreeUrls(){
    Set<Url> firstUrlsSet = new HashSet<>();
    firstUrlsSet.add(url);
    when(test.getUrls()).thenReturn(firstUrlsSet);

    Set<Url> secondUrlsSet = new HashSet<>();
    secondUrlsSet.add(url);
    secondUrlsSet.add(url2);
    when(test2.getUrls()).thenReturn(secondUrlsSet);

    List<com.cognifide.aet.communication.api.metadata.Test> tests = new ArrayList<>();
    tests.add(test);
    tests.add(test2);
    when(suite.getTests()).thenReturn(tests);
  }
}