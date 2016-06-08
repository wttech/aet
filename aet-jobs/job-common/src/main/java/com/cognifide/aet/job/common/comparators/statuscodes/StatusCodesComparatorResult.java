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

import com.cognifide.aet.job.common.collectors.statuscodes.StatusCode;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ComparatorResult;
import com.cognifide.aet.vs.ResultStatus;

import java.util.List;

public class StatusCodesComparatorResult extends ComparatorResult {

	private static final long serialVersionUID = -1569735246456015656L;

	private final List<StatusCode> statusCodes;

	private final List<StatusCode> filteredStatusCodes;

	private final List<StatusCode> excludedStatusCodes;

	public StatusCodesComparatorResult(ResultStatus status, ComparatorProperties properties,
			List<StatusCode> statusCodes, List<StatusCode> filteredStatusCodes,
			List<StatusCode> excludedStatusCodes) {
		super(status, properties, false);
		this.statusCodes = statusCodes;
		this.filteredStatusCodes = filteredStatusCodes;
		this.excludedStatusCodes = excludedStatusCodes;
	}

	public List<StatusCode> getStatusCodes() {
		return statusCodes;
	}

	public List<StatusCode> getFilteredStatusCodes() {
		return filteredStatusCodes;
	}

	public List<StatusCode> getExcludedStatusCodes() {
		return excludedStatusCodes;
	}

}
