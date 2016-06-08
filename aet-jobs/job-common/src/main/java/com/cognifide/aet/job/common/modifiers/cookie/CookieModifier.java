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
package com.cognifide.aet.job.common.modifiers.cookie;

import java.util.Map;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;

public class CookieModifier implements CollectorJob {

	public static final String NAME = "modify-cookie";

	protected static final String NAME_PARAMETER = "cookie-name";

	protected static final String VALUE_PARAMETER = "cookie-value";

	private static final String ACTION_PARAMETER = "action";

	private final WebCommunicationWrapper webCommunicationWrapper;

	private final ValidationResultBuilder validationResultBuilder;

	private final String url;

	private ModifyAction modifyAction;

	private String name;

	private String value;

	public CookieModifier(WebCommunicationWrapper webCommunicationWrapper,
			ValidationResultBuilder validationResultBuilder, String url) {
		this.webCommunicationWrapper = webCommunicationWrapper;
		this.validationResultBuilder = validationResultBuilder;
		this.url = url;
	}

	@Override
	public boolean collect() throws ProcessingException {
		WebDriver webDriver = webCommunicationWrapper.getWebDriver();
		switch (modifyAction) {
			case ADD:
				addCookie(webDriver);
				break;
			case REMOVE:
				removeCookie(webDriver);
				break;
			default:
				break;
		}

		return false;
	}

	private void addCookie(WebDriver webDriver) {
		webDriver.get(url);
		Cookie cookie = new Cookie(name, value);
		webDriver.manage().addCookie(cookie);
		webCommunicationWrapper.getHttpRequestBuilder().addCookie(name, value);
	}

	private void removeCookie(WebDriver webDriver) {
		webDriver.manage().deleteCookieNamed(name);
		webCommunicationWrapper.getHttpRequestBuilder().removeCookie(name);
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		modifyAction = ModifyAction.fromString(params.get(ACTION_PARAMETER));
		name = params.get(NAME_PARAMETER);
		value = params.get(VALUE_PARAMETER);

		Validator validator = new CookieModifierValidator(name, value, modifyAction);
		validator.validate(validationResultBuilder);
		if (validationResultBuilder.hasErrors()) {
			throw new ParametersException(validationResultBuilder.toString());
		}
	}

}
