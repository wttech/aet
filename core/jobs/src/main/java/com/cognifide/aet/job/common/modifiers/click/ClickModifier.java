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
package com.cognifide.aet.job.common.modifiers.click;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.modifiers.WebElementsLocatorParams;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ClickModifier extends WebElementsLocatorParams implements CollectorJob {

  private static final Logger LOG = LoggerFactory.getLogger(ClickModifier.class);

  public static final String NAME = "click";

  private final WebDriver webDriver;

  public ClickModifier(final WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    CollectorStepResult result;
    try {
        WebDriverWait wait = new WebDriverWait(webDriver, getTimeoutInSeconds());
        wait.until(ExpectedConditions.presenceOfElementLocated(getLocator()));
        WebElement elementToClick = webDriver.findElement(getLocator());

      if (elementToClick.isDisplayed()) {
        elementToClick.click();
        result = CollectorStepResult.newModifierResult();
      } else {
        final String message = String.format("Element defined by %s: '%s' is not yet visible!", getSelectorType(), getSelectorValue());
        result = CollectorStepResult.newProcessingErrorResult(message);
        LOG.warn(message);
      }
    } catch (NoSuchElementException | TimeoutException e) {
      final String message =
              String.format("No element defined by %s: '%s' could be found before timeout  (%s seconds)! %d",
                    getSelectorType(), getSelectorValue(), getTimeoutInSeconds());
      LOG.warn(message, e.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
    } catch (ElementNotVisibleException e) {
      final String message = String.format("Element defined by %s: '%s' was not yet visible!", getSelectorType(), getSelectorValue());
      LOG.warn(message, e.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
    }
    return result;
  }

  @Override
  public void setParameters(final Map<String, String> params) throws ParametersException {
    setElementParams(params);
  }

}