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
package com.cognifide.aet.job.common.comparators.jserrors;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsErrorsComparator implements ComparatorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsErrorsComparator.class);

  public static final String COMPARATOR_TYPE = "js-errors";

  public static final String COMPARATOR_NAME = "js-errors";
  private static final Type RESULT_TYPE = new TypeToken<Set<JsErrorLog>>() {
  }.getType();

  private final ComparatorProperties comparatorProperties;

  private final List<DataFilterJob> dataFilterJobs;

  private final ArtifactsDAO artifactsDAO;

  JsErrorsComparator(ComparatorProperties comparatorProperties, List<DataFilterJob> dataFilterJobs,
      ArtifactsDAO artifactsDAO) {
    this.comparatorProperties = comparatorProperties;
    this.dataFilterJobs = dataFilterJobs;
    this.artifactsDAO = artifactsDAO;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult result;
    LOGGER.info("Starting JS Error Comparison with parameters {}.", comparatorProperties);
    try {
      Set<JsErrorLog> jsErrorLogs = getCollectedResult();
      for (DataFilterJob<Set<JsErrorLog>> dataFilterJob : dataFilterJobs) {
        jsErrorLogs = dataFilterJob.modifyData(jsErrorLogs);
      }
      LOGGER.info("Successfully ended data modifications using {}.", comparatorProperties);

      String artifactId = null;
      if (!jsErrorLogs.isEmpty()) {
        artifactId = artifactsDAO.saveArtifactInJsonFormat(comparatorProperties, jsErrorLogs);
      }

      if (noErrorsOrIgnoredOnly(jsErrorLogs)) {
        result = new ComparatorStepResult(artifactId, ComparatorStepResult.Status.PASSED);
      } else {
        result = new ComparatorStepResult(artifactId, ComparatorStepResult.Status.FAILED);
      }
    } catch (Exception e) {
      throw new ProcessingException("Failed to obtain Js Errors Collection Result!", e);
    }
    return result;
  }

  private boolean noErrorsOrIgnoredOnly(Set<JsErrorLog> jsErrorLogs) {
    return jsErrorLogs.stream().allMatch(JsErrorLog::isIgnored);
  }

  private Set<JsErrorLog> getCollectedResult() throws IOException {
    return artifactsDAO
        .getJsonFormatArtifact(comparatorProperties, comparatorProperties.getCollectedId(),
            RESULT_TYPE);
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    // no parameters needed
  }

}
