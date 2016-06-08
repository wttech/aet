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

import org.apache.commons.lang3.StringUtils;

public final class W3cIssueBuilder {

	private int line;

	private int column;

	private String message;

	private String code1 = StringUtils.EMPTY;

	private String code2 = StringUtils.EMPTY;

	private String errorPosition = StringUtils.EMPTY;

	private String additionalInfo;

	private W3cIssueType issueType;

	public W3cIssueBuilder setLine(String lineString) {
		if (StringUtils.isNumeric(lineString)) {
			this.line = Integer.valueOf(lineString);
		}
		return this;
	}

	public W3cIssueBuilder setColumn(String columnString) {
		if (StringUtils.isNumeric(columnString)) {
			this.column = Integer.valueOf(columnString);
		}
		return this;
	}

	public W3cIssueBuilder setMessage(String message) {
		this.message = message;
		return this;
	}

	public W3cIssueBuilder setCode1(String code1) {
		this.code1 = code1;
		return this;
	}

	public W3cIssueBuilder setCode2(String code2) {
		this.code2 = code2;
		return this;
	}

	public W3cIssueBuilder setErrorPosition(String errorPosition) {
		this.errorPosition = errorPosition;
		return this;
	}

	public W3cIssueBuilder setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}

	public W3cIssueBuilder setIssueType(W3cIssueType issueType) {
		this.issueType = issueType;
		return this;
	}

	public W3cIssue build() {
		return new W3cIssue(line, column, message, code1, code2, errorPosition, additionalInfo, issueType);
	}
}
