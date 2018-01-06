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
package com.cognifide.aet.job.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumWaitHelper {

  public static void waitForElementToBePresent(WebDriver webDriver, By elementLocator, long timeOut)
      throws WebDriverException {
    WebDriverWait wait = new WebDriverWait(webDriver, timeOut);
    wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
  }

  public static void waitForElementToBeClickable(WebDriver webDriver, By elementLocator,
      long timeOut) {
    WebDriverWait wait = new WebDriverWait(webDriver, timeOut);
    wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
  }
}
