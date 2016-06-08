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
import java.util.List;

import com.cognifide.aet.communication.api.config.CollectorStep;
import com.cognifide.aet.communication.api.node.CollectMetadata;

/**
 * Model which stores collection job description (configuration).
 */
public class CollectorJobData implements Serializable {

	private static final long serialVersionUID = 1083076341023963219L;

	private final List<CollectMetadata> urlNodeMetadataList;

	private final List<CollectorStep> collectorSteps;

	private final String testName;

	private final String useProxy;

	/**
	 * @param urlNodeMetadataList - list of node keys of urls for collector job.
	 * @param collectorSteps - list of collector steps performed during collection.
	 * @param testName - name of the test that collection job is part of.
	 * @param useProxy - says what kind of proxy should be used, backward compatibility: set 'true' to use
	 * embedded, set 'false' to use none.
	 */
	public CollectorJobData(List<CollectMetadata> urlNodeMetadataList, List<CollectorStep> collectorSteps,
			String testName, String useProxy) {
		this.urlNodeMetadataList = urlNodeMetadataList;
		this.collectorSteps = collectorSteps;
		this.testName = testName;
		this.useProxy = useProxy;
	}

	/**
	 * @return name of kind of proxy that will be used.
	 */
	public String getUseProxy() {
		return useProxy;
	}

	/**
	 * @return name of the test that collection job is part of.
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * @return list of collector steps performed during collection.
	 */
	public List<CollectorStep> getCollectorSteps() {
		return collectorSteps;
	}

	/**
	 * @return list of node keys of urls for collector job.
	 */
	public List<CollectMetadata> getUrlNodeMetadataList() {
		return urlNodeMetadataList;
	}
}
