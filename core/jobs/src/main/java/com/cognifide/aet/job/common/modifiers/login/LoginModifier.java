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

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import java.util.Map;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginModifier implements CollectorJob {

  public static final String NAME = "login";

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginModifier.class);

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final WebDriver webDriver;

  private LoginModifierConfig config;

  LoginModifier(WebCommunicationWrapper webCommunicationWrapper) {
    this.webCommunicationWrapper = webCommunicationWrapper;
    this.webDriver = webCommunicationWrapper.getWebDriver();
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    if (config.isForceLogin() || !hasLoginToken()) {
      int trialNumber = 0;
      boolean successfullyLogged = false;
      while (trialNumber < config.getRetrialNumber() && !successfullyLogged) {
        if (trialNumber > 0) {
          delayBeforeLoginCheckOrReattempt();
        }
        try {
          login();
          successfullyLogged = true;
        } catch (ProcessingException e) {
          LOGGER.warn("Attempt {}/{} to log in to {} failed.", trialNumber + 1,
              config.getRetrialNumber(), config.getLoginPage(), e);
        }
        trialNumber++;
      }
      if (!successfullyLogged) {
        throw new ProcessingException(
            "All attempts to log in to " + config.getLoginPage() + " failed.");
      }
    } else {
      LOGGER.info("User is authenticated.");
      Cookie cookie = getLoginToken();
      webCommunicationWrapper.getHttpRequestExecutor()
          .addCookie(cookie.getName(), cookie.getValue());
    }
    return CollectorStepResult.newModifierResult();
  }

  private void login() throws ProcessingException {
    loginToForm();
    delayBeforeLoginCheckOrReattempt();

    Cookie authCookie = getLoginToken();
    if (authCookie == null) {
      throw new ProcessingException("Unable to acquire Cookie; check credentials.");
    }

    webCommunicationWrapper.getHttpRequestExecutor()
        .addCookie(authCookie.getName(), authCookie.getValue());
    LOGGER.info("User has been authenticated");
  }

  private void loginToForm() throws ProcessingException {
    LOGGER
        .info("Attempting to login user: {} on site {}", config.getLogin(), config.getLoginPage());
    webDriver.get(config.getLoginPage());
    LoginFormComponent form = new LoginFormComponent(webDriver, config.getLoginInputSelector(),
        config.getPasswordInputSelector(), config.getSubmitButtonSelector(),
        new JavaScriptJobExecutor(webDriver));
    form.login(config.getLogin(), config.getPassword());
  }

  private void delayBeforeLoginCheckOrReattempt() {
    if (config.getDelayBeforeLoginCheckOrReattempt() > 0) {
      LOGGER.info("Waiting {} ms.", config.getDelayBeforeLoginCheckOrReattempt());
      try {
        Thread.sleep(config.getDelayBeforeLoginCheckOrReattempt());
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
