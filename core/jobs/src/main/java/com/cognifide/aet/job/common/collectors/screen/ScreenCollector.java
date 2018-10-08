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
package com.cognifide.aet.job.common.collectors.screen;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.communication.api.metadata.Payload;
import com.cognifide.aet.communication.api.metadata.exclude.ExcludedElement;
import com.cognifide.aet.communication.api.metadata.exclude.LayoutExclude;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.SeleniumWaitHelper;
import com.cognifide.aet.job.common.modifiers.WebElementsLocatorParams;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class ScreenCollector extends WebElementsLocatorParams implements CollectorJob {

  public static final String NAME = "screen";

  private static final String CONTENT_TYPE = "image/png";

  private static final String PNG_FORMAT = "png";

  private final WebDriver webDriver;

  private final ArtifactsDAO artifactsDAO;

  private final CollectorProperties properties;

  private String excludeCssSelector;

  ScreenCollector(CollectorProperties properties, WebDriver webDriver, ArtifactsDAO artifactsDAO) {
    this.properties = properties;
    this.webDriver = webDriver;
    this.artifactsDAO = artifactsDAO;
  }

  private List<ExcludedElement> getExcludeElementsFromWebElements(List<WebElement> webElements) {
    List<ExcludedElement> excludeExcludedElements = new ArrayList<>(webElements.size());

    Point screenshotOffset = isSelectorPresent() ?
        webDriver.findElement(getLocator()).getLocation() : new Point(0, 0);
    for (WebElement webElement : webElements) {
      Point point = webElement.getLocation()
          .moveBy(-screenshotOffset.getX(), -screenshotOffset.getY());

      excludeExcludedElements.add(new ExcludedElement(
          new java.awt.Point(point.getX(), point.getY()),
          new java.awt.Dimension(webElement.getSize().width, webElement.getSize().height)));
    }
    return excludeExcludedElements;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    byte[] screenshot = takeScreenshot();

    CollectorStepResult stepResult;
    if (isPatternAndResultMD5Identical(screenshot)) {
      stepResult = CollectorStepResult.newResultThatDuplicatesPattern(properties.getPatternId());
    } else {
      try (final InputStream screenshotStream = new ByteArrayInputStream(screenshot)) {
        String resultId = artifactsDAO.saveArtifact(properties, screenshotStream, CONTENT_TYPE);

        if (excludeCssSelector != null) {
          List<ExcludedElement> excludeExcludedElements = getExcludeElementsFromWebElements(
              webDriver.findElements(By.cssSelector(excludeCssSelector)));
          stepResult = CollectorStepResult
              .newCollectedResult(resultId,
                  new Payload((new LayoutExclude(excludeExcludedElements))));
        } else {
          stepResult = CollectorStepResult.newCollectedResult(resultId);
        }
      } catch (Exception e) {
        throw new ProcessingException(e.getMessage(), e);
      }
    }
    return stepResult;
  }

  private boolean isPatternAndResultMD5Identical(byte[] screenshot) {
    if (properties.getPatternId() != null) {
      final String screenshotMD5 = DigestUtils.md5Hex(screenshot);
      final String patternMD5 = artifactsDAO
          .getArtifactMD5(properties, properties.getPatternId());
      return StringUtils.equalsIgnoreCase(patternMD5, screenshotMD5);
    } else {
      return false;
    }
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (StringUtils.isNotBlank(params.get(XPATH_PARAM)) || StringUtils
        .isNotBlank(params.get(CSS_PARAM))) {
      setElementParams(params);
    }
    if (StringUtils.isNotBlank(params.get(EXCLUDE_ELEMENT_PARAM))) {
      excludeCssSelector = params.get(EXCLUDE_ELEMENT_PARAM);
    }
  }

  private byte[] takeScreenshot() throws ProcessingException {
    try {
      if (isSelectorPresent()) {
        SeleniumWaitHelper
            .waitForElementToBePresent(webDriver, getLocator(), getTimeoutInSeconds());
        return getImagePart(getFullPageScreenshot(), webDriver.findElement(getLocator()));
      } else {
        return getFullPageScreenshot();
      }
    } catch (NoSuchElementException e) {
      throw new ProcessingException("Unable to find element for taking screenshot part", e);
    } catch (IOException | WebDriverException e) {
      throw new ProcessingException("Unable to take screenshot", e);
    }

  }

  private byte[] getFullPageScreenshot() {
    return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
  }

  private byte[] getImagePart(byte[] fullPage, WebElement webElement)
      throws IOException, ProcessingException {
    InputStream in = new ByteArrayInputStream(fullPage);
    try {
      BufferedImage fullImg = ImageIO.read(in);
      Point point = webElement.getLocation();
      Dimension size = webElement.getSize();
      BufferedImage screenshotSection = fullImg.getSubimage(point.getX(), point.getY(),
          size.getWidth(), size.getHeight());
      return bufferedImageToByteArray(screenshotSection);
    } catch (IOException e) {
      throw new ProcessingException("Unable to create image from taken screenshot", e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  private byte[] bufferedImageToByteArray(BufferedImage bufferedImage) throws ProcessingException {
    try (ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream()) {
      ImageIO.write(bufferedImage, PNG_FORMAT, temporaryStream);
      temporaryStream.flush();
      return temporaryStream.toByteArray();
    } catch (IOException e) {
      throw new ProcessingException("Unable to convert screenshot part to byte Array", e);
    }
  }
}
