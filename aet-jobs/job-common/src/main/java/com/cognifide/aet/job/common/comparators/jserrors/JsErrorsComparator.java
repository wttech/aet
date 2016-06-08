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
package com.cognifide.aet.job.common.comparators.jserrors;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.jserrors.JsErrorsCollectorResult;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author lukasz.wieczorek
 */
public class JsErrorsComparator implements ComparatorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsErrorsComparator.class);

	public static final String COMPARATOR_TYPE = "js-errors";

	public static final String COMPARATOR_NAME = "js-errors";

	private final Node resultNode;

	private final Node dataNode;

	private final ComparatorProperties comparatorProperties;

	private final List<DataModifierJob> dataModifierJobs;

	public JsErrorsComparator(Node dataNode, Node resultNode, ComparatorProperties comparatorProperties,
			List<DataModifierJob> dataModifierJobs) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
		this.dataModifierJobs = dataModifierJobs;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		try {
			JsErrorsCollectorResult dataResult = dataNode.getResult(JsErrorsCollectorResult.class);
			for (DataModifierJob<JsErrorsCollectorResult> dataModifierJob : dataModifierJobs) {
				LOGGER.info(
						"Starting {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
				dataResult = dataModifierJob.modifyData(dataResult);
				LOGGER.info(
						"Successfully ended data modifications using {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
			}
			ResultStatus status = ResultStatus.fromBoolean(dataResult.getJsErrorLogs().isEmpty());
			JsErrorsComparatorResult result = new JsErrorsComparatorResult(status, comparatorProperties,
					dataResult.getJsErrorLogs());
			resultNode.saveResult(result);
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return NO_COMPARISON_RESULT;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		// no parameters needed
	}

}
