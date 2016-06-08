/*
 * Cognifide AET :: Job Common
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.comparators.layout.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageComparison - util class for fast images comparison
 *
 * @author Maciej Laskowski
 */
public final class ImageComparison {

	private static final Logger LOG = LoggerFactory.getLogger(ImageComparison.class);

	private static final int RGB_ERROR_COLOR = 16711680;

	private static final int ALPHA_SHIFT = 24;

	private static final int RED_SHIFT = 16;

	private static final int GREEN_SHIFT = 8;

	private static final int SHIFT_BASE = 0xFF;

	private static final int RGB_MAX_VALUE = 255;

	private static final int INVALID_PIXEL_COLOR = 125 <<ALPHA_SHIFT | 255 << RED_SHIFT | 0 << GREEN_SHIFT | 0;

	private static final int VALID_PIXEL_COLOR = 0;

	private ImageComparison() {
	}

	/**
	 * Compares two images, if images are with different dimensions, the output image's dimension is maxWidth
	 * x maxHeight
	 *
	 * @param pattern
	 * @param sample
	 * @return ImageComparisonResult
	 */
	public static ImageComparisonResult compare(final BufferedImage pattern, final BufferedImage sample) {
		LOG.debug("Starting comparison of images.");
		int minWidth = Math.min(pattern.getWidth(), sample.getWidth());
		int minHeight = Math.min(pattern.getHeight(), sample.getHeight());
		int widthDifference = Math.abs(pattern.getWidth() - sample.getWidth());
		int heightDifference = Math.abs(pattern.getHeight() - sample.getHeight());
		int resultWidth = minWidth + widthDifference;
		int resultHeight = minHeight + heightDifference;

		BufferedImage resultImage = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_ARGB);

		int[][] img1Pixels = convertImageTo2DArray(pattern);
		int[][] img2Pixels = convertImageTo2DArray(sample);

		int differenceCounter = fastCompareMatchingArea(minWidth, minHeight, resultImage.getRaster(),
				img1Pixels, img2Pixels);

		// Sets not covered areas as error (red)
		fastMarkOuterAreaAsError(minWidth, minHeight, widthDifference, heightDifference,
				resultImage.getRaster());

		LOG.debug("Returning comparison result of images.");
		return new ImageComparisonResult(differenceCounter, widthDifference, heightDifference, resultImage);
	}

	private static void fastMarkOuterAreaAsError(int minWidth, int minHeight, int widthDifference,
			int heightDifference, WritableRaster writableRaster) {
		// fil area [minWidth, 0, resultWidth, minHeight]
		int[] emptyAreaA = new int[widthDifference * minHeight];
		Arrays.fill(emptyAreaA, RGB_ERROR_COLOR);
		writableRaster.setDataElements(minWidth, 0, widthDifference, minHeight, emptyAreaA);

		// fil area [0, minHeight, minWidth, resultHeight]
		int[] emptyAreaB = new int[minWidth * heightDifference];
		Arrays.fill(emptyAreaB, RGB_ERROR_COLOR);
		writableRaster.setDataElements(0, minHeight, minWidth, heightDifference, emptyAreaB);
	}

	private static int fastCompareMatchingArea(int minWidth, int minHeight, WritableRaster writableRaster,
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

	private static int[][] convertImageTo2DArray(BufferedImage image) {

		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();

		int[][] result = new int[height][width];
		final int pixelLength = 4;
		int row = 0;
		int col = 0;
		for (int pixel = 0; pixel < pixels.length; pixel += pixelLength) {
			int argb = 0;
			argb += ((RGB_MAX_VALUE & SHIFT_BASE) << ALPHA_SHIFT); // alpha
			argb += ((int) pixels[pixel] & SHIFT_BASE); // blue
			argb += (((int) pixels[pixel + 1] & SHIFT_BASE) << GREEN_SHIFT); // green
			argb += (((int) pixels[pixel + 2] & SHIFT_BASE) << RED_SHIFT); // red
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
