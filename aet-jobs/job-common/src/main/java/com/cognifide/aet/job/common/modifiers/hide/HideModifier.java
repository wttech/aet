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
package com.cognifide.aet.job.common.modifiers.hide;

import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.apache.commons.lang3.BooleanUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class HideModifier implements CollectorJob {

	public static final String NAME = "hide";

	private static final Logger LOG = LoggerFactory.getLogger(HideModifier.class);

	private static final String LEAVE_BLANK_SPACE_PARAM = "leaveBlankSpace";

	private static final boolean LEAVE_BLANK_SPACE_DEFAULT = true;

	private static final String DISPLAY_NONE_SCRIPT = "arguments[0].style.display='none'";

	private static final String VISIBILITY_FALSE_SCRIPT = "arguments[0].style.visibility='hidden'";

	private static final String XPATH_PARAM = "xpath";

	private final WebDriver webDriver;

	private final CollectorProperties properties;

	private String xpath;

	private boolean leaveBlankSpace;

	public HideModifier(WebDriver webDriver, CollectorProperties properties) {
		this.webDriver = webDriver;
		this.properties = properties;
	}

	@Override
	public boolean collect() throws ProcessingException {
		LOG.debug("Hiding element for xpath: {} on page: {} with leaveBlankSpace: {}. urlName: {} url: {}",
				xpath, webDriver.getCurrentUrl(), leaveBlankSpace, properties.getUrlName(),
				properties.getUrl());
		hideElement(webDriver, xpath);
		return false;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		xpath = params.get(XPATH_PARAM);
		leaveBlankSpace = BooleanUtils.toBooleanDefaultIfNull(
				BooleanUtils.toBooleanObject(params.get(LEAVE_BLANK_SPACE_PARAM)), LEAVE_BLANK_SPACE_DEFAULT);
		ParametersValidator.checkNotBlank(xpath, "xpath parameter is mandatory");
	}

	public void hideElement(WebDriver driver, String xpath) throws ProcessingException {
		try {
			String script;
			if (leaveBlankSpace) {
				script = VISIBILITY_FALSE_SCRIPT;
			} else {
				script = DISPLAY_NONE_SCRIPT;
			}
			List<WebElement> webElements = driver.findElements(By.xpath(xpath));
			for (WebElement element : webElements) {
				((JavascriptExecutor) driver).executeScript(script, element);
			}
		} catch (NoSuchElementException e) {
			LOG.warn("Error while trying to find element '{}'", xpath, e);
		} catch (Exception e) {
			throw new ProcessingException("Can't hide element: " + xpath, e);
		}
	}

}
