/*
 * Cognifide AET :: Job Common
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.comparators.statuscodes;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCode;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCodesCollectorResult;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.common.collect.Lists;

/**
 * This Unit test is responsible for validating more of component logic then actual filtering. Tests for
 * filter are written separately and can be found in StatusCodesFilterTest
 */
@RunWith(MockitoJUnitRunner.class)
public class StatusCodesComparatorTest {

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
	private Node dataNode;

	@Mock
	private Node resultNode;

	@Mock
	private StatusCodesCollectorResult dataResult;

	@Mock
	private ComparatorProperties comparatorProperties;

	private List<StatusCode> statusCodes;

	@Before
	public void setUp() throws VersionStorageException {

		tested = new StatusCodesComparator(dataNode, resultNode, comparatorProperties, Collections.<DataModifierJob>emptyList());
		initStatusCodes();
		when(dataNode.getResult(StatusCodesCollectorResult.class)).thenReturn(dataResult);
		when(dataResult.getStatusCodes()).thenReturn(statusCodes);

		when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(false);
		when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(false);
	}

	@Test
	public void compareTest() throws VersionStorageException, ProcessingException {
		// no filtering applied
		tested.compare();

		verify(resultNode, times(1)).saveResult(any(StatusCodesComparatorResult.class));
	}

	@Test
	public void compareTest_filterRange() throws VersionStorageException, ProcessingException {
		// filter only by range
		when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
		when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);

		tested.compare();

		verify(resultNode, times(1)).saveResult(any(StatusCodesComparatorResult.class));
	}

	@Test
	public void compareTest_filterCodes() throws VersionStorageException, ProcessingException {
		// filter only by codes
		when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
		when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);

		tested.compare();

		verify(resultNode, times(1)).saveResult(any(StatusCodesComparatorResult.class));
	}

	@Test(expected = ProcessingException.class)
	public void compareTest_storageExceptionOnDataFetch() throws VersionStorageException, ProcessingException {
		when(dataNode.getResult(StatusCodesCollectorResult.class)).thenThrow(
				new VersionStorageException("failed"));

		tested.compare();
	}

	@Test(expected = ProcessingException.class)
	public void compareTest_storageExceptionOnDataSave() throws VersionStorageException, ProcessingException {
		when(resultNode.saveResult(any(StatusCodesComparatorResult.class))).thenThrow(
				new VersionStorageException("failed"));

		tested.compare();
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
		statusCodes = new StatusCodesBuilder().add(404, 303, 500, 501, 302, 200, 400, 201)
				.add(404, "http://www.example.com/image.png").build();
	}

	private static class StatusCodesBuilder {

		private static final String DEFAULT_URL = "http://www.example.com";

		private final List<StatusCode> codes;

		private StatusCodesBuilder() {
			this.codes = Lists.newArrayList();
		}

		public StatusCodesBuilder add(int code) {
			codes.add(new StatusCode(code, DEFAULT_URL));
			return this;
		};

		public StatusCodesBuilder add(Integer... httpCodes) {
			for (Integer httpCode : httpCodes) {
				codes.add(new StatusCode(httpCode, DEFAULT_URL));
			}
			return this;
		};

		public StatusCodesBuilder add(int code, String url) {
			codes.add(new StatusCode(code, url));
			return this;
		};

		public List<StatusCode> build() {
			return codes;
		};

	}
}
