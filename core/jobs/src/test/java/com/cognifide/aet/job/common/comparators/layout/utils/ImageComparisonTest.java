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
package com.cognifide.aet.job.common.comparators.layout.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * When preparing images for unit test keep in mind that <code>ImageComparison</code>
 * expects images to contain only 3 channels: red, green and blue (no alpha!).
 */
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
      new AssertFor(imageComparisonResult).sameImages(true)
              .heightDifference(0).widthDifference(0).numberOfDifferentPixels(0);

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass().getResourceAsStream("/mock/LayoutComparator/mask-identical.png");

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
      new AssertFor(imageComparisonResult).sameImages(false)
              .heightDifference(0).widthDifference(0).numberOfDifferentPixels(15600);

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass().getResourceAsStream("/mock/LayoutComparator/mask-different.png");

      assertThat(IOUtils.contentEquals(maskStream, expectedMaskStream), is(true));
    } finally {
      closeInputStreams(sampleStream, patternStream, maskStream, expectedMaskStream);
    }
  }

  @Test
  public void compare_differentSizeScreenshots_expectSizeDifferenceMarkedWithYellow() throws Exception {
    InputStream sampleStream = null;
    InputStream patternStream = null;
    InputStream maskStream = null;
    InputStream expectedMaskStream = null;
    try {
      // given
      sampleStream = getClass().getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/collected.png");
      patternStream = getClass().getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/pattern.png");

      BufferedImage sample = ImageIO.read(sampleStream);
      BufferedImage pattern = ImageIO.read(patternStream);
      // when
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);
      // then
      new AssertFor(imageComparisonResult).sameImages(false)
              .heightDifference(100).widthDifference(20).numberOfDifferentPixels(14399);

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass().getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/mask.png");
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
