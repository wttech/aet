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

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.common.collectors.source.configuration.SourceCollectorFactoryConf;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

@Component
@Designate(ocd = SourceCollectorFactoryConf.class)
public class SourceCollectorFactory implements CollectorFactory {

  SourceCollectorFactoryConf config;

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  public String getName() {
    return SourceCollector.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
      WebCommunicationWrapper webCommunicationWrapper)
      throws ParametersException {
    return new SourceCollector(artifactsDAO, properties,
        webCommunicationWrapper.getHttpRequestExecutor(), config.sourceCollectorTimeoutValue());
  }

  @Activate
  public void activate(SourceCollectorFactoryConf config) {
    this.config = config;
  }

}
