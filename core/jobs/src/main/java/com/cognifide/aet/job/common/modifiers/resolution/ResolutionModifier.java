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
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import com.cognifide.aet.job.common.utils.Sampler;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolutionModifier implements CollectorJob {

  public static final String NAME = "resolution";

  private static final Logger LOG = LoggerFactory.getLogger(ResolutionModifier.class);
  private static final String WIDTH_PARAM = "width";
  private static final String HEIGHT_PARAM = "height";
  private static final String SAMPLING_PERIOD_PARAM = "samplingPeriod";
  private static final String JAVASCRIPT_GET_BODY_HEIGHT = "return document.body.scrollHeight";

  private static final int HEIGHT_MAX_SIZE = 35000;
  private static final int INITIAL_HEIGHT = 300;
  private static final int MINIMUM_HEIGHT = 1;
  private static final int HEIGHT_NOT_DEFINED = 0;
  private static final int HEIGHT_NOT_CALCULATED = -1;
  private static final int DEFAULT_SAMPLING_WAIT_PERIOD = 100;
  private static final int MAX_SAMPLES_THRESHOLD = 15;
  private static final int SAMPLE_QUEUE_SIZE = 3;
  private static final int MAX_SAMPLING_PERIOD = 10000;

  private final WebDriver webDriver;
  private final JavaScriptJobExecutor jsExecutor;

  private int width;
  private int height;
  private int samplingPeriod;

  ResolutionModifier(WebDriver webDriver, JavaScriptJobExecutor jsExecutor) {
    this.webDriver = webDriver;
    this.jsExecutor = jsExecutor;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    setResolution();
    return CollectorStepResult.newModifierResult();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (params.containsKey(WIDTH_PARAM)) {
      width = NumberUtils.toInt(params.get(WIDTH_PARAM));
      ParametersValidator.checkRange(width, 1, HEIGHT_MAX_SIZE, "Width should be greater than 0");
      if (params.containsKey(HEIGHT_PARAM)) {
        setHeight(params);
      } else {
        setHeightSamplingPeriod(params);
      }
    } else {
      throw new ParametersException("You have to specify width, height parameter is optional");
    }
  }

  private void setHeight(Map<String, String> params) throws ParametersException {
    height = NumberUtils.toInt(params.get(HEIGHT_PARAM));
    ParametersValidator
        .checkRange(height, 1, HEIGHT_MAX_SIZE,
            "Height should be greater than 0 and smaller than " + HEIGHT_MAX_SIZE);
  }

  private void setHeightSamplingPeriod(Map<String, String> params) throws ParametersException {
    samplingPeriod = NumberUtils
        .toInt(params.get(SAMPLING_PERIOD_PARAM), DEFAULT_SAMPLING_WAIT_PERIOD);
    ParametersValidator
        .checkRange(samplingPeriod, 0, MAX_SAMPLING_PERIOD,
            "samplingPeriod should be greater than or equal 0 and smaller or equal "
                + MAX_SAMPLING_PERIOD);
  }

  private void setResolution() throws ProcessingException {
    if (height == HEIGHT_NOT_DEFINED) {
      height = calculateWindowHeight();
      if (height > HEIGHT_MAX_SIZE) {
        LOG.warn("Height is over browser limit, changing height to {}", HEIGHT_MAX_SIZE);
        height = HEIGHT_MAX_SIZE;
      } else if (height == HEIGHT_NOT_CALCULATED) {
        throw new ProcessingException("Failed to calculate height, could not parse javascript result to integer");
      } else if (height < MINIMUM_HEIGHT) {
        LOG.warn("Detected page height is 0, setting height to 1");
        height = MINIMUM_HEIGHT;
      }
    }
    LOG.info("Setting resolution to  {}x{}  ", width, height);
    webDriver.manage().window().setSize(new Dimension(width, height));
  }

  private int calculateWindowHeight() {
    Window window = webDriver.manage().window();
    window.setSize(new Dimension(width, INITIAL_HEIGHT));

    Supplier<Integer> heightSupplier = () -> {
      int heightResult = HEIGHT_NOT_CALCULATED;
      try {
        LOG.debug("Executing Resolution Modifier");
        heightResult = Integer.parseInt(
                jsExecutor.execute(JAVASCRIPT_GET_BODY_HEIGHT).getExecutionResultAsString());
      } catch (ProcessingException e) {
        // ignore exception
      }
      return heightResult;
    };
    return Sampler
        .waitForValue(heightSupplier, samplingPeriod, SAMPLE_QUEUE_SIZE, MAX_SAMPLES_THRESHOLD);
  }

}
