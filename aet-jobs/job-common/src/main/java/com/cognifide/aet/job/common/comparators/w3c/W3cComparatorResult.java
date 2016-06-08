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
package com.cognifide.aet.job.common.comparators.w3c;

import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ComparatorResult;
import com.cognifide.aet.vs.ResultStatus;

import java.util.List;

public class W3cComparatorResult extends ComparatorResult {

	private static final long serialVersionUID = -9145540068681858078L;

	private final int errorCount;

	private final int warningCount;

	private final String validationOutputUrl;

	private final List<W3cIssue> issues;

	private final List<W3cIssue> excludedIssues;

	public W3cComparatorResult(ResultStatus status, ComparatorProperties properties,
							   int errorCount, int warningCount, String validationOutputUrl, List<W3cIssue> issues,
							   List<W3cIssue> excludedIssues) {
		super(status, properties, false);
		this.errorCount = errorCount;
		this.warningCount = warningCount;
		this.validationOutputUrl = validationOutputUrl;
		this.issues = issues;
		this.excludedIssues = excludedIssues;
	}

	public int getErrorsCount() {
		return errorCount;
	}

	public int getWarningsCount() {
		return warningCount;
	}

	public String getValidationOutputUrl() {
		return validationOutputUrl;
	}

	public List<W3cIssue> getIssues() {
		return issues;
	}

	public List<W3cIssue> getExcludedIssues() {
		return excludedIssues;
	}
}
