/**
 * AET
 * <p>
 * Author: pnad@github
 * used code - Copyright (C) 2017 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.executejavascript;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import java.util.Map;

@Component
@Service
public class ExecuteJavaScriptModifierFactory implements CollectorFactory {

  @Reference
  private ExternalSnippetHttpClient httpClient;

  @Override
  public String getName() {
    return ExecuteJavaScriptModifier.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
                                     WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {
    ExecuteJavaScriptModifier modifier = new ExecuteJavaScriptModifier(webCommunicationWrapper.getWebDriver(), httpClient);
    modifier.setParameters(parameters);
    return modifier;
  }

}
