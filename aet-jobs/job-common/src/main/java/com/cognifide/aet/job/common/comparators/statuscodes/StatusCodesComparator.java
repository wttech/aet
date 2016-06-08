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
package com.cognifide.aet.job.common.comparators.statuscodes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cognifide.aet.job.common.Excludable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCode;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCodesCollectorResult;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @author magdalena.biala
 * 
 */
public class StatusCodesComparator implements ComparatorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatusCodesComparator.class);

	private static final int DEFAULT_FILTER_RANGE_UPPER_BOUND = 600;

	public static final String COMPARATOR_TYPE = "status-codes";

	public static final String COMPARATOR_NAME = "status-codes";

	private static final String PARAM_FILTER_RANGE = "filterRange";

	private static final String PARAM_FILTER_CODES = "filterCodes";

	private static final String PARAM_DELIMITER = ",";

	private static final String PARAM_SHOW_EXCLUDED = "showExcluded";

	protected final Node resultNode;

	protected final Node dataNode;

	private final ComparatorProperties comparatorProperties;

	private int filterRangeLowerBound;

	private int filterRangeUpperBound = DEFAULT_FILTER_RANGE_UPPER_BOUND;

	private List<Integer> filterCodes = Lists.newArrayList();

	private boolean showExcluded = true;

	private final List<DataModifierJob> dataModifierJobs;

	public StatusCodesComparator(Node dataNode, Node resultNode, ComparatorProperties comparatorProperties,
			List<DataModifierJob> dataModifierJobs) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
		this.dataModifierJobs = dataModifierJobs;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		try {
			StatusCodesCollectorResult dataResult = dataNode.getResult(StatusCodesCollectorResult.class);

			for (DataModifierJob<StatusCodesCollectorResult> dataModifierJob : dataModifierJobs) {
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

			List<StatusCode> statusCodes = dataResult.getStatusCodes();
			StatusCodesFilter statusCodesFilter = new StatusCodesFilter(filterRangeLowerBound,
					filterRangeUpperBound, filterCodes);

			Map<String, StatusCode> deduplicatedStatusCodes = new LinkedHashMap<>();
			for (StatusCode code : statusCodes) {
				deduplicatedStatusCodes.put(code.getUrl(), code);
			}

			List<StatusCode> filteredStatusCodes = statusCodesFilter.filter(Lists
					.newArrayList(deduplicatedStatusCodes.values()));

			List<StatusCode> notExcludedStatusCodes = Lists.newLinkedList();
			notExcludedStatusCodes.addAll(Collections2.filter(filteredStatusCodes,
					new Excludable.NonExcludedPredicate()));

			List<StatusCode> excludedStatusCodes = Lists.newLinkedList();
			if (showExcluded) {
				excludedStatusCodes.addAll(Collections2.filter(filteredStatusCodes,
						new Excludable.ExcludedPredicate()));
			}

			ResultStatus status = ResultStatus.fromBoolean(notExcludedStatusCodes.isEmpty());
			StatusCodesComparatorResult result = new StatusCodesComparatorResult(status,
					comparatorProperties, statusCodes, notExcludedStatusCodes, excludedStatusCodes);

			resultNode.saveResult(result);
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return NO_COMPARISON_RESULT;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(PARAM_FILTER_RANGE)) {
			processFilterRangeParam(params.get(PARAM_FILTER_RANGE));
		}
		if (params.containsKey(PARAM_FILTER_CODES)) {
			processFilterCodesParam(params.get(PARAM_FILTER_CODES));
		}
		if (params.containsKey(PARAM_SHOW_EXCLUDED)) {
			this.showExcluded = Boolean.valueOf(params.get(PARAM_SHOW_EXCLUDED));
		}
	}

	private void processFilterRangeParam(String param) throws ParametersException {
		String[] split = StringUtils.split(param, PARAM_DELIMITER);
		if (split.length != 2) {
			throw new ParametersException(
					"Filter Range Param is not configured correctly. Correct example: filterRange=\"200,400\"");
		}
		try {
			filterRangeLowerBound = Integer.parseInt(split[0]);
			filterRangeUpperBound = Integer.parseInt(split[1]);
			if (filterRangeUpperBound < filterRangeLowerBound) {
				throw new ParametersException("Filter upper bound is smaller than lower bound");
			}
		} catch (NumberFormatException e) {
			throw new ParametersException(
					"Failed to parse parameter to integer value. Please provide correct values", e);
		}
	}

	private void processFilterCodesParam(String param) throws ParametersException {
		String[] split = StringUtils.split(param, PARAM_DELIMITER);
		for (String code : split) {
			try {
				filterCodes.add(Integer.parseInt(code));
			} catch (NumberFormatException e) {
				throw new ParametersException(
						"Failed to parse parameter to integer value. Please provide correct values", e);
			}
		}
	}

}
