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
package com.cognifide.aet.job.common.comparators.layout.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

public class ImageComparisonTest {

  @Test
  public void test_sameScreenshot_expectNoDifferencesInResultAndTransparentMask() throws Exception {
    InputStream sampleStream = null;
    InputStream patternStream = null;
    InputStream maskStream = null;
    InputStream expectedMaskStream = null;
    try {
      // given
      sampleStream = getClass().getResourceAsStream("/mock/LayoutComparator/image.png");
      patternStream = getClass().getResourceAsStream("/mock/LayoutComparator/image.png");

      BufferedImage sample = ImageIO.read(sampleStream);
      BufferedImage pattern = ImageIO.read(patternStream);
      // when
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);
      // then
      assertThat(imageComparisonResult.getPercentagePixelDifference(), is(0.0));
      assertThat(imageComparisonResult.getHeightDifference(), is(0));
      assertThat(imageComparisonResult.getWidthDifference(), is(0));
      assertThat(imageComparisonResult.getPixelDifferenceCount(), is(0));

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/mask-identical.png");

      assertThat(IOUtils.contentEquals(expectedMaskStream, maskStream), is(true));
    } finally {
      closeInputStreams(sampleStream, patternStream, maskStream, expectedMaskStream);
    }
  }

  @Test
  public void testCompare_different() throws Exception {
    InputStream sampleStream = null;
    InputStream patternStream = null;
    InputStream maskStream = null;
    InputStream expectedMaskStream = null;
    try {
      // given
      sampleStream = getClass().getResourceAsStream("/mock/LayoutComparator/image.png");
      patternStream = getClass().getResourceAsStream("/mock/LayoutComparator/image2.png");

      BufferedImage sample = ImageIO.read(sampleStream);
      BufferedImage pattern = ImageIO.read(patternStream);
      // when
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);
      // then
      assertThat(imageComparisonResult.getPercentagePixelDifference(), is(0.7435369480668039));
      assertThat(imageComparisonResult.getHeightDifference(), is(0));
      assertThat(imageComparisonResult.getWidthDifference(), is(0));
      assertThat(imageComparisonResult.getPixelDifferenceCount(), is(15600));

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/mask-different.png");

      assertThat(IOUtils.contentEquals(maskStream, expectedMaskStream), is(true));
    } finally {
      closeInputStreams(sampleStream, patternStream, maskStream, expectedMaskStream);
    }
  }

  @Test
  public void compare_differentSizeScreenshots_expectSizeDifferenceMarkedWithYellow()
      throws Exception {
    InputStream sampleStream = null;
    InputStream patternStream = null;
    InputStream maskStream = null;
    InputStream expectedMaskStream = null;
    try {
      // given
      sampleStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/collected.png");
      patternStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/pattern.png");

      BufferedImage sample = ImageIO.read(sampleStream);
      BufferedImage pattern = ImageIO.read(patternStream);
      // when
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);
      // then
      assertThat(imageComparisonResult.getPercentagePixelDifference(), is(59.99583333333333));
      assertThat(imageComparisonResult.getHeightDifference(), is(100));
      assertThat(imageComparisonResult.getWidthDifference(), is(20));
      assertThat(imageComparisonResult.getPixelDifferenceCount(), is(14399));

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/mask.png");
      assertThat(IOUtils.contentEquals(maskStream, expectedMaskStream), is(true));
    } finally {
      closeInputStreams(sampleStream, patternStream, maskStream, expectedMaskStream);
    }
  }

  private InputStream imageToStream(BufferedImage image) throws IOException {
    ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, "png", imageStream);
      return new ByteArrayInputStream(imageStream.toByteArray());
    } finally {
      IOUtils.closeQuietly(imageStream);
    }
  }

  private void closeInputStreams(InputStream... inputStreams) {
    for (InputStream inputStream : inputStreams) {
      IOUtils.closeQuietly(inputStream);
    }
  }
}
