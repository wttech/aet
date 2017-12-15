/**
 * AET
 * <p>
 * Author: pnad@github
 * used HideModifier code - Copyright (C) 2017 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.executejavascript;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteJavaScriptModifier implements CollectorJob {

  public static final String NAME = "executejavascript";
  private static final Logger LOG = LoggerFactory.getLogger(ExecuteJavaScriptModifier.class);

  private static final String CMD_PARAM = "cmd";
  private static final String URL_PARAM = "snippetUrl";
  private static final String BASIC_AUTH_PARAM = "basicAuth";

  private final WebDriver webDriver;
  private final ExternalSnippetHttpClient httpClient;

  private String cmd;
  private String snippetUrl;
  private String basicAuth;

  ExecuteJavaScriptModifier(WebDriver webDriver, ExternalSnippetHttpClient httpClient) {
    this.webDriver = webDriver;
    this.httpClient = httpClient;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    final String jsSnippet = getJsSnippet();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Executing JavaScript command: {} on page: {}", jsSnippet,
          webDriver.getCurrentUrl());
    }
    return javaScriptElement(webDriver, jsSnippet);
  }

  private String getJsSnippet() throws ProcessingException {
    final String jsSnippet;
    if (StringUtils.isNotBlank(cmd)) {
      jsSnippet = cmd;
    } else {
      jsSnippet = httpClient.get(snippetUrl, basicAuth);
    }
    return jsSnippet;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    cmd = params.get(CMD_PARAM);
    snippetUrl = params.get(URL_PARAM);
    ParametersValidator.checkAtLeastOneNotBlank(
        "Either 'cmd' or 'snippetUrl' parameter must be provided for executejavascript modifier.",
        cmd, snippetUrl);
    basicAuth = params.get(BASIC_AUTH_PARAM);
  }

  private CollectorStepResult javaScriptElement(WebDriver driver, String jsSnippet) {
    CollectorStepResult result;
    try {
      ((JavascriptExecutor) driver).executeScript(jsSnippet);
      result = CollectorStepResult.newModifierResult();
    } catch (Exception e) {
      final String message = String
          .format("Can't execute JavaScript command. jsSnippet: \"%s\". Error: %s ",
              jsSnippet, e.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
      LOG.warn(message, e);
    }
    return result;
  }

}
