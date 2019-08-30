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
package com.cognifide.aet.job.common.modifiers.replacetext;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.osgi.service.component.annotations.Component;

@Component
public class ReplaceTextFactory implements CollectorFactory {

  @Override
  public String getName() {
    return ReplaceTextModifier.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
      WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {
    WebDriver webDriver = webCommunicationWrapper.getWebDriver();
    ReplaceTextModifier modifier = new ReplaceTextModifier(webDriver, properties,
        new JavaScriptJobExecutor(webDriver));
    modifier.setParameters(parameters);
    return modifier;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Replace Text")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Value")
                            .tag("value")
                            .withValues().and().defaultValue("")
                            .isMandatory(false)
                            .description("New text value for given attribute or innterHTML of selected element")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Attribute Name")
                            .tag("attributeName")
                            .withValues().and().defaultValue("innerHTML")
                            .isMandatory(false)
                            .description("Attribute of selected element to be set, e.g. 'href' or 'value'")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("XPath")
                            .tag("xpath")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Xpath of element(s)")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("CSS")
                            .tag("css")
                            .withoutValues()
                            .isMandatory(false)
                            .description("css selector of element(s)")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Timeout")
                            .tag("timeout")
                            .withValues().and().defaultValue("1000ms")
                            .isMandatory(false)
                            .description("The timeout for the element to appear, in milliseconds. The max value of this parameter is 15000ms")
                            .build()
            )
            .and()
            .withoutDeps()
            .dropTo("Collectors")
            .group("Modifiers")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/ReplaceTextModifier")
            .build();
  }
}
