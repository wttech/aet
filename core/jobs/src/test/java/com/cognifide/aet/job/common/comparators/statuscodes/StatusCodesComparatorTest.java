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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.ArtifactDAOMock;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCode;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCodesCollectorResult;
import com.cognifide.aet.job.common.comparators.AbstractComparatorTest;
import com.cognifide.aet.job.common.comparators.source.SourceComparator;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * This Unit test is responsible for validating more of component logic then actual filtering. Tests
 * for filter are written separately and can be found in StatusCodesFilterTest
 */
@RunWith(MockitoJUnitRunner.class)
public class StatusCodesComparatorTest extends AbstractComparatorTest {

  private static final String PARAM_FILTER_RANGE = "filterRange";

  private static final String PARAM_FILTER_CODES = "filterCodes";

  private static final String FILTER_RANGE_UPPER_LESS_THAN_LOWER = "500,300";

  private static final String FILTER_RANGE_NAN = "not,a_number";

  private static final String FILTER_RANGE_INVALID = "cannot split";

  private static final String FILTER_RANGE = "300,500";

  private static final String FILTER_CODES_SINGLE = "302";

  private static final String FILTER_CODES_MULTIPLE = "302,303,404";

  private static final String FILTER_CODES_SINGLE_NAN = "not-a-number";

  private static final String FILTER_CODES_MULTIPLE_NAN = "NaN,NaN,NaN,NaN";

  private StatusCodesComparator tested;

  @Mock
  private Map<String, String> params;

  @Mock
  private ComparatorProperties comparatorProperties;

  private List<StatusCode> statusCodes;

  @Before
  public void setUp() {
    artifactDaoMock = new ArtifactDAOMock(StatusCodesComparator.class);
    comparatorProperties = new ComparatorProperties(TEST_COMPANY, TEST_PROJECT, null,
        "expected-data-200-result.json");
    tested = new StatusCodesComparator(artifactDaoMock, comparatorProperties, new ArrayList<>());
    initStatusCodes();

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(false);
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(false);
  }

  @Test
  public void compareTest() throws ProcessingException {
    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_filterRange() throws ProcessingException {
    // filter only by range
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);
    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_filterCodes() throws ProcessingException {
    // filter only by codes
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void setParameters() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_SINGLE);

    // no parameters errors expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test
  public void setParameters_filterRange() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);

    // no parameters errors expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterRangeInvalid() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_INVALID);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterRangeNotANumber() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_NAN);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterRangeUpperBoundLessThanLowerBound() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_UPPER_LESS_THAN_LOWER);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test
  public void setParameters_filterCodes() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_SINGLE);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test
  public void setParameters_filterCodesMultipleValues() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);

    // no parameters errors expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterCodesNotANumber() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_SINGLE_NAN);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterCodesMultipleValuesNotANumber() throws ParametersException {
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE_NAN);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  private void initStatusCodes() {
    statusCodes = new StatusCodesBuilder()
        .add(404, 303, 500, 501, 302, 200, 400, 201)
        .add(404, "http://www.example.com/image.png")
        .build();
  }

  private static class StatusCodesBuilder {

    private static final String DEFAULT_URL = "http://www.example.com";

    private final List<StatusCode> codes;

    private StatusCodesBuilder() {
      this.codes = Lists.newArrayList();
    }

    public StatusCodesBuilder add(Integer... httpCodes) {
      for (Integer httpCode : httpCodes) {
        codes.add(new StatusCode(httpCode, DEFAULT_URL));
      }
      return this;
    }

    public StatusCodesBuilder add(int code, String url) {
      codes.add(new StatusCode(code, url));
      return this;
    }

    public List<StatusCode> build() {
      return codes;
    }
  }
}
