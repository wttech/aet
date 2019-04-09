/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.comparators.requestmonitoring;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.requestmonitoring.utils.RequestMonitoringResults;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class RequestMonitoringComparator implements ComparatorJob {

  public static final String COMPARATOR_TYPE = "request-monitoring";

  public static final String COMPARATOR_NAME = "request-monitoring";

  public static final String MAX_SIZE = "maxSize";

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestMonitoringComparator.class);

  private static final Type TYPE = new TypeToken<RequestMonitoringResults>() {
  }.getType();

  private final ComparatorProperties comparatorProperties;

  private final ArtifactsDAO artifactsDAO;

  private Long maxSize;

  RequestMonitoringComparator(ComparatorProperties comparatorProperties,
      ArtifactsDAO artifactsDAO) {
    this.comparatorProperties = comparatorProperties;
    this.artifactsDAO = artifactsDAO;
  }

  @Override
  public ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult result;

    LOGGER.info("Starting Request Monitoring with parameters {}.", comparatorProperties);

    try {
      RequestMonitoringResults collectedResult = getCollectedResult();
      String artifactId = artifactsDAO
          .saveArtifactInJsonFormat(comparatorProperties, collectedResult);

      if (maxSize != null) {
        result = new ComparatorStepResult(artifactId,
            collectedResult.getTotalSize() > maxSize ? ComparatorStepResult.Status.FAILED
                : ComparatorStepResult.Status.PASSED);
      } else {
        result = new ComparatorStepResult(artifactId, ComparatorStepResult.Status.PASSED);
      }


    } catch (Exception e) {
      throw new ProcessingException("Failed to get Request Size Comparator result!", e);
    }

    LOGGER.info("Finished Request Monitoring with parameters {}.", comparatorProperties);

    return result;
  }

  private RequestMonitoringResults getCollectedResult() throws IOException {
    return artifactsDAO
        .getJsonFormatArtifact(comparatorProperties, comparatorProperties.getCollectedId(), TYPE);
  }

  private Long getMaxSize(Map<String, String> parameters) {
    Long result = null;
    if (parameters != null) {
      String maxSizeString = parameters.get(MAX_SIZE);
      if (StringUtils.isNotEmpty(maxSizeString)) {
        result = Long.valueOf(maxSizeString);
      }
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    maxSize = getMaxSize(params);
  }
}
