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

public class AnalyticsQuery implements Serializable{

	private static final long serialVersionUID = -2525089879606775992L;

	private final String key;

	private final String value;

	private final boolean isMarked;

	public AnalyticsQuery(String key, String value, boolean isMarked) {
		this.key = key;
		this.value = value;
		this.isMarked = isMarked;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean isMarked() {
		return isMarked;
	}
}
