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

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;
import java.util.Map;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class CookieModifier implements CollectorJob {

  public static final String NAME = "modify-cookie";

  protected static final String NAME_PARAMETER = "cookie-name";

  protected static final String VALUE_PARAMETER = "cookie-value";

  protected static final String DOMAIN_PARAMETER = "cookie-domain";

  protected static final String PATH_PARAMETER = "cookie-path";

  private static final String ACTION_PARAMETER = "action";

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final ValidationResultBuilder validationResultBuilder;

  private final CollectorProperties properties;

  private ModifyAction modifyAction;

  private String name;

  private String value;

  private String domain;

  private String path;

  CookieModifier(CollectorProperties properties, WebCommunicationWrapper webCommunicationWrapper,
      ValidationResultBuilder validationResultBuilder) {
    this.properties = properties;
    this.webCommunicationWrapper = webCommunicationWrapper;
    this.validationResultBuilder = validationResultBuilder;
  }

  private void addCookie(WebDriver webDriver) {
    webDriver.get(properties.getUrl());
    Cookie cookie = new Cookie(name, value, domain, path, null);
    webDriver.manage().addCookie(cookie);
    webCommunicationWrapper.getHttpRequestExecutor().addCookie(name, value);
  }

  private void removeCookie(WebDriver webDriver) {
    webDriver.manage().deleteCookieNamed(name);
    webCommunicationWrapper.getHttpRequestExecutor().removeCookie(name);
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    switch (modifyAction) {
      case ADD:
        addCookie(webCommunicationWrapper.getWebDriver());
        break;
      case REMOVE:
        removeCookie(webCommunicationWrapper.getWebDriver());
        break;
      default:
        break;
    }
    return CollectorStepResult.newModifierResult();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    modifyAction = ModifyAction.fromString(params.get(ACTION_PARAMETER));
    name = params.get(NAME_PARAMETER);
    value = params.get(VALUE_PARAMETER);
    domain = params.get(DOMAIN_PARAMETER);
    path = params.get(PATH_PARAMETER);

    Validator validator = new CookieModifierValidator(name, value, modifyAction);
    validator.validate(validationResultBuilder);
    if (validationResultBuilder.hasErrors()) {
      throw new ParametersException(validationResultBuilder.toString());
    }
  }

}
