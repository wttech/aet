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
package com.cognifide.aet.job.common.comparators.permormance.clientside;

import java.util.Map;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.performance.clientside.ClientSidePerformanceCollectorResult;
import com.cognifide.aet.job.common.comparators.permormance.clientside.parser.ClientSidePerformanceComparatorResultData;
import com.cognifide.aet.job.common.comparators.permormance.clientside.parser.ClientSidePerformanceParser;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorageException;

public class ClientSidePerformanceComparator implements ComparatorJob {

	public static final String TYPE = "client-side-performance";

	public static final String NAME = "client-side-performance";

	private final Node dataNode;

	private final Node resultNode;

	private final ComparatorProperties comparatorProperties;

	private final ClientSidePerformanceParser clientSidePerformanceParser;

	public ClientSidePerformanceComparator(Node dataNode, Node resultNode,
			ClientSidePerformanceParser clientSidePerformanceParser,
			ComparatorProperties comparatorProperties) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.clientSidePerformanceParser = clientSidePerformanceParser;
		this.comparatorProperties = comparatorProperties;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		try {
			ClientSidePerformanceCollectorResult collectorResult = dataNode
					.getResult(ClientSidePerformanceCollectorResult.class);
			ClientSidePerformanceComparatorResultData comparatorResultData = clientSidePerformanceParser
					.parse(collectorResult.getResultJson());
			resultNode.saveResult(new ClientSidePerformanceComparatorResult(comparatorResultData, comparatorProperties));
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return NO_COMPARISON_RESULT;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		//no parameters needed
	}
}
