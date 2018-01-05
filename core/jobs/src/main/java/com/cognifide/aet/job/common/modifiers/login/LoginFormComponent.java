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
package com.cognifide.aet.job.common.modifiers.login;

import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginFormComponent {

  private static final int TIMEOUT = 10;

  private static final int MAX_ATTEMPTS = 10;

  private static final String VALUE_ATTRIBUTE = "value";

  private static final String SET_VALUE_SCRIPT = "arguments[0].value=arguments[1]";

  private final WebElement loginInput;

  private final WebElement passwordInput;

  private final WebElement submitButton;

  private final JavascriptExecutor js;

  public LoginFormComponent(WebDriver webDriver, String loginInputSelector,
      String passwordInputSelector,
      String submitButtonSelector) throws ProcessingException {
    js = (JavascriptExecutor) webDriver;
    try {
      loginInput = getElementByXpath(webDriver, loginInputSelector);
      passwordInput = getElementByXpath(webDriver, passwordInputSelector);
      submitButton = getElementByXpath(webDriver, submitButtonSelector);
    } catch (TimeoutException e) {
      throw new ProcessingException("Exception during LoginFormComponent set up", e);
    }
  }

  private WebElement getElementByXpath(WebDriver webDriver, String xpathExpression) {
    WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathExpression)));
    return webDriver.findElement(By.xpath(xpathExpression));
  }

  public void login(String login, String password) throws ProcessingException {
    int i = 0;
    while (!isFormFilledProperly(login, password)) {
      if (++i > MAX_ATTEMPTS) {
        throw new ProcessingException(
            "Login failed after " + MAX_ATTEMPTS + " attempts to fill form.");
      }
      fill(loginInput, login);
      fill(passwordInput, password);
    }
    submit();
  }

  public boolean isFormFilledProperly(String login, String password) {
    return isFilledProperly(loginInput, login) && isFilledProperly(passwordInput, password);
  }

  private boolean isFilledProperly(WebElement element, String value) {
    return element.getAttribute(VALUE_ATTRIBUTE).equals(value);
  }

  private void submit() {
    submitButton.click();
  }

  private void fill(WebElement input, String value) {
    if (!isFilledProperly(input, value)) {
      js.executeScript(SET_VALUE_SCRIPT, input, value);
    }
  }

}
