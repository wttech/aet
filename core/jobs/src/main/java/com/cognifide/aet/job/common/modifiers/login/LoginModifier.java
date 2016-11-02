/**
 * Automated Exploratory Tests
 * <p>
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.aet.job.common.modifiers.login;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.validation.ValidationResultBuilder;

import com.google.common.base.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginModifier implements CollectorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginModifier.class);

  public static final String NAME = "login";

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final WebDriver webDriver;

  private final ValidationResultBuilder validationResultBuilder;

  private LoginModifierConfig config;

  LoginModifier(WebCommunicationWrapper webCommunicationWrapper, ValidationResultBuilder validationResultBuilder) {
    this.webCommunicationWrapper = webCommunicationWrapper;
    webDriver = webCommunicationWrapper.getWebDriver();
    this.validationResultBuilder = validationResultBuilder;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    if (config.isForceLogin() || !hasLoginToken()) {
      int trialNumber = 0;
      boolean successfullyLogged = false;
      while (trialNumber < config.getRetrialNumber() && !successfullyLogged) {
        if (trialNumber > 0) {
          sleep();
        }
        try {
          login();
          successfullyLogged = true;
        } catch (ProcessingException e) {
          LOGGER.warn("Attempt {}/{} to log in to {} failed.", trialNumber + 1, config.getRetrialNumber(), config.getLoginPage(), e);
        }
        trialNumber++;
      }
      if (!successfullyLogged) {
        throw new ProcessingException("All attempts to log in to " + config.getLoginPage() + " failed.");
      }
    } else {
      LOGGER.info("User is authenticated.");
      Cookie cookie = getLoginToken();
      webCommunicationWrapper.getHttpRequestBuilder().addCookie(cookie.getName(), cookie.getValue());
    }
    return CollectorStepResult.newModifierResult();
  }

  private void login() throws ProcessingException {
    loginToForm();
    sleep();

    Cookie authCookie = getLoginToken();
    if (authCookie == null) {
      throw new ProcessingException("Unable to acquire Cookie; check credentials.");
    }

    webCommunicationWrapper.getHttpRequestBuilder().addCookie(authCookie.getName(), authCookie.getValue());
    LOGGER.info("User has been authenticated");
  }

  private void loginToForm() throws ProcessingException {
    LOGGER.info("Attempting to login user: {} on site {}", config.getLogin(), config.getLoginPage());
    webDriver.get(config.getLoginPage());
    LoginFormComponent form = new LoginFormComponent(webDriver, config.getLoginInputSelector(),
            config.getPasswordInputSelector(), config.getSubmitButtonSelector());
    form.login(config.getLogin(), config.getPassword());
  }

  private void sleep() {
    if (config.getLoginCheckTimeout() > 0) {
      LOGGER.info("Waiting {} ms before checking login token.", config.getLoginCheckTimeout());
      try {
        Thread.sleep(config.getLoginCheckTimeout());
      } catch (InterruptedException e) {
        LOGGER.error("Interruption", e);
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    this.config = new LoginModifierConfig(params);
  }

  private Cookie getLoginToken() {
    return webDriver.manage().getCookieNamed(config.getLoginTokenKey());
  }

  private boolean hasLoginToken() {
    return getLoginToken() != null;
  }
}
