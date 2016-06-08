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
package com.cognifide.aet.job.common.comparators.analytics;

import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ComparatorResult;
import com.cognifide.aet.vs.ResultStatus;

import java.util.List;

public class AnalyticsComparatorResult extends ComparatorResult {

	private static final long serialVersionUID = 1311997691855632L;

	private final List<AnalyticsRequest> requests;

	public AnalyticsComparatorResult(ResultStatus status, ComparatorProperties properties,
			List<AnalyticsRequest> requests) {
		super(status, properties, false);
		this.requests = requests;
	}

	public List<AnalyticsRequest> getRequests() {
		return requests;
	}
}
