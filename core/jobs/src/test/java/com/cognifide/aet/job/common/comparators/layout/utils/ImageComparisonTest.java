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

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ImageComparisonTest {

  @Test
  public void testCompare_identical() throws Exception {
    InputStream sampleStream = null;
    InputStream patternStream = null;
    InputStream maskStream = null;
    InputStream expectedMaskStream = null;
    try {
      sampleStream = getClass().getResourceAsStream("/mock/LayoutComparator/image.png");
      patternStream = getClass().getResourceAsStream("/mock/LayoutComparator/image.png");

      BufferedImage sample = ImageIO.read(sampleStream);
      BufferedImage pattern = ImageIO.read(patternStream);
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);

      assertThat(imageComparisonResult.isMatch(), is(true));
      assertThat(imageComparisonResult.getHeightDifference(), is(0));
      assertThat(imageComparisonResult.getWidthDifference(), is(0));
      assertThat(imageComparisonResult.getPixelDifferenceCount(), is(0));

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass().getResourceAsStream("/mock/LayoutComparator/mask-identical.png");

      assertThat(IOUtils.contentEquals(expectedMaskStream, maskStream), is(true));
    } finally {
      IOUtils.closeQuietly(sampleStream);
      IOUtils.closeQuietly(patternStream);
      IOUtils.closeQuietly(maskStream);
      IOUtils.closeQuietly(expectedMaskStream);
    }
  }

  @Test
  public void testCompare_different() throws Exception {
    InputStream sampleStream = null;
    InputStream patternStream = null;
    InputStream maskStream = null;
    InputStream expectedMaskStream = null;
    try {
      sampleStream = getClass().getResourceAsStream("/mock/LayoutComparator/image.png");
      patternStream = getClass().getResourceAsStream("/mock/LayoutComparator/image2.png");

      BufferedImage sample = ImageIO.read(sampleStream);
      BufferedImage pattern = ImageIO.read(patternStream);
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);

      assertThat(imageComparisonResult.isMatch(), is(false));
      assertThat(imageComparisonResult.getHeightDifference(), is(0));
      assertThat(imageComparisonResult.getWidthDifference(), is(0));
      assertThat(imageComparisonResult.getPixelDifferenceCount(), is(15600));

      maskStream = imageToStream(imageComparisonResult.getResultImage());
      expectedMaskStream = getClass().getResourceAsStream("/mock/LayoutComparator/mask-different.png");

      assertThat(IOUtils.contentEquals(maskStream, expectedMaskStream), is(true));
    } finally {
      IOUtils.closeQuietly(sampleStream);
      IOUtils.closeQuietly(patternStream);
      IOUtils.closeQuietly(maskStream);
      IOUtils.closeQuietly(expectedMaskStream);
    }
  }

  @Test
  public void testCompare_different_canvas() throws Exception {
    InputStream sampleStream = null;
    InputStream patternStream = null;
    InputStream maskStream = null;
    InputStream expectedMaskStream = null;
    try {
      sampleStream = getClass().getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/collected.png");
      patternStream = getClass().getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/pattern.png");

      BufferedImage sample = ImageIO.read(sampleStream);
      BufferedImage pattern = ImageIO.read(patternStream);
      ImageComparisonResult imageComparisonResult = ImageComparison.compare(pattern, sample);

      assertThat(imageComparisonResult.isMatch(), is(false));
      assertThat(imageComparisonResult.getHeightDifference(), is(100));
      assertThat(imageComparisonResult.getWidthDifference(), is(0));
      assertThat(imageComparisonResult.getPixelDifferenceCount(), is(10399));

      maskStream = imageToStream(imageComparisonResult.getResultImage());

      expectedMaskStream = getClass().getResourceAsStream("/mock/LayoutComparator/canvasSizeDiff/mask.png");
      assertThat(IOUtils.contentEquals(maskStream, expectedMaskStream), is(true));

    } finally {
      IOUtils.closeQuietly(sampleStream);
      IOUtils.closeQuietly(patternStream);
      IOUtils.closeQuietly(maskStream);
      IOUtils.closeQuietly(expectedMaskStream);
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

}
