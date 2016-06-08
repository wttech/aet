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
package com.cognifide.aet.communication.api.job;

import java.io.Serializable;

import com.cognifide.aet.communication.api.config.ComparatorStep;
import com.cognifide.aet.communication.api.node.CollectMetadata;

/**
 * Model which stores comparison job description (configuration).
 */
public class ComparatorJobData implements Serializable {

	private static final long serialVersionUID = -6846450349328407946L;

	private final CollectMetadata collectorNodeMetadata;

	private final ComparatorStep comparatorStep;

	/**
	 * @param collectorNodeMetadata - node key which points to collection results.
	 * @param comparatorStep - comparator step performed during comparison.
	 */
	public ComparatorJobData(CollectMetadata collectorNodeMetadata, ComparatorStep comparatorStep) {
		this.collectorNodeMetadata = collectorNodeMetadata;
		this.comparatorStep = comparatorStep;
	}

	/**
	 * @return node key which points to collection results.
	 */
	public CollectMetadata getCollectorNodeMetadata() {
		return collectorNodeMetadata;
	}

	/**
	 * @return comparator step performed during comparison.
	 */
	public ComparatorStep getComparatorStep() {
		return comparatorStep;
	}

}
