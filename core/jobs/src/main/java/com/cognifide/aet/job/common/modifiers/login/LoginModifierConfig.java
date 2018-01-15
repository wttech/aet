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

import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

class LoginModifierConfig {

  private static final String DEFAULT_LOGIN = "admin";

  private static final String DEFAULT_PASSWORD = "admin";

  private static final String DEFAULT_LOGIN_INPUT_SELECTOR = "//input[@name='j_username']";

  private static final String DEFAULT_PASSWORD_INPUT_SELECTOR = "//input[@name='j_password']";

  private static final String DEFAULT_SUBMIT_BUTTON_SELECTOR = "//*[@type='submit']";

  private static final String DEFAULT_LOGIN_TOKEN = "login-token";

  private static final String LOGIN_PARAM = "login";

  private static final String PASSWORD_PARAM = "password";

  private static final String LOGIN_PAGE_PARAM = "login-page";

  private static final String LOGIN_INPUT_SELECTOR_PARAM = "login-input-selector";

  private static final String PASSWORD_INPUT_SELECTOR_PARAM = "password-input-selector";

  private static final String SUBMIT_BUTTON_SELECTOR_PARAM = "submit-button-selector";

  private static final String LOGIN_TOKEN_KEY_PARAM = "login-token-key";

  private static final String LOGIN_CHECK_TIMEOUT_PARAM = "timeout";

  private static final String FORCE_LOGIN = "force-login";

  private static final String DEFAULT_CHECK_TIMEOUT_PARAM = "1000";

  private static final String RETRIAL_NUMBER_PARAM = "retrial-number";

  private static final String DEFAULT_RETRIAL_NUMBER = "3";

  private final String loginPage;

  private final String login;

  private final String password;

  private final String loginInputSelector;

  private final String passwordInputSelector;

  private final String submitButtonSelector;

  private final String loginTokenKey;

  private final int delayBeforeLoginCheckOrReattempt;

  private final boolean forceLogin;

  private final int retrialNumber;

  public LoginModifierConfig(Map<String, String> params) throws ParametersException {
    this.loginPage = params.get(LOGIN_PAGE_PARAM);
    this.login = getParameter(params, LOGIN_PARAM, DEFAULT_LOGIN);
    this.password = getParameter(params, PASSWORD_PARAM, DEFAULT_PASSWORD);
    this.loginInputSelector = getParameter(params, LOGIN_INPUT_SELECTOR_PARAM,
        DEFAULT_LOGIN_INPUT_SELECTOR);
    this.passwordInputSelector = getParameter(params, PASSWORD_INPUT_SELECTOR_PARAM,
        DEFAULT_PASSWORD_INPUT_SELECTOR);
    this.submitButtonSelector = getParameter(params, SUBMIT_BUTTON_SELECTOR_PARAM,
        DEFAULT_SUBMIT_BUTTON_SELECTOR);
    this.loginTokenKey = getParameter(params, LOGIN_TOKEN_KEY_PARAM, DEFAULT_LOGIN_TOKEN);
    this.forceLogin = BooleanUtils.toBoolean(params.get(FORCE_LOGIN));
    this.delayBeforeLoginCheckOrReattempt = NumberUtils
        .toInt(getParameter(params, LOGIN_CHECK_TIMEOUT_PARAM, DEFAULT_CHECK_TIMEOUT_PARAM));
    this.retrialNumber = NumberUtils
        .toInt(getParameter(params, RETRIAL_NUMBER_PARAM, DEFAULT_RETRIAL_NUMBER));

    ParametersValidator.checkNotBlank(loginPage, "`login-page` parameter is mandatory");
    ParametersValidator.checkRange(delayBeforeLoginCheckOrReattempt, 0, 10000,
        "Timeout duration should be greater than 0 and less than 10 seconds");
    ParametersValidator
        .checkRange(retrialNumber, 1, Integer.MAX_VALUE, "Retrial number has to be at least 1");
  }

  private String getParameter(Map<String, String> params, String key, String defaultValue) {
    return StringUtils.defaultString(params.get(key), defaultValue);
  }

  public String getLoginPage() {
    return loginPage;
  }

  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }

  public String getLoginInputSelector() {
    return loginInputSelector;
  }

  public String getPasswordInputSelector() {
    return passwordInputSelector;
  }

  public String getSubmitButtonSelector() {
    return submitButtonSelector;
  }

  public String getLoginTokenKey() {
    return loginTokenKey;
  }

  public int getDelayBeforeLoginCheckOrReattempt() {
    return delayBeforeLoginCheckOrReattempt;
  }

  public boolean isForceLogin() {
    return forceLogin;
  }

  public int getRetrialNumber() {
    return retrialNumber;
  }
}
