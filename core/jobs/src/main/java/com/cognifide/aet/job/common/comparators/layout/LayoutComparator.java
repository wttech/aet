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
package com.cognifide.aet.job.common.comparators.layout;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Payload;
import com.cognifide.aet.communication.api.metadata.exclude.ExcludedElement;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparison;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparisonResult;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class LayoutComparator implements ComparatorJob {

  public static final String COMPARATOR_TYPE = "screen";

  public static final String COMPARATOR_NAME = "layout";

  public static final String CONTENT_TYPE = "image/png";

  public static final String PERCENTAGE_THRESHOLD_PARAM = "percentageThreshold";

  public static final String PIXEL_THRESHOLD_PARAM = "pixelThreshold";

  private final ComparatorProperties properties;

  private final ArtifactsDAO artifactsDAO;

  private Integer pixelThreshold;

  private Double percentageThreshold;

  private boolean excludeFunctionIsOn;

  private boolean excludeElementsNotFound;

  LayoutComparator(ComparatorProperties comparatorProperties, ArtifactsDAO artifactsDAO) {
    this.properties = comparatorProperties;
    this.artifactsDAO = artifactsDAO;
  }

  private void hideElementsInImg(BufferedImage img, List<ExcludedElement> excludedElements) {
    Graphics graphics = img.getGraphics();
    for (ExcludedElement excludedElement : excludedElements) {
      graphics.setColor(Color.CYAN);
      graphics.fillRect(excludedElement.getPoint().x, excludedElement.getPoint().y,
          excludedElement.getDimension().width,
          excludedElement.getDimension().height);
    }
    graphics.dispose();
  }

  @Override
  public ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult stepResult;
    ImageComparisonResult imageComparisonResult;
    if (areInputsIdentical(artifactsDAO, properties)) {
      stepResult = getPassedStepResult();
    } else {
      try (InputStream collectedArtifact = artifactsDAO
          .getArtifact(properties, properties.getCollectedId()).getArtifactStream();
          InputStream patternArtifact = artifactsDAO
              .getArtifact(properties, properties.getPatternId()).getArtifactStream()) {

        BufferedImage patternImg = ImageIO.read(patternArtifact);
        BufferedImage collectedImg = ImageIO.read(collectedArtifact);

        Optional.ofNullable(properties.getPayload())
            .map(Payload::getLayoutExclude)
            .ifPresent(exclude -> {
              excludeFunctionIsOn = true;
              List<ExcludedElement> excludedElements = exclude.getExcludedElements();
              if (!CollectionUtils.isEmpty(excludedElements)) {
                hideElementsInImg(patternImg, excludedElements);
                hideElementsInImg(collectedImg, excludedElements);
              } else {
                excludeElementsNotFound = true;
              }
            });

        imageComparisonResult = ImageComparison.compare(patternImg, collectedImg);
        stepResult = saveArtifacts(imageComparisonResult);
      } catch (IOException e) {
        throw new ProcessingException("Error while obtaining artifacts!", e);
      }
    }

    return stepResult;
  }

  private boolean areInputsIdentical(ArtifactsDAO artifactsDAO, ComparatorProperties properties) {
    String collectedMD5 = artifactsDAO.getArtifactMD5(properties, properties.getCollectedId());
    String patternMD5 = artifactsDAO.getArtifactMD5(properties, properties.getPatternId());
    return StringUtils.equalsIgnoreCase(collectedMD5, patternMD5);
  }

  private ComparatorStepResult saveArtifacts(ImageComparisonResult imageComparisonResult)
      throws ProcessingException {
    final ComparatorStepResult result;
    InputStream mask = null;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ImageIO.write(imageComparisonResult.getResultImage(), "png", baos);
      mask = new ByteArrayInputStream(baos.toByteArray());
      String maskArtifactId = artifactsDAO.saveArtifact(properties, mask, CONTENT_TYPE);

      if (!excludeFunctionIsOn && isMaskWithoutDifference(imageComparisonResult)) {
        result = getPassedStepResult();
      } else if (hasMaskThresholdWithAcceptableDifference(imageComparisonResult)
          || isMaskWithoutDifference(imageComparisonResult)) {
        result = new ComparatorStepResult(maskArtifactId, Status.CONDITIONALLY_PASSED, true);
      } else {
        result = new ComparatorStepResult(maskArtifactId, Status.FAILED, true);
      }

      if (excludeElementsNotFound) {
        addExcludeMessageToResult(result, "Elements to exclude are not found on page");
      }

      addPixelDifferenceDataToResult(result, imageComparisonResult);
      addTimestampToResult(result);
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(mask);
    }

    return result;
  }

  boolean hasMaskThresholdWithAcceptableDifference(ImageComparisonResult mask) {
    if (pixelThreshold != null && percentageThreshold != null) {
      return isAcceptablePixelChange(mask) && this.isAcceptablePercentageChange(mask);
    } else if (pixelThreshold != null) {
      return isAcceptablePixelChange(mask);
    } else if (percentageThreshold != null) {
      return isAcceptablePercentageChange(mask);
    }
    return false;
  }

  public void setPixelThreshold(Integer pixelThreshold) {
    this.pixelThreshold = pixelThreshold;
  }

  public void setPercentageThreshold(Double percentageThreshold) {
    this.percentageThreshold = percentageThreshold;
  }

  private boolean isAcceptablePixelChange(ImageComparisonResult mask) {
    return mask.getPixelDifferenceCount() <= this.pixelThreshold;
  }

  private boolean isAcceptablePercentageChange(ImageComparisonResult mask) {
    return mask.getPercentagePixelDifference() <= this.percentageThreshold;
  }

  private boolean isMaskWithoutDifference(ImageComparisonResult mask) {
    return mask.getHeightDifference() == 0 && mask.getWidthDifference() == 0
        && mask.getPixelDifferenceCount() == 0;
  }

  private void addPixelDifferenceDataToResult(ComparatorStepResult result,
      ImageComparisonResult imageComparisonResult) {
    result.addData("heightDifference",
        Integer.toString(imageComparisonResult.getHeightDifference()));
    result.addData("widthDifference",
        Integer.toString(imageComparisonResult.getWidthDifference()));
    result.addData("pixelDifference",
        Integer.toString(imageComparisonResult.getPixelDifferenceCount()));
    result.addData("percentagePixelDifference",
        Double.toString(imageComparisonResult.getPercentagePixelDifference()));
  }

  private void addExcludeMessageToResult(ComparatorStepResult result, String message) {
    result.addData("excludeMessage", message);
  }

  private void addTimestampToResult(ComparatorStepResult result) {
    result.addData("patternTimestamp", Long.toString(
        artifactsDAO.getArtifactUploadDate(properties, properties.getPatternId()).getTime()));
    result.addData("collectTimestamp", Long.toString(System.currentTimeMillis()));
  }

  private ComparatorStepResult getPassedStepResult() {
    ComparatorStepResult result = new ComparatorStepResult(null, ComparatorStepResult.Status.PASSED,
        false);
    addTimestampToResult(result);
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (params.containsKey(PERCENTAGE_THRESHOLD_PARAM)) {
      setPercentageThreshold(Double.valueOf(params.get(PERCENTAGE_THRESHOLD_PARAM)));
      ParametersValidator
          .checkRange(percentageThreshold.intValue(), 0, 100,
              "Percentage threshold should be a decimal value between 0 and 100");
    }
    if (params.containsKey(PIXEL_THRESHOLD_PARAM)) {
      setPixelThreshold(Integer.valueOf(params.get(PIXEL_THRESHOLD_PARAM)));
      ParametersValidator.checkParameter(pixelThreshold >= 0,
          "Pixel threshold should be greater or equal to 0");
    }
  }

}
