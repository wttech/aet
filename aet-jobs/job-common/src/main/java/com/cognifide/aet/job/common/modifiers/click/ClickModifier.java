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
package com.cognifide.aet.job.common.modifiers.click;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.validation.ValidationResultBuilder;

public class ClickModifier implements CollectorJob {

	private static final Logger LOG = LoggerFactory.getLogger(ClickModifier.class);

	public static final String NAME = "click";

	private final WebDriver webDriver;

	private static final String PARAM_XPATH = "xpath";

	private static final String PARAM_TIMEOUT = "timeout";

	private static final long TIMEOUT_SECONDS_MAX_VALUE = 15L;

	private final ValidationResultBuilder validationResultBuilder;

	/**
	 * Xpath to element to click.
	 */
	private String xpath;

	/**
	 * Timeout for waiting for element (in seconds).
	 */
	private long timeoutInSeconds;

	public ClickModifier(final WebDriver webDriver, ValidationResultBuilder validationResultBuilder) {
		this.webDriver = webDriver;
		this.validationResultBuilder = validationResultBuilder;
	}

	@Override
	public boolean collect() throws ProcessingException {
		try {
			WebElement elementToClick = getElementByXpath(webDriver, xpath);
			if (elementToClick.isDisplayed()) {
				elementToClick.click();
			} else {
				LOG.warn("Element defined by xpath: '{}' was not yet visible! {}", xpath);
			}
		} catch (NoSuchElementException | TimeoutException e) {
			LOG.warn("No element defined by xpath: '{}' could be found before timeout  ({} seconds)! {}", xpath, timeoutInSeconds, e.getMessage());
		} catch (ElementNotVisibleException e) {
			LOG.warn("Element defined by xpath: '{}' was not yet visible! {}", xpath, e.getMessage());
		}
		return false;
	}

	@Override
	public void setParameters(final Map<String, String> params) throws ParametersException {
		xpath = params.get(PARAM_XPATH);
		String timeoutString = params.get(PARAM_TIMEOUT);
		if (StringUtils.isNotBlank(timeoutString) && StringUtils.isNumeric(timeoutString)) {
			timeoutInSeconds = TimeUnit.SECONDS.convert(Long.valueOf(timeoutString), TimeUnit.MILLISECONDS);
			if (timeoutInSeconds < 0) {
				validationResultBuilder.addErrorMessage("'timeout' parameter value on Click Modifier should be greater or equal zero.");
			} else if (TIMEOUT_SECONDS_MAX_VALUE < timeoutInSeconds) {
				validationResultBuilder.addErrorMessage("'timeout' parameter value on Click Modifier can't be greater than 15 seconds.");
			}
		} else {
			validationResultBuilder.addErrorMessage("Parameter 'timeout' on Click Modifier isn't a numeric value.");
		}
		if (StringUtils.isBlank(xpath)) {
			validationResultBuilder.addErrorMessage("Missing 'xpath' parameters on Click Modifier.");
		}
		if (validationResultBuilder.hasErrors()) {
			throw new ParametersException(validationResultBuilder.toString());
		}
	}

	private WebElement getElementByXpath(WebDriver webDriver, String xpathExpression) {
		WebDriverWait wait = new WebDriverWait(webDriver, timeoutInSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathExpression)));
		return webDriver.findElement(By.xpath(xpathExpression));
	}

}