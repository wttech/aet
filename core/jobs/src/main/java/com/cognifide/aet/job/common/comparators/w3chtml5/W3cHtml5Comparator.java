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
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.w3chtml5.parser.W3cHtml5ValidationResultParser;
import com.cognifide.aet.job.common.comparators.w3chtml5.wrapper.NuValidatorWrapper;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class W3cHtml5Comparator extends W3cHtml5ComparatorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(W3cHtml5Comparator.class);

  public static final String COMPARATOR_NAME = "w3c-html5";

  /**
   * Will be removed in 1.5.0 version
   *
   * @deprecated use parameter "ignore-warnings" instead
   */
  @Deprecated
  private static final String PARAM_ERRORS_ONLY = "errors-only";

  private final NuValidatorWrapper wrapper;

  private final W3cHtml5ValidationResultParser validationResultParser;

  W3cHtml5Comparator(ArtifactsDAO artifactsDAO, ComparatorProperties comparatorProperties,
      NuValidatorWrapper wrapper, W3cHtml5ValidationResultParser validationResultParser,
      List<DataFilterJob> dataFilterJobs) {
    super(artifactsDAO, comparatorProperties, dataFilterJobs);
    this.wrapper = wrapper;
    this.validationResultParser = validationResultParser;
  }

  @Override
  public ComparatorStepResult compare() throws ProcessingException {
    ComparatorStepResult comparatorStepResult;
    InputStream sourceStream = null;
    try {
      LOGGER.info("Checking w3c for artifact {} in {} {} using validator: {}.",
          properties.getCompany(),
          properties.getProject(), properties.getPatternId(), properties);
      sourceStream = artifactsDAO.getArtifact(properties, properties.getCollectedId())
          .getArtifactStream();

      String json = wrapper.validate(sourceStream);
      W3cHtml5ComparatorResult w3cComparatorResult = validationResultParser.parse(json);

      comparatorStepResult = getComparatorStepResult(w3cComparatorResult);
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(sourceStream);
    }
    return comparatorStepResult;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    super.setParameters(params);
    if (!params.containsKey(PARAM_IGNORE_WARNINGS) && params.containsKey(PARAM_ERRORS_ONLY)) {
      ignoreWarnings = Boolean.valueOf(params.get(PARAM_ERRORS_ONLY));
    }
    wrapper.setErrorsOnly(ignoreWarnings);
  }
}
