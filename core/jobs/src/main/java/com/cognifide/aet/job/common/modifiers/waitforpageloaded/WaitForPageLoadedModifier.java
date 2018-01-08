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
package com.cognifide.aet.job.common.modifiers.waitforpageloaded;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This modifier waits until page is loaded or fixed amount of time is up. The idea of waiting for
 * page is counting amount of elements [by findElements(By.xpath("//*"))] on current page state in
 * loop. If number of elements has increased since last checkout, continue loop (or break if
 * timeout). Else if number of elements is still, assume the page is loaded and finish waiting.
 */
public class WaitForPageLoadedModifier implements CollectorJob {

  public static final String NAME = "wait-for-page-loaded";

  private static final Logger LOG = LoggerFactory.getLogger(WaitForPageLoadedModifier.class);

  private static final int PAGE_LOAD_WAIT_TIMEOUT_MS = 10000;

  private static final int PAGE_LOAD_CHECK_TIME_MS = 1000;

  private final WebDriver webDriver;

  public WaitForPageLoadedModifier(WebDriver webDriver) {
    this.webDriver = webDriver;
  }


  @Override
  public CollectorStepResult collect() throws ProcessingException {
    CollectorStepResult result;
    final String currentUrl = webDriver.getCurrentUrl();
    LOG.info("Start WaitForPageLoadedModifier for page {}", currentUrl);
    int waitTimeoutMs = PAGE_LOAD_WAIT_TIMEOUT_MS;

    int previousLoadedElementsCount;
    int currentLoadedElementsCount = 0;
    try {
      do {
        previousLoadedElementsCount = currentLoadedElementsCount;
        Thread.sleep(PAGE_LOAD_CHECK_TIME_MS);
        waitTimeoutMs -= PAGE_LOAD_CHECK_TIME_MS;
        currentLoadedElementsCount = webDriver.findElements(By.xpath("//*")).size();
        LOG.debug("Currently found {} elements. Left {} ms to timeout.", currentLoadedElementsCount,
            waitTimeoutMs);
      } while (currentLoadedElementsCount > previousLoadedElementsCount && waitTimeoutMs > 0);
      result = CollectorStepResult.newModifierResult();
    } catch (InterruptedException e) {
      LOG.error("WaitForPageLoadedModifier interrupted", e);
      throw new ProcessingException("WaitForPageLoadedModifier interrupted", e);
    }
    if (waitTimeoutMs <= 0) {
      LOG.warn("Page {} reload timeout!", currentUrl);
      result.addError(String.format("Page %s was timed out before loading!", currentUrl));
    }

    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //no parameters needed
  }

}
