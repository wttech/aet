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
package com.cognifide.aet.job.common.comparators.statuscodes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.cognifide.aet.job.common.collectors.statuscodes.StatusCode;
import com.google.common.collect.Lists;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
public class StatusCodesFilterTest {

  private static final String EXAMPLE_URL = "http://www.example.com";

  private StatusCodesFilter tested;

  private List<StatusCode> statusCodes;

  @Before
  public void setUp() {
    statusCodes = prepareStatusCodes();
  }

  @Test
  public void testFilter_emptyList() {
    statusCodes = Lists.newArrayList();

    tested = getTested(0, 600, new ArrayList<Integer>());

    tested.filter(statusCodes);

    assertThat(statusCodes, notNullValue());
    assertThat(statusCodes, is((empty())));
  }

  @Test(expected = NullPointerException.class)
  public void testFilter_nullList() {
    statusCodes = null;

    tested = getTested(0, 600, new ArrayList<Integer>());

    tested.filter(statusCodes);
  }

  @TestWith({"0,18", "100,18", "101,17", "199,16", "200,15", "300,12", "400,9", "500,3",})
  public void testFilter_lowerBound(int lowerBound, int expectedSize) {
    tested = getTested(lowerBound, 600, new ArrayList<Integer>());

    List<StatusCode> result = tested.filter(statusCodes);

    assertThat(result.size(), is(expectedSize));
  }

  @TestWith({"0,0", "100,1", "101,2", "199,3", "200,4", "300,7", "400,10", "500,16",})
  public void testFilter_upperBound(int upperBound, int expectedSize) {
    tested = getTested(0, upperBound, new ArrayList<Integer>());

    List<StatusCode> result = tested.filter(statusCodes);

    assertThat(result.size(), is(expectedSize));
  }

  @TestWith({"0,600,18", "100,500,16", "400,600,9"})
  public void testFilter_bounds(int lowerBound, int upperBound, int expectedSize) {
    tested = getTested(lowerBound, upperBound, new ArrayList<Integer>());

    List<StatusCode> result = tested.filter(statusCodes);

    assertThat(result.size(), is(expectedSize));
  }

  @TestWith({"100;200;201,18", "400;401;499,18", "404,18"})
  public void testFilter_filterCodes(String filterCodesParam, int expectedSize) {
    tested = getTested(0, 600, prepareFilterCodes(filterCodesParam));

    List<StatusCode> result = tested.filter(statusCodes);

    assertThat(result.size(), is(expectedSize));
  }

  @TestWith({"100;200;201,100,200,5", "400;401;499,100,400,12"})
  public void testFilter_boundsWithFilterCodes(String filterCodesParam, int lowerBound,
      int upperBound,
      int expectedSize) {
    tested = getTested(lowerBound, upperBound, prepareFilterCodes(filterCodesParam));

    List<StatusCode> result = tested.filter(statusCodes);

    assertThat(result.size(), is(expectedSize));
  }

  private StatusCodesFilter getTested(int loweBound, int upperBound, List<Integer> filterCodes) {
    return new StatusCodesFilter(loweBound, upperBound, filterCodes);
  }

  private List<Integer> prepareFilterCodes(String filterCodesParam) {
    String[] split = StringUtils.split(filterCodesParam, ";");
    List<Integer> filterCodes = Lists.newArrayList();
    for (String item : split) {
      filterCodes.add(Integer.parseInt(item));
    }
    return filterCodes;
  }

  private List<StatusCode> prepareStatusCodes() {
    List<StatusCode> statusCodes = Lists.newArrayList();
    statusCodes.add(prepareStatusCode(100));
    statusCodes.add(prepareStatusCode(101));
    statusCodes.add(prepareStatusCode(199));
    statusCodes.add(prepareStatusCode(200));
    statusCodes.add(prepareStatusCode(201));
    statusCodes.add(prepareStatusCode(299));
    statusCodes.add(prepareStatusCode(300));
    statusCodes.add(prepareStatusCode(301));
    statusCodes.add(prepareStatusCode(399));
    statusCodes.add(prepareStatusCode(400));
    statusCodes.add(prepareStatusCode(404));
    statusCodes.add(prepareStatusCode(404));
    statusCodes.add(prepareStatusCode(404));
    statusCodes.add(prepareStatusCode(401));
    statusCodes.add(prepareStatusCode(499));
    statusCodes.add(prepareStatusCode(500));
    statusCodes.add(prepareStatusCode(501));
    statusCodes.add(prepareStatusCode(599));
    return statusCodes;
  }

  private StatusCode prepareStatusCode(int code) {
    return new StatusCode(code, EXAMPLE_URL);
  }
}
