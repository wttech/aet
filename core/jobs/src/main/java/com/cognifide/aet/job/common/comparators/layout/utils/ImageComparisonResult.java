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


import java.awt.image.BufferedImage;
import java.util.Optional;

public class ImageComparisonResult {

  private int pixelCount;

  private int pixelDifferenceCount;

  private int heightDifference;

  private int widthDifference;

  private BufferedImage resultImage;

  private Optional<Integer> pixelTreshold;

  private Optional<Double> percentageTreshold;

  public ImageComparisonResult() {
    this(0, 0, 0, null);
  }

  public ImageComparisonResult(final int pixelDifferenceCount, final int widthDifference,
      final int heightDifference, final BufferedImage resultImage) {
    this.pixelDifferenceCount = pixelDifferenceCount;
    this.heightDifference = heightDifference;
    this.widthDifference = widthDifference;
    this.resultImage = resultImage;
    this.pixelCount = resultImage.getHeight() * resultImage.getWidth();
    this.percentageTreshold = Optional.empty();
    this.pixelTreshold = Optional.empty();
  }

  public boolean isMatch() {
    if (this.pixelTreshold.isPresent() || this.percentageTreshold.isPresent()) {
      return isAcceptablePixelChange() && isAcceptablePercentageChange();
    }
    return isNoChange();
  }

  private boolean isNoChange() {
    return this.pixelDifferenceCount == 0 && this.heightDifference == 0
        && this.widthDifference == 0;
  }

  private boolean isAcceptablePixelChange() {
    return this.pixelTreshold.map(integer -> this.pixelDifferenceCount <= integer).orElse(true);
  }

  private boolean isAcceptablePercentageChange() {
    return this.percentageTreshold
        .map(aDouble -> 100 * this.pixelDifferenceCount / (double)this.pixelCount <= aDouble).orElse(true);
  }

  public int getPixelDifferenceCount() {
    return this.pixelDifferenceCount;
  }

  public int getHeightDifference() {
    return this.heightDifference;
  }

  public int getWidthDifference() {
    return this.widthDifference;
  }

  public BufferedImage getResultImage() {
    return resultImage;
  }

  public void setPixelTreshold(Optional<Integer> pixelTreshold) {
    this.pixelTreshold = pixelTreshold;
  }

  public void setPercentageTreshold(Optional<Double> percentageTreshold) {
    this.percentageTreshold = percentageTreshold;
  }

}
