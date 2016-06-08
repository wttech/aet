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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AnalyticsRequest implements Serializable {

	private static final long serialVersionUID = -1923932254132821255L;

	private final Date datetime;

	private final List<AnalyticsQuery> queries;

	public AnalyticsRequest(Date datetime, List<AnalyticsQuery> queries) {
		// deep copy to avoid un-intentional modifications
		this.datetime = new Date(datetime.getTime());
		this.queries = queries;
	}

	public Date getDatetime() {
		return new Date(datetime.getTime());
	}

	public List<AnalyticsQuery> getQuaries() {
		return queries;
	}
}
