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
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import java.util.Map;
import org.openqa.selenium.WebDriver;

class ScrollModifier implements CollectorJob {

  static final String NAME = "scroll";

  private final ParametersParser parametersParser;
  private final JavaScriptJobExecutor jsExecutor;

  ScrollModifier(WebDriver webDriver) {
    this.parametersParser = new ParametersParser();
    this.jsExecutor = new JavaScriptJobExecutor(webDriver);
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    parametersParser.setParameters(params);
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    String jsSnippet = parametersParser.getJavaScriptSnippet();
    jsExecutor.execute(jsSnippet);
    return CollectorStepResult.newModifierResult();
  }

}
