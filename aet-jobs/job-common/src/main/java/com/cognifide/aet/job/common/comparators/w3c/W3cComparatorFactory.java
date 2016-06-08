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
package com.cognifide.aet.job.common.comparators.w3c;

import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.job.api.collector.HttpRequestBuilderFactory;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;

@Component(metatype = true, description = "AET W3C Comparator Factory", label = "AET W3C Comparator Factory")
@Service
public class W3cComparatorFactory implements ComparatorFactory {

	private static final String DEFAULT_PARAM_VALIDATOR = "http://w3c.qa.cognifide.com/w3c-validator/";

	private static final String PARAM_VALIDATOR = "validator";

	@Property(name = PARAM_VALIDATOR, label = "Validator url", description = "Url of the W3c validator", value = DEFAULT_PARAM_VALIDATOR)
	private String validatorUrl;

	@Reference
	private HttpRequestBuilderFactory httpRequestBuilderFactory;

	@Override
	public String getType() {
		return W3cComparator.COMPARATOR_TYPE;
	}

	@Override
	public String getName() {
		return W3cComparator.COMPARATOR_NAME;
	}

	@Override
	public int getRanking() {
		return 90;
	}

	@Activate
	public void activate(Map<String, String> properties) {
		validatorUrl = properties.get(PARAM_VALIDATOR);
	}

	@Override
	public ComparatorJob createInstance(Node dataNode, Node patternNode, Node resultNode,
			Map<String, String> params, ComparatorProperties comparatorProperties,
			List<DataModifierJob> dataModifierJobs) throws ParametersException {

		W3cComparator comparator = new W3cComparator(dataNode, resultNode, comparatorProperties,
				httpRequestBuilderFactory.createInstance(), new W3cResponseParser(), validatorUrl,dataModifierJobs);
		comparator.setParameters(params);
		return comparator;
	}

}
