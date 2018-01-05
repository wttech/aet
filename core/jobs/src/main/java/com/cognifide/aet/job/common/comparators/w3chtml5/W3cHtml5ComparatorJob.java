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
package com.cognifide.aet.job.common.comparators.w3chtml5;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.List;
import java.util.Map;

public abstract class W3cHtml5ComparatorJob implements ComparatorJob {

  public static final String COMPARATOR_TYPE = "source";

  protected static final String PARAM_IGNORE_WARNINGS = "ignore-warnings";

  protected final ArtifactsDAO artifactsDAO;

  protected final ComparatorProperties properties;

  protected final List<DataFilterJob> dataFilterJobs;

  protected boolean ignoreWarnings = true;

  public W3cHtml5ComparatorJob(ArtifactsDAO artifactsDAO, ComparatorProperties properties,
      List<DataFilterJob> dataFilterJobs) {
    this.artifactsDAO = artifactsDAO;
    this.properties = properties;
    this.dataFilterJobs = dataFilterJobs;
  }

  @SuppressWarnings("unchecked")
  protected ComparatorStepResult getComparatorStepResult(
      W3cHtml5ComparatorResult w3cComparatorResult) throws
      ProcessingException {
    String resultId;
    ComparatorStepResult comparatorStepResult;
    W3cHtml5ComparatorResult filteredW3cComparatorResult = w3cComparatorResult;
    for (DataFilterJob<W3cHtml5ComparatorResult> dataFilterJob : dataFilterJobs) {
      filteredW3cComparatorResult = dataFilterJob.modifyData(filteredW3cComparatorResult);
    }

    resultId = artifactsDAO.saveArtifactInJsonFormat(properties, filteredW3cComparatorResult);
    if (filteredW3cComparatorResult.getErrorsCount() > 0) {
      comparatorStepResult = new ComparatorStepResult(resultId, ComparatorStepResult.Status.FAILED);
    } else if (!ignoreWarnings && filteredW3cComparatorResult.getWarningsCount() > 0) {
      comparatorStepResult = new ComparatorStepResult(resultId,
          ComparatorStepResult.Status.WARNING);
    } else {
      comparatorStepResult = new ComparatorStepResult(resultId, ComparatorStepResult.Status.PASSED);
    }
    comparatorStepResult
        .addData("errorCount", Integer.toString(filteredW3cComparatorResult.getErrorsCount()));
    comparatorStepResult
        .addData("warningCount", Integer.toString(filteredW3cComparatorResult.getWarningsCount()));
    return comparatorStepResult;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (params.containsKey(PARAM_IGNORE_WARNINGS)) {
      ignoreWarnings = Boolean.valueOf(params.get(PARAM_IGNORE_WARNINGS));
    }
  }
}
