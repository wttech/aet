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
package com.cognifide.aet.job.common.modifiers.cookie;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import com.cognifide.aet.validation.ValidationResultBuilderFactory;
import java.util.Map;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class CookieModifierFactory implements CollectorFactory {

  @Reference
  private ValidationResultBuilderFactory validationResultBuilderFactory;

  @Override
  public String getName() {
    return CookieModifier.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
      WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {
    CookieModifier cookieModifier = new CookieModifier(properties, webCommunicationWrapper,
        validationResultBuilderFactory.createInstance());
    cookieModifier.setParameters(parameters);
    return cookieModifier;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Cookie")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Action")
                            .tag("action")
                            .withValues()
                            .addValue("add")
                            .addValue("remove")
                            .and().defaultValue(null)
                            .isMandatory(true)
                            .description("Specifies what action should be taken with a given cookie")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Cookie Name")
                            .tag("cookie-name")
                            .withoutValues()
                            .isMandatory(true)
                            .description("Cookie name")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Cookie Value")
                            .tag("cookie-value")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Cookie Value, mandatory if 'add' action is selected")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Cookie Domain")
                            .tag("cookie-domain")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Cookie domain, used only if 'add' action is selected but it's not mandatory")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Cookie Path")
                            .tag("cookie-path")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Cookie path, used only if 'add' action is selected but it's not mandatory")
                            .build()
            )
            .and()
            .withoutDeps()
            .dropTo("Collectors")
            .group("Modifiers")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/CookieModifier")
            .build();
  }
}
