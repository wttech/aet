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
package com.cognifide.aet.job.common.modifiers.waitfor;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;


public final class WaitForHelper {

  private WaitForHelper() {
  }

  /**
   * This helper method allows a Selenium WebDriver to wait until one or more ExpectedConditions are
   * met. The WebDriver waits for each ExpectedCondition to be met in a sequence, probing the page
   * every 500ms until the ExpectedCondition is met or the timeout limit is reached.
   *
   * @param webDriver The instance of a WebDriver navigating the page under test
   * @param timeoutInSeconds The time after which waiting ends unless the ExpectedCondition is met
   * earlier.
   * @param expectedConditions One or more of ExpectedConditions that have to be met.
   */
  public static CollectorStepResult waitForExpectedCondition(
      WebDriver webDriver,
      long timeoutInSeconds,
      ExpectedCondition<?>... expectedConditions) {
    FluentWait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(timeoutInSeconds,
        TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS)
        .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

    for (ExpectedCondition<?> expectedCondition : expectedConditions) {

      wait.until(expectedCondition);
    }
    return CollectorStepResult.newModifierResult();
  }
}
