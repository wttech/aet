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

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.Node;

/**
 * @author lukasz.wieczorek
 */
@Component
@Service
public class JsErrorsCollectorFactory implements CollectorFactory {

	@Override
	public String getName() {
		return JsErrorsCollector.NAME;
	}

	@Override
	public CollectorJob createInstance(Node dataNode, Node patternNode, Map<String, String> params,
			WebCommunicationWrapper webCommunicationWrapper, CollectorProperties collectorProperties)
			throws ParametersException {
		JsErrorsCollector collector = new JsErrorsCollector(dataNode, webCommunicationWrapper,
				collectorProperties);
		collector.setParameters(params);
		return collector;
	}

}
