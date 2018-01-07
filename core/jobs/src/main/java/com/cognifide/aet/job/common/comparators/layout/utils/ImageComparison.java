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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageComparison - util class for fast images comparison. This class expects compared images to
 * contain four channels: red, green, blue and alpha.
 *
 * @author Maciej Laskowski and Tomasz Misiewicz
 */
public final class ImageComparison {

  private static final Logger LOG = LoggerFactory.getLogger(ImageComparison.class);

  // red, green, blue, alpha
  private static final int CHANNELS_IN_IMAGE = 4;

  private static final int CHANNEL_VALUE_MASK = 0xFF;

  private static final int INVALID_PIXEL_COLOR = new Color(255, 0, 0, 125).getRGB();

  private static final int CANVAS_DIFF_COLOR = new Color(255, 255, 0, 125).getRGB();

  private static final int VALID_PIXEL_COLOR = new Color(0, 0, 0, 0).getRGB();

  private ImageComparison() {
  }

  /**
   * Compares two images, if images are with different dimensions, the output image's dimension is
   * maxWidth x maxHeight
   *
   * @param pattern - saved image of how screen should look like
   * @param sample - actual screen collected during test
   * @return ImageComparisonResult
   */
  public static ImageComparisonResult compare(final BufferedImage pattern,
      final BufferedImage sample) {
    LOG.debug("Starting comparison of images.");
    int minWidth = Math.min(pattern.getWidth(), sample.getWidth());
    int minHeight = Math.min(pattern.getHeight(), sample.getHeight());
    int widthDifference = Math.abs(pattern.getWidth() - sample.getWidth());
    int heightDifference = Math.abs(pattern.getHeight() - sample.getHeight());
    int resultWidth = minWidth + widthDifference;
    int resultHeight = minHeight + heightDifference;

    BufferedImage resultImage = new BufferedImage(resultWidth, resultHeight,
        BufferedImage.TYPE_INT_ARGB);

    int[][] img1Pixels = convertImageTo2DArray(pattern);
    int[][] img2Pixels = convertImageTo2DArray(sample);

    int differenceCounter = fastCompareMatchingArea(minWidth, minHeight, resultImage.getRaster(),
        img1Pixels, img2Pixels);

    differenceCounter += fastMarkOuterAreaAsError(minWidth, minHeight, widthDifference,
        heightDifference,
        resultHeight, resultImage.getRaster());

    LOG.debug("Returning comparison result of images.");
    return new ImageComparisonResult(differenceCounter, widthDifference, heightDifference,
        resultImage);
  }

  private static int fastMarkOuterAreaAsError(int minWidth, int minHeight, int widthDifference,
      int heightDifference, int resultHeight, WritableRaster writableRaster) {
    // fill right area
    int[] emptyAreaA = new int[widthDifference * resultHeight];
    Arrays.fill(emptyAreaA, CANVAS_DIFF_COLOR);
    writableRaster.setDataElements(minWidth, 0, widthDifference, resultHeight, emptyAreaA);

    // fill bottom area
    int[] emptyAreaB = new int[minWidth * heightDifference];
    Arrays.fill(emptyAreaB, CANVAS_DIFF_COLOR);
    writableRaster.setDataElements(0, minHeight, minWidth, heightDifference, emptyAreaB);

    return emptyAreaA.length + emptyAreaB.length;
  }

  private static int fastCompareMatchingArea(int minWidth, int minHeight,
      WritableRaster writableRaster,
      int[][] img1Pixels, int[][] img2Pixels) {
    int differenceCounter = 0;
    int[] pixels = new int[minWidth * minHeight];
    int i = 0;

    // compare matching area and set comparison result to output raster
    for (int row = 0; row < minHeight; ++row) {
      for (int col = 0; col < minWidth; ++col) {
        int newRGB;
        if (img1Pixels[row][col] == img2Pixels[row][col]) {
          newRGB = VALID_PIXEL_COLOR;
        } else {
          newRGB = INVALID_PIXEL_COLOR;
          ++differenceCounter;
        }
        pixels[i++] = newRGB;
      }
    }
    writableRaster.setDataElements(0, 0, minWidth, minHeight, pixels);
    return differenceCounter;
  }

  // package-scoped for unit test
  static int[][] convertImageTo2DArray(BufferedImage image) {
    final byte[] imageBytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    final int width = image.getWidth();
    final int height = image.getHeight();

    int[][] result = new int[height][width];
    int row = 0;
    int col = 0;
    for (int pixel = 0; pixel < imageBytes.length; pixel += CHANNELS_IN_IMAGE) {

      int red = imageBytes[pixel + 3] & CHANNEL_VALUE_MASK;
      int green = imageBytes[pixel + 2] & CHANNEL_VALUE_MASK;
      int blue = imageBytes[pixel + 1] & CHANNEL_VALUE_MASK;
      int alpha = imageBytes[pixel] & CHANNEL_VALUE_MASK;

      int argb = new Color(red, green, blue, alpha).getRGB();
      result[row][col] = argb;
      col++;
      if (col == width) {
        col = 0;
        row++;
      }
    }
    return result;
  }
}
