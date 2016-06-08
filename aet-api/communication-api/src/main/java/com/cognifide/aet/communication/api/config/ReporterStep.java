/*
 * Cognifide AET :: Communication API
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
package com.cognifide.aet.communication.api.config;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents single report generation step. Contains basic information about report generation.
 */
public class ReporterStep implements Serializable {

	private static final long serialVersionUID = -1067380516063753707L;

	private final String module;

	private final Map<String, String> parameters;

	/**
	 * @param module - unique name of module that will generate report basing on collected data and comparison
	 * results gathered during test.
	 * @param parameters - all additional parameters needed by report module.
	 */
	public ReporterStep(String module, Map<String, String> parameters) {
		this.module = module;
		this.parameters = parameters;
	}

	/**
	 * @return name of module that will generate report.
	 */
	public String getModule() {
		return module;
	}

	/**
	 * @return map with additional parameters needed by report module.
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

}
