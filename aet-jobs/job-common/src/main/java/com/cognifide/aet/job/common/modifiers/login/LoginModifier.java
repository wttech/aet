/*
 * Cognifide AET :: Job Common
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.login;

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by mikolaj.staniewski on 2014-10-03.
 */
public class LoginModifier implements CollectorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginModifier.class);

	private static final String DEFAULT_LOGIN_PAGE = "http://localhost:4502/libs/granite/core/content/login.html";

	private static final String DEFAULT_LOGIN = "admin";

	private static final String DEFAULT_PASSWORD = "admin";

	private static final String HIDDEN_PASSWORD = "*****";

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

	private static final String FORCE_LOGIN = "force-login";

	private final WebCommunicationWrapper webCommunicationWrapper;

	private String login;

	private String password;

	private String loginPage;

	private String submitButtonSelector;

	private String passwordInputSelector;

	private String loginInputSelector;

	private String loginTokenKey;

	private boolean forceLogin;

	public LoginModifier(WebCommunicationWrapper webCommunicationWrapper) {
		this.webCommunicationWrapper = webCommunicationWrapper;
	}

	@Override
	public boolean collect() throws ProcessingException {
		// check if there is a cookie
		if (forceLogin || !hasLoginToken()) {
			LOGGER.info("Attempting to login user: {} on site {}", login, loginPage);
			WebDriver driver = webCommunicationWrapper.getWebDriver();
			driver.get(loginPage);
			LoginFormComponent form = new LoginFormComponent(driver, loginInputSelector,
					passwordInputSelector, submitButtonSelector);
			form.login(login, password);

			Cookie authCookie = getLoginToken();
			if (authCookie == null) {
				throw new ProcessingException("Unable to acquire Cookie; check credentials.");
			}

			webCommunicationWrapper.getHttpRequestBuilder().addCookie(authCookie.getName(),
					authCookie.getValue());

			LOGGER.info("User has been authenticated");
		} else {
			LOGGER.info("User is authenticated.");
			Cookie cookie = getLoginToken();
			webCommunicationWrapper.getHttpRequestBuilder().addCookie(cookie.getName(), cookie.getValue());
		}
		return true;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		login = StringUtils.defaultString(params.get(LOGIN_PARAM), DEFAULT_LOGIN);
		password = StringUtils.defaultString(params.get(PASSWORD_PARAM), DEFAULT_PASSWORD);
		params.put(PASSWORD_PARAM, HIDDEN_PASSWORD);
		loginPage = StringUtils.defaultString(params.get(LOGIN_PAGE_PARAM), DEFAULT_LOGIN_PAGE);

		loginInputSelector = StringUtils.defaultString(params.get(LOGIN_INPUT_SELECTOR_PARAM),
				DEFAULT_LOGIN_INPUT_ELEMENT_SELECTOR);
		passwordInputSelector = StringUtils.defaultString(params.get(PASSWORD_INPUT_SELECTOR_PARAM),
				DEFAULT_PASSWORD_INPUT_ELEMENT_SELECTOR);
		submitButtonSelector = StringUtils.defaultString(params.get(SUBMIT_BUTTON_SELECTOR_PARAM),
				DEFAULT_SUBMIT_BUTTON_ELEMENT_SELECTOR);

		loginTokenKey = StringUtils.defaultString(params.get(LOGIN_TOKEN_KEY_PARAM), DEFAULT_LOGIN_TOKEN);
		if (params.containsKey(FORCE_LOGIN)) {
			this.forceLogin = Boolean.valueOf(params.get(FORCE_LOGIN));
		}
	}

	private Cookie getLoginToken() {
		return webCommunicationWrapper.getWebDriver().manage().getCookieNamed(loginTokenKey);
	}

	private boolean hasLoginToken() {
		return getLoginToken() != null;
	}
}
