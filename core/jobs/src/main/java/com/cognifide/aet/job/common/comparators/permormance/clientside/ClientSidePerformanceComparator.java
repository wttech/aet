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
package com.cognifide.aet.job.common.comparators.permormance.clientside;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.permormance.clientside.parser.ClientSidePerformanceComparatorResultData;
import com.cognifide.aet.job.common.comparators.permormance.clientside.parser.ClientSidePerformanceParser;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.Map;

public class ClientSidePerformanceComparator implements ComparatorJob {

  public static final String TYPE = "client-side-performance";

  public static final String NAME = "client-side-performance";

  private final ArtifactsDAO artifactsDAO;

  private final ComparatorProperties comparatorProperties;

  private final ClientSidePerformanceParser clientSidePerformanceParser;

  public ClientSidePerformanceComparator(ArtifactsDAO artifactsDAO,
      ClientSidePerformanceParser clientSidePerformanceParser,
      ComparatorProperties comparatorProperties) {
    this.artifactsDAO = artifactsDAO;
    this.clientSidePerformanceParser = clientSidePerformanceParser;
    this.comparatorProperties = comparatorProperties;
  }

  @Override
  public ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult result;
    try {
      String collectedData = artifactsDAO
          .getArtifactAsString(comparatorProperties, comparatorProperties.getCollectedId());
      ClientSidePerformanceComparatorResultData comparatorResultData = clientSidePerformanceParser
          .parse(collectedData);

      String artifactId = artifactsDAO
          .saveArtifactInJsonFormat(comparatorProperties, comparatorResultData.getResult());
      result = new ComparatorStepResult(artifactId, comparatorResultData.getResult());

    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //no parameters needed
  }
}
