/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.resolution;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
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

  private static final int HEIGHT_VALUE_IF_PARAMETER_MISSING = 1;

  private static final int MAX_SIZE = 100000;

  private final WebDriver webDriver;

  private int width;

  private int height = HEIGHT_VALUE_IF_PARAMETER_MISSING;

  ResolutionModifier(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    setResolution(this.webDriver);
    return CollectorStepResult.newModifierResult();
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (params.containsKey(WIDTH_PARAM)) {
      width = Integer.parseInt(params.get(WIDTH_PARAM));
      ParametersValidator.checkRange(width, 1, MAX_SIZE, "Width should be greater than 0");
      if (params.containsKey(HEIGHT_PARAM)) {
        height = Integer.parseInt(params.get(HEIGHT_PARAM));
        ParametersValidator.checkRange(height, 1, MAX_SIZE, "Height should be greater than 0");
      }
    } else {
      throw new ParametersException("You have to specify width parameter!");
    }
  }

  private void setResolution(WebDriver webDriver) {
    Window window = webDriver.manage().window();
    LOG.info("Setting resolution to  {}x{}  ", width, height);
    window.setSize(new Dimension(width, height));
  }

}
