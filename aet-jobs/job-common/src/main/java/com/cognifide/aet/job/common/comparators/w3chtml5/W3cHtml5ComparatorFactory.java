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
package com.cognifide.aet.job.common.comparators.w3chtml5;

import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.common.comparators.w3chtml5.parser.W3cHtml5ValidationResultParser;
import com.cognifide.aet.job.common.comparators.w3chtml5.wrapper.NuValidatorWrapper;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;

@Component
@Service
public class W3cHtml5ComparatorFactory implements ComparatorFactory {

	private final W3cHtml5ValidationResultParser resultParser = new W3cHtml5ValidationResultParser();

	@Override
	public String getType() {
		return W3cHtml5Comparator.COMPARATOR_TYPE;
	}

	@Override
	public String getName() {
		return W3cHtml5Comparator.COMPARATOR_NAME;
	}

	@Override
	public int getRanking() {
		return 70;
	}

	@Override
	public ComparatorJob createInstance(Node dataNode, Node patternNode, Node resultNode,
										Map<String, String> params, ComparatorProperties comparatorProperties,
										List<DataModifierJob> dataModifierJobs) throws ParametersException {
		W3cHtml5Comparator comparator = new W3cHtml5Comparator(dataNode, resultNode, comparatorProperties,
				new NuValidatorWrapper(), resultParser, dataModifierJobs);
		comparator.setParameters(params);
		return comparator;
	}
}
