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

import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.config.ReporterStep;
import com.cognifide.aet.communication.api.config.TestRun;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Model which stores report creation job description (configuration).
 */
public class ReporterJobData implements Serializable {

	private static final long serialVersionUID = -1928808824759800177L;

	private final List<NodeMetadata> resultNodeMetadatas;

	private final ReporterStep reporterStep;

	private final Map<String, TestRun> expectedResults;

	/**
	 * @param resultNodeMetadatas - list of node keys with comparison results.
	 * @param reporterStep - reporter step configuration.
	 * @param expectedResults - map with expected results test -> list of urls
	 */
	public ReporterJobData(List<NodeMetadata> resultNodeMetadatas, ReporterStep reporterStep,
			Map<String, TestRun> expectedResults) {
		this.resultNodeMetadatas = resultNodeMetadatas;
		this.reporterStep = reporterStep;
		this.expectedResults = expectedResults;
	}

	/**
	 * @return list of node keys with comparison results.
	 */
	public List<NodeMetadata> getResultNodeMetadatas() {
		return resultNodeMetadatas;
	}

	/**
	 * @return reporter step configuration.
	 */
	public ReporterStep getReporterStep() {
		return reporterStep;
	}

	public Map<String, TestRun> getExpectedResults() {
		return expectedResults;
	}
}
