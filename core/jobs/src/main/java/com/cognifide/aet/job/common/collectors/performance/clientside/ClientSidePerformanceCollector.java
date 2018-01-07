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
package com.cognifide.aet.job.common.collectors.performance.clientside;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.JsRuntimeWrapper;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.browsermob.core.har.Har;
import org.osgi.framework.BundleContext;

public class ClientSidePerformanceCollector implements CollectorJob {

  public static final String NAME = "client-side-performance";

  private static final String RESOURCE_PKG = ClientSidePerformanceCollector.class.getPackage()
      .getName()
      .replace('.', '/');

  private final ArtifactsDAO artifactsDAO;

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final CollectorProperties collectorProperties;

  private final BundleContext bundleContext;

  public ClientSidePerformanceCollector(ArtifactsDAO artifactsDAO,
      WebCommunicationWrapper webCommunicationWrapper,
      CollectorProperties collectorProperties, BundleContext bundleContext) {

    this.artifactsDAO = artifactsDAO;
    this.webCommunicationWrapper = webCommunicationWrapper;
    this.collectorProperties = collectorProperties;
    this.bundleContext = bundleContext;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    final JsRuntimeWrapper jsRuntimeWrapper = new JsRuntimeWrapper(bundleContext);
    prepareJsRuntime(jsRuntimeWrapper);
    String result = jsRuntimeWrapper.executeBundledScript(RESOURCE_PKG + "/yslow-runner.js");
    if (StringUtils.isBlank(result)) {
      throw new ProcessingException("Response from yslow is empty");
    }
    String artifactId = artifactsDAO.saveArtifact(collectorProperties, result);
    return CollectorStepResult.newCollectedResult(artifactId);
  }

  private void prepareJsRuntime(JsRuntimeWrapper jsRuntimeWrapper) throws ProcessingException {
    ProxyServerWrapper proxyServer = webCommunicationWrapper.getProxyServer();
    if (proxyServer != null) {
      Har har = proxyServer.getHar();
      StringWriter writer = new StringWriter();
      try {
        har.writeTo(writer);
      } catch (IOException e) {
        throw new ProcessingException(e.getMessage(), e);
      }
      String harJson = writer.toString();

      jsRuntimeWrapper.putBundledFilePathAsProperty("yslow_path", RESOURCE_PKG + "/yslow.js");
      jsRuntimeWrapper.putBundledFilePathAsProperty("env_path", RESOURCE_PKG + "/env.rhino.1.2.js");
      jsRuntimeWrapper.putBundledFilePathAsProperty("blank_path", RESOURCE_PKG + "/blank.html");
      jsRuntimeWrapper.putJsonProperty("har_content", harJson);
    } else {
      throw new ProcessingException("Can't check client-side performance without using proxy!");
    }
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //no parameters needed
  }
}
