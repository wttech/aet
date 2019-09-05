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
package com.cognifide.aet.job.common.modifiers.executejavascript;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import java.util.Map;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
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
    ExecuteJavaScriptModifier modifier = new ExecuteJavaScriptModifier(
        httpClient, new JavaScriptJobExecutor(webCommunicationWrapper.getWebDriver()));
    modifier.setParameters(parameters);
    return modifier;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("ExecuteJavaScript")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Command")
                            .tag("cmd")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Javascript command that will be executed, not mandatory if snippetUrl is set")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Snippet URL")
                            .tag("snippetUrl")
                            .withoutValues()
                            .isMandatory(false)
                            .description("The url to external js snippet that will be executed, not mandatory if cmd is set")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Basic Auth Username")
                            .tag("basicAuthUsername")
                            .withoutValues()
                            .isMandatory(false)
                            .description("For Basic HTTP Authentication header, only if basicAuthPassword is set")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Basic Auth Password")
                            .tag("basicAuthPassword")
                            .withoutValues()
                            .isMandatory(false)
                            .description("For Basic HTTP Authentication header, only if basicAuthUsername is set")
                            .build()
            )
            .and()
            .withoutDeps()
            .dropTo("Collectors")
            .group("Modifiers")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/ExecuteJavaScriptModifier")
            .build();
  }

}
