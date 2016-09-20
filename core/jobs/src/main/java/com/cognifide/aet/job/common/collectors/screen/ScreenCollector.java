/**
 * Automated Exploratory Tests
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
package com.cognifide.aet.job.common.collectors.screen;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class ScreenCollector implements CollectorJob {

  public static final String NAME = "screen";
  public static final String CONTENT_TYPE = "image/png";

  private final WebDriver webDriver;

  private final ArtifactsDAO artifactsDAO;

  private final CollectorProperties properties;

  ScreenCollector(CollectorProperties properties, WebDriver webDriver, ArtifactsDAO artifactsDAO) {
    this.properties = properties;
    this.webDriver = webDriver;
    this.artifactsDAO = artifactsDAO;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    byte[] screenshot = takeScreenshot(webDriver);

    CollectorStepResult stepResult;
    if (isPatternAndResultMD5Identical(screenshot)) {
      stepResult = CollectorStepResult.newResultThatDuplicatesPattern(properties.getPatternId());
    } else {
      try (final InputStream screenshotStream = new ByteArrayInputStream(screenshot)) {
        String resultId = artifactsDAO.saveArtifact(properties, screenshotStream, CONTENT_TYPE);
        stepResult = CollectorStepResult.newCollectedResult(resultId);
      } catch (Exception e) {
        throw new ProcessingException(e.getMessage(), e);
      }
    }

    return stepResult;
  }

  private boolean isPatternAndResultMD5Identical(byte[] screenshot) {
    if (properties.getPatternId() != null) {
      final String screenshotMD5 = DigestUtils.md5Hex(screenshot);
      final String patternMD5 = artifactsDAO.getArtifactMD5(properties, properties.getPatternId());
      return StringUtils.equalsIgnoreCase(patternMD5, screenshotMD5);
    } else {
      return false;
    }
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //No parameters here
  }

  private byte[] takeScreenshot(WebDriver webDriver) throws ProcessingException {
    try {
      return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
    } catch (WebDriverException e) {
      throw new ProcessingException("Could not take screenshot, page is not loaded yet!", e);
    }
  }

}
