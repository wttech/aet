/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.collectors.source;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.ArtifactsDAO;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;

import java.util.Map;

@Component(metatype = true, description = "AET Source Collector Factory", label = "AET Source Collector Factory")
@Service
public class SourceCollectorFactory implements CollectorFactory {

  private static final String SOURCE_COLLECTOR_TIMEOUT_VALUE = "sourceCollectorTimeoutValue";

  private static final int DEFAULT_TIMEOUT = 20000;

  @Property(name = SOURCE_COLLECTOR_TIMEOUT_VALUE, label = "Timeout value for source collector [ms]", intValue = DEFAULT_TIMEOUT, description = "Timeout value for source collector [ms]")
  private int timeoutValue;

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  public String getName() {
    return SourceCollector.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters, WebCommunicationWrapper webCommunicationWrapper)
          throws ParametersException {
    return new SourceCollector(artifactsDAO, properties, webCommunicationWrapper.getHttpRequestExecutor(), timeoutValue);
  }

  @Activate
  public void activate(Map properties) {
    timeoutValue = PropertiesUtil.toInteger(properties.get(SOURCE_COLLECTOR_TIMEOUT_VALUE), DEFAULT_TIMEOUT);
  }

}
