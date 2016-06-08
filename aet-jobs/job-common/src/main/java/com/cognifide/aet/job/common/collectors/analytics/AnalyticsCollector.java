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
package com.cognifide.aet.job.common.collectors.analytics;

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorageException;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

public class AnalyticsCollector implements CollectorJob {

	public static final String NAME = "analytics";

	private static final String PARAM_ANALYTICS_ENDPOINT = "analytics-endpoint";

	private final Node dataNode;

	private final WebCommunicationWrapper webCommunicationWrapper;

	private final CollectorProperties collectorProperties;

	private String analyticsEndpoint;

	public AnalyticsCollector(Node dataNode, WebCommunicationWrapper webCommunicationWrapper,
			CollectorProperties collectorProperties) {
		this.dataNode = dataNode;
		this.webCommunicationWrapper = webCommunicationWrapper;
		this.collectorProperties = collectorProperties;
	}

	@Override
	public boolean collect() throws ProcessingException {
		try {
			validateConfiguration();

			HarLog harLog = webCommunicationWrapper.getProxyServer().getHar().getLog();
			Collection<HarEntry> siteCatalystEntries = AnalyticsHarLogFilter.getSiteCatalystEntries(harLog,
					analyticsEndpoint);

			dataNode.saveResult(new AnalyticsCollectorResult(collectorProperties.getUrl(), siteCatalystEntries));
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(PARAM_ANALYTICS_ENDPOINT)) {
			analyticsEndpoint = params.get(PARAM_ANALYTICS_ENDPOINT).toString();
		}
	}

	private void validateConfiguration() throws ProcessingException {
		if (!webCommunicationWrapper.isUseProxy()) {
			throw new ProcessingException("Cannot collect status codes without using proxy!");
		}

		if (StringUtils.isEmpty(analyticsEndpoint)) {
			throw new ProcessingException("Analytics Endpoint is not specified!");
		}
	}

}
