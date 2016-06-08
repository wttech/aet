/*
 * Cognifide AET :: Data Storage API
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
package com.cognifide.aet.vs;

import java.io.Serializable;

public abstract class ComparatorResult implements Result, Serializable {

	private static final long serialVersionUID = -5182863059653028881L;

	protected final ResultStatus status;

	protected final ComparatorProperties properties;

	protected final boolean hasPattern;

	/**
	 * @param status - status of results,
	 * @param properties - comparison properties,
	 * @param hasPattern - flag that says if comparator has pattern and can be rebased from report.
	 */
	public ComparatorResult(ResultStatus status, ComparatorProperties properties, boolean hasPattern) {
		this.status = status;
		this.properties = properties;
		this.hasPattern = hasPattern;
	}

	public ResultStatus getStatus() {
		return status;
	}

	public ComparatorProperties getProperties() {
		return properties;
	}
}
