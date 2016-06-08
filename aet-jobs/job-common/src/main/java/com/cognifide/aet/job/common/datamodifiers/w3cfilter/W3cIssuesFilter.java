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

import java.util.*;

import com.cognifide.aet.job.common.comparators.w3c.W3cIssueComparator;
import com.cognifide.aet.job.common.comparators.w3c.W3cIssueType;
import com.cognifide.aet.vs.ResultStatus;
import org.apache.commons.lang3.StringUtils;

import com.cognifide.aet.job.api.datamodifier.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.w3c.W3cComparatorResult;
import com.cognifide.aet.job.common.comparators.w3c.W3cIssue;

public class W3cIssuesFilter extends AbstractDataModifierJob<W3cComparatorResult> {

	public static final String NAME = "w3c-filter";

	private static final String PARAM_MESSAGE = "message";

	private static final String PARAM_LINE = "line";

	private static final String PARAM_COLUMN = "column";
	private static final Comparator<? super W3cIssue> W3C_ISSUE_COMPARATOR = new W3cIssueComparator();

	private String message;

	private int line;

	private int column;

	@Override
	public W3cComparatorResult modifyData(W3cComparatorResult data) throws ProcessingException {
		int errorsCount = data.getErrorsCount();
		int warningsCount = data.getWarningsCount();
		List<W3cIssue> excluded = data.getExcludedIssues();
		List<W3cIssue> notExcluded = new ArrayList<>();

		for (W3cIssue issue : data.getIssues()) {
			if (match(issue)) {
				excluded.add(issue);
				if (W3cIssueType.ERR.equals(issue.getIssueType())) {
					errorsCount--;
				}
				if (W3cIssueType.WARN.equals(issue.getIssueType())
						|| W3cIssueType.INFO.equals(issue.getIssueType())) {
					warningsCount--;
				}
			} else {
				notExcluded.add(issue);
			}
		}
		ResultStatus status = ResultStatus.SUCCESS;
		if (errorsCount > 0) {
			status = ResultStatus.FAILED;
		} else if (warningsCount > 0) {
			status = ResultStatus.WARNING;
		}
		Collections.sort(excluded, W3C_ISSUE_COMPARATOR);
		return new W3cComparatorResult(status, data.getProperties(), errorsCount,
				warningsCount, data.getValidationOutputUrl(), notExcluded, excluded);
	}

	private boolean match(W3cIssue issue) {
		final boolean messageNotSetOrIgnored =
				StringUtils.isBlank(message) || StringUtils.startsWithIgnoreCase(issue.getMessage(), message);
		final boolean lineNotSetOrIgnored = line == 0 || line == issue.getLine();
		final boolean columnNotSetOrIgnored = column == 0 || column == issue.getColumn();
		return messageNotSetOrIgnored && lineNotSetOrIgnored && columnNotSetOrIgnored;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(PARAM_MESSAGE)) {
			message = params.get(PARAM_MESSAGE);
		}
		line = getNumericParam(PARAM_LINE, params);
		column = getNumericParam(PARAM_COLUMN, params);

		if (isParametersEmpty()) {
			throw new ParametersException("Parameters for w3c filter cannot be empty.");
		}
	}

	private int getNumericParam(String paramName, Map<String, String> params) throws ParametersException {
		int paramValue = 0;
		if (params.containsKey(paramName)) {
			try {
				paramValue = Integer.valueOf(params.get(paramName));
			} catch (NumberFormatException e) {
				throw new ParametersException(paramName + " parameter for w3c filter should be numeric.", e);
			}
		}
		return paramValue;
	}

	@Override
	public String getInfo() {
		return NAME + " DataModifier with parameters: " + PARAM_MESSAGE + ": '" + message + "' " + PARAM_LINE
				+ ": " + line + " " + PARAM_COLUMN + ": " + column;
	}

	private boolean isParametersEmpty() {
		return line == 0 && column == 0 && StringUtils.isBlank(message);
	}
}
