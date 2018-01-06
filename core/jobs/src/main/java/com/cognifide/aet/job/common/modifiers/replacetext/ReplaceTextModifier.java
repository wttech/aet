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
package com.cognifide.aet.job.common.modifiers.replacetext;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.SeleniumWaitHelper;
import com.cognifide.aet.job.common.modifiers.WebElementsLocatorParams;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceTextModifier extends WebElementsLocatorParams implements CollectorJob {

  public static final String NAME = "replaceText";

  private static final Logger LOG = LoggerFactory.getLogger(ReplaceTextModifier.class);

  private static final String ATTRIBUTE_PARAM = "attributeName";

  private static final String VALUE_PARAM = "value";

  private final WebDriver webDriver;

  private final CollectorProperties properties;

  private String attributeName;
  private String value;

  public ReplaceTextModifier(WebDriver webDriver, CollectorProperties properties) {
    this.webDriver = webDriver;
    this.properties = properties;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    if (LOG.isDebugEnabled()) {
      LOG.debug(
          "Replacing element for {} on page: {} with attributeName : {} and value: {}. url: {}",
          getLocator().toString(), webDriver.getCurrentUrl(), attributeName, value,
          properties.getUrl(),
          properties.getUrl());
    }
    return replaceText();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    setElementParams(params);
    attributeName = StringUtils.defaultIfBlank(params.get(ATTRIBUTE_PARAM), "innerHTML");
    value = StringUtils.defaultIfBlank(params.get(VALUE_PARAM), "");
  }

  public CollectorStepResult replaceText() throws ProcessingException {
    CollectorStepResult result;
    try {
      String script = "arguments[0]." + attributeName + "=arguments[1];";

      By elementLocator = getLocator();
      SeleniumWaitHelper
          .waitForElementToBePresent(webDriver, elementLocator, getTimeoutInSeconds());

      List<WebElement> webElements = webDriver.findElements(elementLocator);
      for (WebElement element : webElements) {
        ((JavascriptExecutor) webDriver).executeScript(script, element, value);
      }
      result = CollectorStepResult.newModifierResult();
    } catch (WebDriverException e) {
      final String message = String
          .format("Error while replacing text in element %s. %s", getLocator().toString(),
              e.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
      LOG.warn("Error while trying to find element '{}'", getLocator().toString(), e);
    } catch (Exception e) {
      throw new ProcessingException(
          "Can't replace text in +" + attributeName + " attribute in element: " + getLocator()
              .toString(), e);
    }
    return result;
  }

}
