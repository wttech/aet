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
package com.cognifide.aet.job.common.collectors.source;


import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.HttpRequestExecutor;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class SourceCollector implements CollectorJob {

  public static final String NAME = "source";
  private static final String CONTENT_TYPE = "text/html";
  private static final String CHAR_ENCODING = "UTF-8";

  private final ArtifactsDAO artifactsDAO;

  private final HttpRequestExecutor httpRequestExecutor;

  private final CollectorProperties properties;

  private final int timeoutValue;

  public SourceCollector(ArtifactsDAO artifactsDAO, CollectorProperties collectorProperties,
      HttpRequestExecutor httpRequestExecutor, int timeoutValue) {
    this.artifactsDAO = artifactsDAO;
    this.properties = collectorProperties;
    this.httpRequestExecutor = httpRequestExecutor;
    this.timeoutValue = timeoutValue;
  }

  @Override
  public final CollectorStepResult collect() throws ProcessingException {
    CollectorStepResult stepResult;
    InputStream dataInputStream = null;
    try {
      byte[] content = getContent();
      final String pageSource = new String(content, CHAR_ENCODING);
      if (StringUtils.isBlank(pageSource)) {
        throw new ProcessingException("Page source is empty!");
      }
      dataInputStream = IOUtils.toInputStream(pageSource, CHAR_ENCODING);
      String resultId = artifactsDAO.saveArtifact(properties, dataInputStream, CONTENT_TYPE);
      stepResult = CollectorStepResult.newCollectedResult(resultId);
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(dataInputStream);
    }
    return stepResult;
  }

  private byte[] getContent() throws ProcessingException {
    try {
      return httpRequestExecutor.executeGetRequest(properties.getUrl(), timeoutValue).getContent();
    } catch (IOException e) {
      throw new ProcessingException(e.getMessage(), e);
    }
  }

  @Override
  public void setParameters(final Map<String, String> params) throws ParametersException {
    // no parameters needed
  }

}
