/**
 * AET
 *
 * Copyright (C) 2018 Cognifide Limited
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
package com.cognifide.aet.job.common.utils.javascript;

import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaScriptJobExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptJobExecutor.class);

  private final org.openqa.selenium.JavascriptExecutor executor;
  private final String currentUrl;

  public JavaScriptJobExecutor(WebDriver webDriver) {
    executor = (org.openqa.selenium.JavascriptExecutor) webDriver;
    currentUrl = webDriver.getCurrentUrl();
  }

  public JavaScriptJobResult execute(String jsSnippet, Object... elements)
      throws ProcessingException {

    try {
      return executeJs(jsSnippet, elements);
    } catch (Exception ex) {
      String message = String
          .format("Can't execute JavaScript command. jsSnippet: \"%s\". Error: %s ",
              jsSnippet, ex.getMessage());
      LOGGER.warn(message, ex);
      throw new ProcessingException(message, ex);
    }
  }

  private JavaScriptJobResult executeJs(String jsSnippet, Object... elements) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Executing JavaScript command: {} on page: {}", jsSnippet, currentUrl);
    }
    Object jsResult = executor.executeScript(jsSnippet, elements);
    return new JavaScriptJobResult(jsResult);
  }

}
