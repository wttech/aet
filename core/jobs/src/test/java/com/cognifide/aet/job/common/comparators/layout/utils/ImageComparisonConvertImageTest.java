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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ImageComparisonConvertImageTest {

  private InputStream patternStream;
  private InputStream sampleStream;

  private List<InputStream> imageStreams = Arrays.asList(patternStream, sampleStream);

  @Test
  public void convertImageTo2DArray_2x1Image_expectFirstPixelSame() throws Exception {
    try {
      // given
      patternStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/comparingColors/2x1-red.png");
      BufferedImage pattern = ImageIO.read(patternStream);
      sampleStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/comparingColors/2x1-red-black.png");
      BufferedImage sample = ImageIO.read(sampleStream);
      // when
      int[][] patternPixels = ImageComparison.convertImageTo2DArray(pattern);
      int[][] samplePixels = ImageComparison.convertImageTo2DArray(sample);
      // then
      int patternPixel = patternPixels[0][0];
      int samplePixel = samplePixels[0][0];
      assertThat(patternPixel, is(equalTo(samplePixel)));
    } finally {
      closeInputStreams(imageStreams);
    }
  }

  @Test
  public void convertImageTo2DArray_2x1Image_expectSecondPixelDifferent() throws Exception {
    try {
      // given
      patternStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/comparingColors/2x1-red.png");
      BufferedImage pattern = ImageIO.read(patternStream);
      sampleStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/comparingColors/2x1-red-black.png");
      BufferedImage sample = ImageIO.read(sampleStream);
      // when
      int[][] patternPixels = ImageComparison.convertImageTo2DArray(pattern);
      int[][] samplePixels = ImageComparison.convertImageTo2DArray(sample);
      // then
      int patternPixel = patternPixels[0][1];
      int samplePixel = samplePixels[0][1];
      assertThat(patternPixel, is(not(equalTo(samplePixel))));
    } finally {
      closeInputStreams(imageStreams);
    }
  }

  @Test
  public void convertImageTo2DArray_1x4Gradients_expectLastPixelSame() throws Exception {
    try {
      // given
      patternStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/comparingColors/1x4-blue-gradient.png");
      BufferedImage pattern = ImageIO.read(patternStream);
      sampleStream = getClass()
          .getResourceAsStream("/mock/LayoutComparator/comparingColors/1x4-grey-gradient.png");
      BufferedImage sample = ImageIO.read(sampleStream);
      // when
      int[][] patternPixels = ImageComparison.convertImageTo2DArray(pattern);
      int[][] samplePixels = ImageComparison.convertImageTo2DArray(sample);
      // then
      int patternPixel = patternPixels[3][0];
      int samplePixel = samplePixels[3][0];
      assertThat(patternPixel, is(equalTo(samplePixel)));
    } finally {
      closeInputStreams(imageStreams);
    }
  }

  private void closeInputStreams(List<InputStream> inputStreams) {
    for (InputStream inputStream : inputStreams) {
      IOUtils.closeQuietly(inputStream);
    }
  }
}
