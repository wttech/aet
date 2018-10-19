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
package com.cognifide.aet.job.common.modifiers.resolution;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResolutionModifier implements CollectorJob {

  static final String NAME = "resolution";

  private static final Logger LOG = LoggerFactory.getLogger(ResolutionModifier.class);

  private static final String JAVASCRIPT_GET_BODY_HEIGHT = "return document.body.scrollHeight";

  private static final String WIDTH_PARAM = "width";
  private static final String HEIGHT_PARAM = "height";

  private static final int HEIGHT_MAX_SIZE = 35000;
  private static final int HEIGHT_MIN_SIZE = 10;
  private static final int INITIAL_HEIGHT = 300;
  private static final int HEIGHT_NOT_DEFINED = 0;

  private final WebDriver webDriver;

  private int width;
  private int height;

  ResolutionModifier(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    setResolution();
    return CollectorStepResult.newModifierResult();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    setWidth(params);
    setHeight(params);
  }

  private void setWidth(Map<String, String> params) throws ParametersException {
    width = Optional.ofNullable(params.get(WIDTH_PARAM))
        .map(NumberUtils::toInt)
        .orElseThrow(() ->
            new ParametersException("You have to specify width, height parameter is optional"));
    ParametersValidator.checkRange(width, 1, HEIGHT_MAX_SIZE,
        "Width should be greater than 0");
  }

  private void setHeight(Map<String, String> params) throws ParametersException {

    String heightParam = params.get(HEIGHT_PARAM);
    if (StringUtils.isNotBlank(heightParam)) {
      height = NumberUtils.toInt(heightParam);
      ParametersValidator.checkRange(height, 1, HEIGHT_MAX_SIZE,
          "Height should be greater than 0 and smaller than " + HEIGHT_MAX_SIZE);
    }
  }

  private void setResolution() throws ProcessingException {
    Window window = webDriver.manage().window();
    if (height == HEIGHT_NOT_DEFINED) {
      calculateHeight(window);
    }
    LOG.info("Setting resolution to  {}x{}  ", width, height);
    window.setSize(new Dimension(width, height));
  }

  private void calculateHeight(Window window) throws ProcessingException {
    window.setSize(new Dimension(width, INITIAL_HEIGHT));
    getHeightFromDocumentBody();
    verifyHeight();
  }

  private void getHeightFromDocumentBody() throws ProcessingException {
    JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
    Object jsResult = jsExecutor.executeScript(JAVASCRIPT_GET_BODY_HEIGHT);
    height = Optional.ofNullable(jsResult)
        .map(Object::toString)
        .map(NumberUtils::toInt)
        .orElseThrow(() ->
            new ProcessingException("Cannot calculate document height from js command: "
                + JAVASCRIPT_GET_BODY_HEIGHT));

  }

  private void verifyHeight() {
    if (height > HEIGHT_MAX_SIZE) {
      LOG.warn("Height is over browser limit, changing height to {}", HEIGHT_MAX_SIZE);
      height = HEIGHT_MAX_SIZE;
    } else if (height < HEIGHT_MIN_SIZE) {
      LOG.warn("Height is lower than minimum, changing height to {}", HEIGHT_MIN_SIZE);
      height = HEIGHT_MIN_SIZE;
    }
  }
}
