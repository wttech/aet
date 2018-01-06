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
package com.cognifide.aet.job.common.modifiers.waitfor.elementtobevisible;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.modifiers.WebElementsLocatorParams;
import com.cognifide.aet.job.common.modifiers.waitfor.WaitForHelper;
import java.util.Map;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitForElementToBeVisibleModifier extends WebElementsLocatorParams implements
    CollectorJob {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(WaitForElementToBeVisibleModifier.class);

  static final String NAME = "wait-for-element-to-be-visible";

  private final WebDriver webDriver;

  WaitForElementToBeVisibleModifier(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    CollectorStepResult result;
    try {
      result = WaitForHelper.waitForExpectedCondition(webDriver, getTimeoutInSeconds(),
          ExpectedConditions.visibilityOfElementLocated(getLocator()));
    } catch (TimeoutException te) {
      final String message =
          String.format("Failed to wait for element to be visible with provided locator. "
              + "Page: %s. Error: %s", webDriver.getCurrentUrl(), te.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
      LOGGER.warn(message, te);
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    setElementParams(params);
  }
}
