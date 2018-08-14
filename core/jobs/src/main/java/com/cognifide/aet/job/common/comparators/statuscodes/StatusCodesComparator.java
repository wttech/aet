/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.comparators.statuscodes;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.Excludable;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCode;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCodesCollectorResult;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusCodesComparator implements ComparatorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatusCodesComparator.class);

  private static final int DEFAULT_FILTER_RANGE_LOWER_BOUND = 400;

  private static final int DEFAULT_FILTER_RANGE_UPPER_BOUND = 600;

  public static final String COMPARATOR_TYPE = "status-codes";

  public static final String COMPARATOR_NAME = "status-codes";

  private static final String PARAM_FILTER_RANGE = "filterRange";

  private static final String PARAM_FILTER_CODES = "filterCodes";

  private static final String PARAM_DELIMITER = ",";

  private static final String PARAM_SHOW_EXCLUDED = "showExcluded";

  private static final Type RESULT_TYPE = new TypeToken<StatusCodesCollectorResult>() {
  }.getType();

  private final ArtifactsDAO artifactsDAO;

  private final ComparatorProperties properties;

  private int filterRangeLowerBound = DEFAULT_FILTER_RANGE_LOWER_BOUND;

  private int filterRangeUpperBound = DEFAULT_FILTER_RANGE_UPPER_BOUND;

  private List<Integer> filterCodes = Lists.newArrayList();

  private boolean showExcluded = true;

  private final List<DataFilterJob> dataFilterJobs;

  public StatusCodesComparator(ArtifactsDAO artifactsDAO, ComparatorProperties comparatorProperties,
      List<DataFilterJob> dataFilterJobs) {
    this.artifactsDAO = artifactsDAO;
    this.properties = comparatorProperties;
    this.dataFilterJobs = dataFilterJobs;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult result;
    LOGGER.info("Starting comparison phase for  status codes for Company: {} Project: {}",
        properties.getCompany(), properties.getProject());
    try {

      StatusCodesCollectorResult dataResult = artifactsDAO.getJsonFormatArtifact(properties,
          properties.getCollectedId(), RESULT_TYPE);

      for (DataFilterJob<StatusCodesCollectorResult> dataFilterJob : dataFilterJobs) {
        dataResult = dataFilterJob.modifyData(dataResult);
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
      Boolean isSuccess = notExcludedStatusCodes.isEmpty();

      StatusCodesComparatorResult comparatorResult = new StatusCodesComparatorResult(statusCodes,
          notExcludedStatusCodes, excludedStatusCodes);
      String resultId = artifactsDAO.saveArtifactInJsonFormat(properties, comparatorResult);

      result = new ComparatorStepResult(resultId,
          isSuccess ? ComparatorStepResult.Status.PASSED : ComparatorStepResult.Status.FAILED);

    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
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
