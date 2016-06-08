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
package com.cognifide.aet.job.common.modifiers.header;

import java.util.Map;

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;

public class HeaderModifier implements CollectorJob {

	public static final String NAME = "header";

	private static final String KEY = "key";

	private static final String VALUE = "value";

	private final WebCommunicationWrapper webCommunicationWrapper;

	private String key;

	private String value;

	public HeaderModifier(WebCommunicationWrapper webCommunicationWrapper) {
		this.webCommunicationWrapper = webCommunicationWrapper;
	}

	@Override
	public boolean collect() throws ProcessingException {
		if (!webCommunicationWrapper.isUseProxy()) {
			throw new ProcessingException("Cannot modify header without using proxy");
		}
		webCommunicationWrapper.getProxyServer().addHeader(key, value);
		webCommunicationWrapper.getHttpRequestBuilder().addHeader(key, value);
		return false;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(KEY) && params.containsKey(VALUE)) {
			key = params.get(KEY);
			value = params.get(VALUE);
		} else {
			throw new ParametersException("Missing Key or Value on Header Modifier");
		}
	}

}
