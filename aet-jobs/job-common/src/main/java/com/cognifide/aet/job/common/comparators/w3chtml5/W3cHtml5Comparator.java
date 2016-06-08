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

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.w3c.W3cComparatorResult;
import com.cognifide.aet.job.common.comparators.w3chtml5.parser.W3cHtml5ValidationResultParser;
import com.cognifide.aet.job.common.comparators.w3chtml5.wrapper.NuValidatorWrapper;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorageException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class W3cHtml5Comparator implements ComparatorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(W3cHtml5Comparator.class);

	public static final String COMPARATOR_TYPE = "source";

	public static final String COMPARATOR_NAME = "w3c-html5";

	private static final String PARAM_ERRORS_ONLY = "errors-only";

	private static final String DATA_ATTRIBUTE_NAME = "data";

	private final Node dataNode;

	private final Node resultNode;

	private final ComparatorProperties comparatorProperties;

	private final NuValidatorWrapper wrapper;

	private final W3cHtml5ValidationResultParser validationResultParser;

	private boolean errorsOnly = true;

	private final List<DataModifierJob> dataModifierJobs;

	public W3cHtml5Comparator(Node dataNode, Node resultNode, ComparatorProperties comparatorProperties,
							  NuValidatorWrapper wrapper, W3cHtml5ValidationResultParser validationResultParser,
							  List<DataModifierJob> dataModifierJobs) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
		this.wrapper = wrapper;
		this.validationResultParser = validationResultParser;
		this.dataModifierJobs = dataModifierJobs;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		InputStream sourceStream = null;
		try {
			sourceStream = getPageSource(dataNode);

			String json = wrapper.validate(sourceStream);

			W3cComparatorResult result = validationResultParser.parse(json, comparatorProperties);

			for (DataModifierJob<W3cComparatorResult> dataModifierJob : dataModifierJobs) {
				LOGGER.info(
						"Starting {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
				result = dataModifierJob.modifyData(result);
				LOGGER.info(
						"Successfully ended data modifications using {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
			}

			resultNode.saveResult(result);
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(sourceStream);
		}
		return NO_COMPARISON_RESULT;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(PARAM_ERRORS_ONLY)) {
			this.errorsOnly = BooleanUtils.toBoolean(params.get(PARAM_ERRORS_ONLY));
		}
		wrapper.setErrorsOnly(errorsOnly);
	}

	private InputStream getPageSource(Node node) throws VersionStorageException {
		@SuppressWarnings("unchecked")
		Map<String, String> data = node.getResult(Map.class);
		return node.getData(data.get(DATA_ATTRIBUTE_NAME));
	}
}
