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
import static org.hamcrest.Matchers.not;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * When preparing images for unit test keep in mind that <code>ImageComparison</code>
 * expects images to contain only 3 channels: red, green and blue (no alpha!).
 */
public class ImageComparisonTest {

  private InputStream patternStream;
  private InputStream sampleStream;
  private InputStream maskStream;
  private InputStream expectedMaskStream;

  private List<InputStream> imageStreams = Arrays.asList(patternStream, sampleStream,
          maskStream, expectedMaskStream);

  /**
   * To ensure every test will have its own initialization.
   */
  @Before
  public void resetImageStreams() {
    sampleStream = null;
    patternStream = null;
    maskStream = null;
    expectedMaskStream = null;
  }

  @Test
  public void test_sameScreenshot_expectNoDifferencesInResultAndTransparentMask() throws Exception {
    try {
      // given
      patternStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/image.png");
      BufferedImage pattern = ImageIO.read(patternStream);
      sampleStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/image.png");
      BufferedImage sample = ImageIO.read(sampleStream);
      // when
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);
      // then
      new ComparisonResultAssertions(imageComparisonResult).areSame(true)
              .heightDifferenceIs(0).widthDifferenceIs(0).numberOfDifferentPixelsIs(0);
      // and
      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/mask-identical.png");
      assertThat(IOUtils.contentEquals(expectedMaskStream, maskStream), is(true));
    } finally {
      closeInputStreams(imageStreams);
    }
  }

  @Test
  public void compare_differentScreenshots_expectDifferenceMask() throws Exception {
    try {
      // given
      patternStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/image2.png");
      BufferedImage pattern = ImageIO.read(patternStream);
      sampleStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/image.png");
      BufferedImage sample = ImageIO.read(sampleStream);
      // when
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);
      // then
      new ComparisonResultAssertions(imageComparisonResult).areSame(false)
              .heightDifferenceIs(0).widthDifferenceIs(0).numberOfDifferentPixelsIs(15600);
      // and
      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/mask-different.png");
      assertThat(IOUtils.contentEquals(maskStream, expectedMaskStream), is(true));
    } finally {
      closeInputStreams(imageStreams);
    }
  }

  @Test
  public void compare_differentSizeScreenshots_expectSizeDifferenceMarkedWithYellow() throws Exception {
    try {
      // given
      patternStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/pattern.png");
      BufferedImage pattern = ImageIO.read(patternStream);
      sampleStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/collected.png");
      BufferedImage sample = ImageIO.read(sampleStream);
      // when
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);
      // then
      new ComparisonResultAssertions(imageComparisonResult).areSame(false)
              .heightDifferenceIs(100).widthDifferenceIs(20).numberOfDifferentPixelsIs(14399);
      // and
      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass()
              .getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/mask.png");
      assertThat(IOUtils.contentEquals(maskStream, expectedMaskStream), is(true));
    } finally {
      closeInputStreams(imageStreams);
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

  private void closeInputStreams(List<InputStream> inputStreams) {
    for(InputStream inputStream : inputStreams){
      IOUtils.closeQuietly(inputStream);
    }
  }
}
