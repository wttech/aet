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

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorageException;

import java.util.Map;
import java.util.Set;

/**
 * @author lukasz.wieczorek
 */
public class JsErrorsCollector implements CollectorJob {

	public static final String NAME = "js-errors";

	private final Node dataNode;

	private final WebCommunicationWrapper webCommunicationWrapper;

	private final String url;

	public JsErrorsCollector(Node dataNode, WebCommunicationWrapper webCommunicationWrapper,
			CollectorProperties collectorProperties) {
		this.dataNode = dataNode;
		this.webCommunicationWrapper = webCommunicationWrapper;
		this.url = collectorProperties.getUrl();
	}

	@Override
	public boolean collect() throws ProcessingException {
		Set<JsErrorLog> jsErrorLogs = webCommunicationWrapper.getJSErrorLogs();
		JsErrorsCollectorResult jsErrorsCollectorResult = new JsErrorsCollectorResult(url, jsErrorLogs);
		try {
			dataNode.saveResult(jsErrorsCollectorResult);
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public void setParameters(Map<String, String> params) {
		//no parameters needed
	}

}
