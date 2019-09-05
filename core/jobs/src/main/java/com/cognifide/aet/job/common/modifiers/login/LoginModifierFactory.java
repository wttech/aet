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
package com.cognifide.aet.job.common.modifiers.login;

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
public class LoginModifierFactory implements CollectorFactory {

  @Reference
  private ValidationResultBuilderFactory validationResultBuilderFactory;

  @Override
  public String getName() {
    return LoginModifier.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
      WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {
    LoginModifier modifier = new LoginModifier(webCommunicationWrapper);
    modifier.setParameters(parameters);
    return modifier;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Login")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Login")
                            .tag("login")
                            .withValues().and().defaultValue("admin")
                            .isMandatory(false)
                            .description("User's login")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Password")
                            .tag("password")
                            .withValues().and().defaultValue("admin")
                            .isMandatory(false)
                            .description("User's password")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Login Page")
                            .tag("login-page")
                            .withoutValues()
                            .isMandatory(true)
                            .description("Url to login page")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Login Input Selector")
                            .tag("login-input-selector")
                            .withValues().and().defaultValue("//input[@name='j_username']")
                            .isMandatory(false)
                            .description("Xpath expression for login input")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Login Password Selector")
                            .tag("login-password-selector")
                            .withValues().and().defaultValue("//input[@name='j_password']")
                            .isMandatory(false)
                            .description("Xpath expression for password input")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Submit Button Selector")
                            .tag("submit-button-selector")
                            .withValues().and().defaultValue("//*[@type='submit']")
                            .isMandatory(false)
                            .description("Xpath expression for submit button")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Login Token Key")
                            .tag("login-token-key")
                            .withValues().and().defaultValue("login-token")
                            .isMandatory(false)
                            .description("Name for cookie we get after successful login")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Timeout")
                            .tag("timeout")
                            .withValues().and().defaultValue("5000ms")
                            .isMandatory(false)
                            .description("Number of milliseconds (between 0 and 10000) that modifier will wait to login page response after submiting credentials. It is also used between reattempts to log in.")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Force Login")
                            .tag("force-login")
                            .withValues().and().defaultValue("1000ms")
                            .isMandatory(false)
                            .description("Enforces login even when login cookie is present.")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Retrial Number")
                            .tag("retrial-number")
                            .withValues().and().defaultValue("3")
                            .isMandatory(false)
                            .description("Number of reattempts to log in. It's a way to deal with unpredictable problem with logging in.")
                            .build()
            )
            .and()
            .withoutDeps()
            .dropTo("Collectors")
            .group("Modifiers")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/LoginModifier")
            .build();
  }
}
