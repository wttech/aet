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
package com.cognifide.aet.job.common.collectors.statuscodes;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.Map;
import org.browsermob.core.har.HarEntry;
import org.browsermob.core.har.HarLog;

public class StatusCodesCollector implements CollectorJob {

  public static final String NAME = "status-codes";

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final CollectorProperties collectorProperties;

  private final ArtifactsDAO artifactsDAO;

  public StatusCodesCollector(ArtifactsDAO artifactsDAO,
      WebCommunicationWrapper webCommunicationWrapper,
      CollectorProperties collectorProperties) {
    this.artifactsDAO = artifactsDAO;
    this.webCommunicationWrapper = webCommunicationWrapper;
    this.collectorProperties = collectorProperties;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    CollectorStepResult stepResult;
    try {
      if (!webCommunicationWrapper.isUseProxy()) {
        throw new ProcessingException("Cannot collect status codes without using proxy!");
      }
      HarLog harLog = webCommunicationWrapper.getProxyServer().getHar().getLog();

      final StatusCodesCollectorResult statusCodesCollectorResult = new StatusCodesCollectorResult(
          collectorProperties.getUrl());
      for (final HarEntry harEntry : harLog.getEntries()) {
        int status = harEntry.getResponse().getStatus();
        statusCodesCollectorResult.addStatusCode(harEntry.getRequest().getUrl(), status);
      }
      String resultId = artifactsDAO
          .saveArtifactInJsonFormat(collectorProperties, statusCodesCollectorResult);

      stepResult = CollectorStepResult.newCollectedResult(resultId);

    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return stepResult;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //no parameters needed
  }
}
