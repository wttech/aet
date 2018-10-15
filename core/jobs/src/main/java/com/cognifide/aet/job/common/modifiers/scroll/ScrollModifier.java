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
package com.cognifide.aet.job.common.modifiers.scroll;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ScrollModifier implements CollectorJob {

  static final String NAME = "scroll";
  private static final Logger LOGGER = LoggerFactory.getLogger(ScrollModifier.class);
  private final ParametersParser parametersParser;
  private final JavascriptExecutor jsExecutor;
  private final String currentUtl;

  ScrollModifier(WebDriver webDriver) {
    this.parametersParser = new ParametersParser();
    this.jsExecutor = (JavascriptExecutor) webDriver;
    this.currentUtl = webDriver.getCurrentUrl();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    parametersParser.setParameters(params);
  }

  @Override
  public CollectorStepResult collect() {
    String jsSnippet = parametersParser.getJavaScriptSnippet();
    return tryToExecuteJs(jsSnippet);
  }

  private CollectorStepResult tryToExecuteJs(String jsSnippet) {
    CollectorStepResult result;
    try {
      result = executeJs(jsSnippet);
    } catch (Exception ex) {
      result = logErrorDuringExecution(jsSnippet, ex);
    }
    return result;
  }

  private CollectorStepResult executeJs(String jsSnippet) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Executing JavaScript command: {} on page: {}", jsSnippet, currentUtl);
    }
    jsExecutor.executeScript(jsSnippet);
    return CollectorStepResult.newModifierResult();
  }

  private CollectorStepResult logErrorDuringExecution(String jsSnippet, Exception ex) {
    String message = String
        .format("Can't execute JavaScript command. jsSnippet: \"%s\". Error: %s ",
            jsSnippet, ex.getMessage());
    LOGGER.warn(message, ex);
    return CollectorStepResult.newProcessingErrorResult(message);
  }
}
