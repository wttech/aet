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
package com.cognifide.aet.job.common.comparators.accessibility;


import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReport;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ComparatorResult;
import com.cognifide.aet.vs.ResultStatus;

public class AccessibilityComparatorResult extends ComparatorResult {

	private static final long serialVersionUID = 4059206971791843863L;

	private final AccessibilityReport report;

	public AccessibilityComparatorResult(ResultStatus status, AccessibilityReport comparatorReport,
			ComparatorProperties properties) {
		super(status, properties, false);
		this.report = comparatorReport;
	}

	public AccessibilityReport getReport() {
		return report;
	}
}
