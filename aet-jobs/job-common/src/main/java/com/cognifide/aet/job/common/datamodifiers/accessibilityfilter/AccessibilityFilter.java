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
package com.cognifide.aet.job.common.datamodifiers.accessibilityfilter;

import com.cognifide.aet.job.api.datamodifier.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityCollectorResult;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class AccessibilityFilter extends AbstractDataModifierJob<AccessibilityCollectorResult> {

	public static final String NAME = "accessibility-filter";

	private static final String PARAM_PRINCIPLE = "principle";

	private static final String PARAM_LINE = "line";

	private static final String PARAM_COLUMN = "column";

	private static final String PARAM_ERROR = "error";

	private String errorMessage;

	private String principle;

	private Integer line;

	private Integer column;

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		errorMessage = params.get(PARAM_ERROR);
		principle = params.get(PARAM_PRINCIPLE);
		line = getIntegerParam(params, PARAM_LINE);
		column = getIntegerParam(params, PARAM_COLUMN);
		validateParameters(principle, errorMessage, line, column);
	}

	@Override
	public AccessibilityCollectorResult modifyData(AccessibilityCollectorResult data) throws ProcessingException {
		AccessibilityCollectorResult result = data;
		for (AccessibilityIssue issue : result.getAccessibilityIssues()) {
			if (matchStrings(principle, issue.getCode())
					&& matchStrings(errorMessage, issue.getMessage())
					&& matchNumbers(line, issue.getLineNumber())
					&& matchNumbers(column, issue.getColumnNumber())) {
				issue.exclude();
			}
		}
		return result;
	}

	@Override
	public String getInfo() {
		return NAME + " DataModifier with parameters: " + PARAM_PRINCIPLE + ": " + principle + " "
				+ PARAM_ERROR + ": " + errorMessage + " " + PARAM_LINE + ": " + line + " " + PARAM_COLUMN + ": " + column;
	}

	private Integer getIntegerParam(Map<String, String> params, String param) throws ParametersException {
		Integer result = null;
		if (params.containsKey(param)) {
			try {
				result = Integer.parseInt(params.get(param));
			} catch (NumberFormatException e) {
				throw new ParametersException("Provided " + param + ": " + params.get(param) + " is not a numeric value.", e);
			}
		}

		return result;
	}

	private void validateParameters(String principle, String errorMessege, Integer line, Integer column)
			throws ParametersException {
		if (errorMessege == null && principle == null && line == null && column == null) {
			throw new ParametersException("At least one parameter must be provided");
		}
	}

	private boolean matchStrings(String paramValue, String errorValue) {
		return StringUtils.isEmpty(paramValue) || paramValue.equalsIgnoreCase(errorValue);
	}

	private boolean matchNumbers(Integer paramValue, int errorValue) {
		return paramValue == null || paramValue.intValue() == errorValue;
	}

}
