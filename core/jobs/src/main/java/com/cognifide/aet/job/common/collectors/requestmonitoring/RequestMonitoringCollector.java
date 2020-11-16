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
package com.cognifide.aet.job.common.collectors.requestmonitoring;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.requestmonitoring.utils.RequestMonitoringResults;
import com.cognifide.aet.vs.ArtifactsDAO;
import org.apache.commons.lang3.StringUtils;
import org.browsermob.core.har.HarEntry;
import org.browsermob.core.har.HarLog;

import java.util.Map;

public class RequestMonitoringCollector implements CollectorJob {

  public static final String NAME = "request-monitoring";

  private static final String URL_PATTERN_PARAM = "urlPattern";

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final ArtifactsDAO artifactsDAO;

  private final CollectorProperties properties;

  private String urlPattern;

  public RequestMonitoringCollector(WebCommunicationWrapper webCommunicationWrapper,
      ArtifactsDAO artifactsDAO, CollectorProperties properties) {
    this.webCommunicationWrapper = webCommunicationWrapper;
    this.artifactsDAO = artifactsDAO;
    this.properties = properties;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    final CollectorStepResult stepResult;

    RequestMonitoringResults results = new RequestMonitoringResults();
    if (!webCommunicationWrapper.isUseProxy()) {
      throw new ProcessingException("Cannot collect request monitoring data without using proxy!");
    }
    HarLog log = webCommunicationWrapper.getProxyServer().getHar().getLog();

    for (final HarEntry harLogEntry : log.getEntries()) {
      String url = harLogEntry.getRequest().getUrl();
      if (url.matches(urlPattern)) {
        long bodySize = harLogEntry.getResponse().getBodySize();
        results.addItem(url, bodySize);
      }
    }

    final String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, results);
    stepResult = CollectorStepResult.newCollectedResult(artifactId);

    return stepResult;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    validateParams(params);
    urlPattern = params.get(URL_PATTERN_PARAM);
  }

  private void validateParams(Map<String, String> params) throws ParametersException {
    if (params == null || StringUtils.isEmpty(params.get(URL_PATTERN_PARAM))) {
      throw new ParametersException(
          "Please specify the 'urlPattern' (regexp) parameter for the Request Monitoring collector.");
    }
  }

}
