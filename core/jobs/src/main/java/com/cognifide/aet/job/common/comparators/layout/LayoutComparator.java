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
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparison;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparisonResult;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class LayoutComparator implements ComparatorJob {

  public static final String COMPARATOR_TYPE = "screen";

  public static final String COMPARATOR_NAME = "layout";

  public static final String CONTENT_TYPE = "image/png";

  private final ComparatorProperties properties;

  private final ArtifactsDAO artifactsDAO;

  LayoutComparator(ComparatorProperties comparatorProperties, ArtifactsDAO artifactsDAO) {
    this.properties = comparatorProperties;
    this.artifactsDAO = artifactsDAO;
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
    if (imageComparisonResult.isMatch()) {
      result = getPassedStepResult();
    } else {
      InputStream mask = null;
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        ImageIO.write(imageComparisonResult.getResultImage(), "png", baos);
        mask = new ByteArrayInputStream(baos.toByteArray());
        String maskArtifactId = artifactsDAO.saveArtifact(properties, mask, CONTENT_TYPE);

        result = new ComparatorStepResult(maskArtifactId, ComparatorStepResult.Status.FAILED, true);

        result.addData("heightDifference",
            Integer.toString(imageComparisonResult.getHeightDifference()));
        result.addData("widthDifference",
            Integer.toString(imageComparisonResult.getWidthDifference()));
        result.addData("pixelDifference",
            Integer.toString(imageComparisonResult.getPixelDifferenceCount()));
        result.addData("patternTimestamp", Long.toString(
            artifactsDAO.getArtifactUploadDate(properties, properties.getPatternId()).getTime()));
        result.addData("collectTimestamp", Long.toString(
            artifactsDAO.getArtifactUploadDate(properties, properties.getCollectedId())
                .getTime()));
      } catch (Exception e) {
        throw new ProcessingException(e.getMessage(), e);
      } finally {
        IOUtils.closeQuietly(mask);
      }
    }

    return result;
  }

  private ComparatorStepResult getPassedStepResult() {
    ComparatorStepResult result = new ComparatorStepResult(null, ComparatorStepResult.Status.PASSED,
        false);
    result.addData("patternTimestamp", Long.toString(
        artifactsDAO.getArtifactUploadDate(properties, properties.getPatternId()).getTime()));
    result.addData("collectTimestamp", Long.toString(System.currentTimeMillis()));
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    // no parameters needed
  }

}
