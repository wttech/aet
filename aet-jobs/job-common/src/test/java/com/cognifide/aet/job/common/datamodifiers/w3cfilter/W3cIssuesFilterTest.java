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
package com.cognifide.aet.job.common.datamodifiers.w3cfilter;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.w3c.W3cComparatorResult;
import com.cognifide.aet.job.common.comparators.w3c.W3cIssue;
import com.cognifide.aet.job.common.comparators.w3c.W3cIssueType;
import com.cognifide.aet.vs.ResultStatus;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class W3cIssuesFilterTest {

	public static final String PARAM_LINE = "line";

	public static final String PARAM_COLUMN = "column";

	public static final String PARAM_MESSAGE = "message";

	private W3cIssuesFilter tested;

	Map<String, String> params;

	@Before
	public void setUp() {
		tested = new W3cIssuesFilter();
		params = new HashMap<String, String>();
	}

	@Test
	public void setParameters_whenAllCorect_thenNoValidationErrors() throws ParametersException {
		params = getParams("1", "3", "Some message...");

		tested.setParameters(params);
	}

	@Test(expected = ParametersException.class)
	public void setParameters_whenAllEmpty_thenValidationErrors() throws ParametersException {
		tested.setParameters(params);

	}

	@Test(expected = ParametersException.class)
	public void setParameters_whenLineNonNumeric_thenValidationErrors()
			throws ParametersException, ProcessingException {
		params = getParams("xyz", "3", "Some message...");
		tested.setParameters(params);

	}

	@Test
	public void modifyData_whenLineMatch_issueIsRemoved() throws ParametersException, ProcessingException {
		params = getParams("1", null, null);
		tested.setParameters(params);
		W3cComparatorResult data = prepareComparatorResult(1, 0, "Message...", W3cIssueType.ERR);

		W3cComparatorResult processedData = tested.modifyData(data);

		assertThat(processedData.getErrorsCount(), is(0));
		assertThat(processedData.getIssues().size(), is(0));
		assertThat(processedData.getExcludedIssues().size(), is(1));
	}

	@Test
	public void modifyData_whenColumnMatch_issueIsRemoved() throws ParametersException, ProcessingException {
		params = getParams(null, "3", null);
		tested.setParameters(params);
		W3cComparatorResult data = prepareComparatorResult(0, 3, "Message...", W3cIssueType.WARN);

		W3cComparatorResult processedData = tested.modifyData(data);

		assertThat(processedData.getWarningsCount(), is(0));
		assertThat(processedData.getIssues().size(), is(0));
		assertThat(processedData.getExcludedIssues().size(), is(1));
	}

	@Test
	public void modifyData_whenMessageMatch_issueIsRemovedAndStatusIsSuccess()
			throws ParametersException, ProcessingException {
		params = getParams(null, null, "Message");
		tested.setParameters(params);
		W3cComparatorResult data = prepareComparatorResult(0, 0, "Message...", W3cIssueType.WARN);

		W3cComparatorResult processedData = tested.modifyData(data);

		assertThat(processedData.getWarningsCount(), is(0));
		assertThat(processedData.getIssues().size(), is(0));
		assertThat(processedData.getExcludedIssues().size(), is(1));
		assertThat(processedData.getStatus(), is(ResultStatus.SUCCESS));
	}

	@Test
	public void modifyData_whenIssuesExcluded_statusIsSuccess()
			throws ParametersException, ProcessingException {
		params = getParams("1", null, null);
		tested.setParameters(params);
		List<W3cIssue> issues = Lists.newArrayList(
				new W3cIssue(1, 2, "Message", null, null, null, null, W3cIssueType.WARN),
				new W3cIssue(1, 3, "Different message", null, null, null, null, W3cIssueType.ERR));
		W3cComparatorResult data = prepareComparatorResult(issues, Lists.<W3cIssue>newArrayList());

		W3cComparatorResult processedData = tested.modifyData(data);

		assertThat(processedData.getStatus(), is(ResultStatus.SUCCESS));
	}

	@Test
	public void modifyData_whenPartiallyMatch_issueIsNotRemoved()
			throws ParametersException, ProcessingException {
		params = getParams("1", "3", "Message");
		tested.setParameters(params);
		W3cComparatorResult data = prepareComparatorResult(2, 3, "Message...", W3cIssueType.WARN);

		W3cComparatorResult processedData = tested.modifyData(data);

		assertThat(processedData.getWarningsCount(), is(1));
		assertThat(processedData.getIssues().size(), is(1));
		assertThat(processedData.getExcludedIssues().size(), is(0));
	}

	@Test
	public void modifyData_whenManyCalls_excludedIssuesArePreserved()
			throws ParametersException, ProcessingException {
		params = getParams("1", "3", "Message");
		tested.setParameters(params);
		List<W3cIssue> issues = Lists.newArrayList(
				new W3cIssue(2, 3, "Message...", null, null, null, null, W3cIssueType.ERR),
				new W3cIssue(4, 3, "Warning...", null, null, null, null, W3cIssueType.WARN));
		List<W3cIssue> excludedIssues = Lists.newArrayList(
				new W3cIssue(2, 3, "Message...", null, null, null, null, W3cIssueType.WARN));
		W3cComparatorResult data = prepareComparatorResult(issues, excludedIssues);

		// first call
		data = tested.modifyData(data);

		assertThat(data.getWarningsCount(), is(1));
		assertThat(data.getErrorsCount(), is(1));
		assertThat(data.getIssues().size(), is(2));
		assertThat(data.getExcludedIssues().size(), is(1));
		assertThat(data.getStatus(), is(ResultStatus.FAILED));

		params = getParams("2", "3", "Message");
		tested.setParameters(params);

		// second call
		data = tested.modifyData(data);

		assertThat(data.getWarningsCount(), is(1));
		assertThat(data.getErrorsCount(), is(0));
		assertThat(data.getIssues().size(), is(1));
		assertThat(data.getExcludedIssues().size(), is(2));
		assertThat(data.getStatus(), is(ResultStatus.WARNING));
	}

	private Map<String, String> getParams(String line, String column, String message) {
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(line)) {
			params.put(PARAM_LINE, line);
		}
		if (StringUtils.isNotBlank(column)) {
			params.put(PARAM_COLUMN, column);
		}
		if (StringUtils.isNotBlank(message)) {
			params.put(PARAM_MESSAGE, message);
		}
		return params;
	}

	private W3cComparatorResult prepareComparatorResult(int line, int column, String message,
														W3cIssueType type) {
		List<W3cIssue> issues = new ArrayList<W3cIssue>();
		issues.add(new W3cIssue(line, column, message, null, null, null, null, type));
		List<W3cIssue> excludedIssues = new ArrayList<W3cIssue>();
		return prepareComparatorResult(issues, excludedIssues);
	}

	private W3cComparatorResult prepareComparatorResult(List<W3cIssue> issues,
														List<W3cIssue> excludedIssues) {
		int errorCount = 0;
		int warningCount = 0;
		for (W3cIssue issue : issues) {
			W3cIssueType type = issue.getIssueType();
			if (W3cIssueType.ERR.equals(type)) {
				errorCount++;
			}
			if (W3cIssueType.WARN.equals(type) || W3cIssueType.INFO.equals(type)) {
				warningCount++;
			}
		}
		ResultStatus status = ResultStatus.SUCCESS;
		if (errorCount > 0) {
			status = ResultStatus.FAILED;
		} else if (warningCount > 0) {
			status = ResultStatus.WARNING;
		}
		W3cComparatorResult data = new W3cComparatorResult(status, null, errorCount, warningCount,
				"someUrl", issues, excludedIssues);
		return data;
	}

}
