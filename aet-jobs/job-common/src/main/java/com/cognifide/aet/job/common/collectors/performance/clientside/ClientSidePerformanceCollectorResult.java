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
package com.cognifide.aet.job.common.collectors.performance.clientside;

import com.cognifide.aet.vs.CollectorResult;

public class ClientSidePerformanceCollectorResult extends CollectorResult {

	private static final long serialVersionUID = -5967609521876771780L;

	private final String resultJson;

	public ClientSidePerformanceCollectorResult(String url, String resultJson) {
		super(url);
		this.resultJson = resultJson;
	}

	public String getResultJson() {
		return resultJson;
	}
}
