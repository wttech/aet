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
package com.cognifide.aet.job.common.collectors.jserrors;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.vs.CollectorResult;

import java.util.Set;

/**
 * @author lukasz.wieczorek
 */
public class JsErrorsCollectorResult extends CollectorResult {

	private static final long serialVersionUID = -3358103006410847294L;

	private final Set<JsErrorLog> jsErrorLogs;

	public JsErrorsCollectorResult(String url, Set<JsErrorLog> jsErrorLogs) {
		super(url);
		this.jsErrorLogs = jsErrorLogs;
	}

	public Set<JsErrorLog> getJsErrorLogs() {
		return jsErrorLogs;
	}

}
