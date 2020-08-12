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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.runner.processing.data.UrlPacket;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestRunIndexWrapperTest {

  private TestRunIndexWrapper testRunIndexWrapper;

  @Mock
  private Run objectToRunWrapper;

  @Mock
  private Suite suite;

  private Optional<com.cognifide.aet.communication.api.metadata.Test> test;

  private Optional<Url> url;

  private Optional<Url> url2;

  @Before
  public void setUp() throws Exception {
    test = Optional
        .of(new com.cognifide.aet.communication.api.metadata.Test("testName", "proxy", "chrome"));
    url = Optional.of(new Url("urlName", "urlUrl", "urlDomain"));
    url2 = Optional.of(new Url("urlName2", "urlUrl2", "urlDomain2"));
    testRunIndexWrapper = new TestRunIndexWrapper(objectToRunWrapper);
    when(objectToRunWrapper.getRealSuite()).thenReturn(suite);
    when(suite.getTest(any(String.class))).thenReturn(test);
    when(objectToRunWrapper.getObjectToRun()).thenReturn(test.get());
  }

  @Test
  public void getUrlPackets_expectTwo() {
    prepareTwoUrls();
    List<UrlPacket> packets = testRunIndexWrapper.getUrlPackets();
    Long urlsCount = packets.stream().map(UrlPacket::getUrls).mapToLong(List::size).sum();
    assertThat(urlsCount, is(2L));
  }

  @Test
  public void getUrlPackets_expectZero() {
    List<UrlPacket> packets = testRunIndexWrapper.getUrlPackets();
    Long urlsCount = packets.stream().map(UrlPacket::getUrls).mapToLong(List::size).sum();
    assertThat(urlsCount, is(0L));
  }

  @Test
  public void countUrls_expectZero() {
    assertThat(testRunIndexWrapper.countUrls(), is(0));
  }

  @Test
  public void countUrls_expectTwo() {
    prepareTwoUrls();
    assertThat(testRunIndexWrapper.countUrls(), is(2));
  }

  private void prepareTwoUrls() {
    Set<Url> urls = new HashSet<>();
    urls.add(url.get());
    urls.add(url2.get());
    test.get().setUrls(urls);
  }
}