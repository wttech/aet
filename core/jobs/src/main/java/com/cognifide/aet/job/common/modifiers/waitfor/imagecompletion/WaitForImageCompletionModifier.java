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
package com.cognifide.aet.job.common.modifiers.waitfor.imagecompletion;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.common.modifiers.WebElementsLocatorParams;
import com.cognifide.aet.job.common.modifiers.waitfor.WaitForHelper;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import java.util.Map;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitForImageCompletionModifier extends WebElementsLocatorParams implements
    CollectorJob {

  static final String NAME = "wait-for-image-completion";
  private static final Logger LOGGER = LoggerFactory
      .getLogger(WaitForImageCompletionModifier.class);

  private final WebDriver webDriver;
  private final JavaScriptJobExecutor jsExecutor;

  WaitForImageCompletionModifier(WebDriver webDriver, JavaScriptJobExecutor jsExecutor) {
    this.webDriver = webDriver;
    this.jsExecutor = jsExecutor;
  }

  @Override
  public CollectorStepResult collect() {
    CollectorStepResult result;
    try {
      result = WaitForHelper.waitForExpectedCondition(webDriver, getTimeoutInSeconds(),
          ExpectedConditions.visibilityOfElementLocated(getLocator()),
          this::waitForImageCompletion);
    } catch (TimeoutException te) {
      final String message =
          String.format("Failed to wait for image to be loaded with provided locator. Error: %s",
              te.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
      LOGGER.warn(message, te);
    }
    return result;
  }

  private Boolean waitForImageCompletion(WebDriver webDriver) {
    Boolean complete;
    WebElement element = webDriver.findElement(getLocator());
    try {
      complete = (Boolean) jsExecutor.execute("return arguments[0].complete", element)
          .getExecutionResult()
          .orElse(false);
    } catch (Exception ex) {
      LOGGER.warn("Cannot execute javascript", ex);
      complete = false;
    }
    LOGGER.debug("Waiting for image completion. Complete: '{}'", complete);
    return complete;
  }

  @Override
  public void setParameters(Map<String, String> parameters) throws ParametersException {
    setElementParams(parameters);
  }
}
