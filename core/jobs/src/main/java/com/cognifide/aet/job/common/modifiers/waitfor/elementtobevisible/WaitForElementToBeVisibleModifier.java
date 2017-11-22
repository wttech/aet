/**
 * AET
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
package com.cognifide.aet.job.common.modifiers.waitfor.elementtobevisible;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.modifiers.waitfor.WaitForHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WaitForElementToBeVisibleModifier implements CollectorJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForElementToBeVisibleModifier.class);

    static final String NAME = "wait-for-element-to-be-visible";

    private final WebDriver webDriver;

    private static final String CSS_PARAMETER = "css";
    private static final String XPATH_PARAMETER = "xpath";
    private static final String TIMEOUT_PARAMETER = "timeout";

    private String webElementCssLocator;
    private String webElementXpathLocator;
    private int timeoutInMilliseconds;

    WaitForElementToBeVisibleModifier(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public CollectorStepResult collect() throws ProcessingException {
        CollectorStepResult result;
        try {
            By locator = WaitForHelper.determineLocatorStrategy(webElementCssLocator, webElementXpathLocator);
            result = WaitForHelper.waitForExpectedCondition(webDriver, timeoutInMilliseconds, ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            final String message =
                    String.format("Failed to wait for element to be visible with provided locator. Error: %s",
                            e.getMessage());
            result = CollectorStepResult.newProcessingErrorResult(message);
            LOGGER.warn(message, e);
        }
        return result;
    }

    @Override
    public void setParameters(Map<String, String> parameters) throws ParametersException {
        if (parameters.containsKey(CSS_PARAMETER)) {
            webElementCssLocator = parameters.get(CSS_PARAMETER);
        } else if (parameters.containsKey(XPATH_PARAMETER)) {
            webElementXpathLocator = parameters.get(XPATH_PARAMETER);
        } else {
            throw new ParametersException("Xpath or CSS locator has to be provided for wait-for-element-to-be-visible modifier.");
        }

        if (!parameters.containsKey(TIMEOUT_PARAMETER)) {
            throw new ParametersException("Timeout has to be provided for wait-for-element-to-be-visible modifier.");
        }
        timeoutInMilliseconds = Integer.parseInt(parameters.get(TIMEOUT_PARAMETER));
    }
}
