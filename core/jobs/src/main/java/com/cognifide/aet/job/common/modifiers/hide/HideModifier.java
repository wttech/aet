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
package com.cognifide.aet.job.common.modifiers.hide;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.SeleniumWaitHelper;
import com.cognifide.aet.job.common.modifiers.WebElementsLocatorParams;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideModifier extends WebElementsLocatorParams implements CollectorJob {

  public static final String NAME = "hide";

  private static final Logger LOG = LoggerFactory.getLogger(HideModifier.class);

  private static final String LEAVE_BLANK_SPACE_PARAM = "leaveBlankSpace";

  private static final boolean LEAVE_BLANK_SPACE_DEFAULT = true;

  private static final String DISPLAY_NONE_SCRIPT = "arguments[0].style.display='none'";

  private static final String VISIBILITY_FALSE_SCRIPT = "arguments[0].style.visibility='hidden'";

  private final WebDriver webDriver;

  private final CollectorProperties properties;

  private boolean leaveBlankSpace;

  HideModifier(WebDriver webDriver, CollectorProperties properties) {
    this.webDriver = webDriver;
    this.properties = properties;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Hiding element {} on page: {} with leaveBlankSpace: {}. url: {}",
          getLocator().toString(), webDriver.getCurrentUrl(), leaveBlankSpace, properties.getUrl());
    }
    return hideElement(getLocator(), leaveBlankSpace);
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    setElementParams(params);
    leaveBlankSpace = BooleanUtils.toBooleanDefaultIfNull(
        BooleanUtils.toBooleanObject(params.get(LEAVE_BLANK_SPACE_PARAM)),
        LEAVE_BLANK_SPACE_DEFAULT);
  }

  private CollectorStepResult hideElement(By locator, boolean leaveBlankSpace)
      throws ProcessingException {
    CollectorStepResult result;
    try {
      String script = retrieveHidingScript(leaveBlankSpace);

      SeleniumWaitHelper
          .waitForElementToBePresent(webDriver, locator, getTimeoutInSeconds());

      List<WebElement> webElements = webDriver.findElements(locator);
      for (WebElement element : webElements) {
        ((JavascriptExecutor) webDriver).executeScript(script, element);
      }
      result = CollectorStepResult.newModifierResult();
    } catch (TimeoutException e) {
      final String message =
          String.format("Element not found before timeout (%s seconds): '%s'",
              getTimeoutInSeconds(), locator.toString());
      result = CollectorStepResult.newProcessingErrorResult(message);
      LOG.info(message);
    } catch (WebDriverException e) {
      final String message = String
          .format("Error while hiding element %s. Error: %s",
              locator.toString(), e.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
      LOG.warn(message, e);
    } catch (Exception e) {
      throw new ProcessingException("Can't hide element by " + locator.toString(), e);
    }
    return result;
  }

  private String retrieveHidingScript(boolean leaveBlankSpace) {
    String script;
    if (leaveBlankSpace) {
      script = VISIBILITY_FALSE_SCRIPT;
    } else {
      script = DISPLAY_NONE_SCRIPT;
    }
    return script;
  }
}
