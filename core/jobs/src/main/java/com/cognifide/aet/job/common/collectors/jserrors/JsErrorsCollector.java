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
package com.cognifide.aet.job.common.collectors.jserrors;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.Map;
import java.util.Set;

public class JsErrorsCollector implements CollectorJob {

  public static final String NAME = "js-errors";

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final ArtifactsDAO artifactsDAO;

  private final CollectorProperties properties;

  JsErrorsCollector(CollectorProperties properties, WebCommunicationWrapper webCommunicationWrapper,
      ArtifactsDAO artifactsDAO) {

    this.properties = properties;
    this.webCommunicationWrapper = webCommunicationWrapper;
    this.artifactsDAO = artifactsDAO;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    CollectorStepResult stepResult;
    Set<JsErrorLog> jsErrorLogs = webCommunicationWrapper.getJSErrorLogs();
    final String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, jsErrorLogs);
    stepResult = CollectorStepResult.newCollectedResult(artifactId);

    return stepResult;
  }

  @Override
  public void setParameters(Map<String, String> params) {
    //no parameters needed
  }

}
