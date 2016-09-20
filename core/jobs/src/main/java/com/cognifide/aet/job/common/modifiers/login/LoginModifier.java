/**
 * Automated Exploratory Tests
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
package com.cognifide.aet.job.common.modifiers.login;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.validation.ValidationResultBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginModifier implements CollectorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginModifier.class);

  private static final String DEFAULT_LOGIN = "admin";

  private static final String DEFAULT_PASSWORD = "admin";

  private static final String DEFAULT_LOGIN_INPUT_ELEMENT_SELECTOR = "//input[@name='j_username']";

  private static final String DEFAULT_PASSWORD_INPUT_ELEMENT_SELECTOR = "//input[@name='j_password']";

  private static final String DEFAULT_SUBMIT_BUTTON_ELEMENT_SELECTOR = "//*[@type='submit']";

  private static final String DEFAULT_LOGIN_TOKEN = "login-token";

  public static final String NAME = "login";

  private static final String LOGIN_PARAM = "login";

  private static final String PASSWORD_PARAM = "password";

  private static final String LOGIN_PAGE_PARAM = "login-page";

  private static final String LOGIN_INPUT_SELECTOR_PARAM = "login-input-selector";

  private static final String PASSWORD_INPUT_SELECTOR_PARAM = "password-input-selector";

  private static final String SUBMIT_BUTTON_SELECTOR_PARAM = "submit-button-selector";

  private static final String LOGIN_TOKEN_KEY_PARAM = "login-token-key";

  private static final String LOGIN_CHECK_TIMEOUT_PARAM = "timeout";

  private static final String FORCE_LOGIN = "force-login";

  private final WebCommunicationWrapper webCommunicationWrapper;

  private final WebDriver webDriver;

  private final ValidationResultBuilder validationResultBuilder;

  private String login;

  private String password;

  private String loginPage;

  private String submitButtonSelector;

  private String passwordInputSelector;

  private String loginInputSelector;

  private String loginTokenKey;

  private int loginCheckTimeout;

  private boolean forceLogin;

  LoginModifier(WebCommunicationWrapper webCommunicationWrapper, ValidationResultBuilder validationResultBuilder) {
    this.webCommunicationWrapper = webCommunicationWrapper;
    webDriver = webCommunicationWrapper.getWebDriver();
    this.validationResultBuilder = validationResultBuilder;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    // check if there is a cookie
    if (forceLogin || !hasLoginToken()) {
      LOGGER.info("Attempting to login user: {} on site {}", login, loginPage);
      webDriver.get(loginPage);
      LoginFormComponent form = new LoginFormComponent(webDriver, loginInputSelector,
              passwordInputSelector, submitButtonSelector);
      form.login(login, password);

      if (loginCheckTimeout > 0) {
        LOGGER.info("Waiting {} ms before checking login token.", loginCheckTimeout);
        try {
          Thread.sleep(loginCheckTimeout);
        } catch (InterruptedException e) {
          LOGGER.error("Interruption", e);
          Thread.currentThread().interrupt();
        }
      }
      Cookie authCookie = getLoginToken();
      if (authCookie == null) {
        throw new ProcessingException("Unable to acquire Cookie; check credentials.");
      }

      webCommunicationWrapper.getHttpRequestBuilder().addCookie(authCookie.getName(), authCookie.getValue());

      LOGGER.info("User has been authenticated");
    } else {
      LOGGER.info("User is authenticated.");
      Cookie cookie = getLoginToken();
      webCommunicationWrapper.getHttpRequestBuilder().addCookie(cookie.getName(), cookie.getValue());
    }
    return CollectorStepResult.newModifierResult();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    loginPage = params.get(LOGIN_PAGE_PARAM);
    login = StringUtils.defaultString(params.get(LOGIN_PARAM), DEFAULT_LOGIN);
    password = StringUtils.defaultString(params.get(PASSWORD_PARAM), DEFAULT_PASSWORD);

    loginInputSelector = StringUtils.defaultString(params.get(LOGIN_INPUT_SELECTOR_PARAM),
            DEFAULT_LOGIN_INPUT_ELEMENT_SELECTOR);
    passwordInputSelector = StringUtils.defaultString(params.get(PASSWORD_INPUT_SELECTOR_PARAM),
            DEFAULT_PASSWORD_INPUT_ELEMENT_SELECTOR);
    submitButtonSelector = StringUtils.defaultString(params.get(SUBMIT_BUTTON_SELECTOR_PARAM),
            DEFAULT_SUBMIT_BUTTON_ELEMENT_SELECTOR);

    loginTokenKey = StringUtils.defaultString(params.get(LOGIN_TOKEN_KEY_PARAM), DEFAULT_LOGIN_TOKEN);
    final String timeoutString = params.get(LOGIN_CHECK_TIMEOUT_PARAM);

    ParametersValidator.checkNotBlank(loginPage, "`login-page` parameter is mandatory");
    if (StringUtils.isNotBlank(timeoutString)) {
      loginCheckTimeout = NumberUtils.toInt(timeoutString);
      ParametersValidator.checkRange(loginCheckTimeout, 0, 10000, "Timeout duration should be greater than 0 and " +
              "less than 10 seconds");
    }
    if (params.containsKey(FORCE_LOGIN)) {
      this.forceLogin = Boolean.valueOf(params.get(FORCE_LOGIN));
    }
  }

  private Cookie getLoginToken() {
    return webDriver.manage().getCookieNamed(loginTokenKey);
  }

  private boolean hasLoginToken() {
    return getLoginToken() != null;
  }
}
