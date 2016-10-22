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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class AssertFor {

  private final ImageComparisonResult imageComparisonResult;

  AssertFor(ImageComparisonResult imageComparisonResult) {
    this.imageComparisonResult = imageComparisonResult;
  }

  /**
   * Checks if compared images was same
   *
   * @param comparedImagesMatch <code>true</code> if compared images are expected to be same
   */
  AssertFor sameImages(boolean comparedImagesMatch) {
    assertThat(imageComparisonResult.isMatch(), is(comparedImagesMatch));
    return this;
  }

  AssertFor heightDifference(int expectedHeightDifference) {
    assertThat(imageComparisonResult.getHeightDifference(), is(expectedHeightDifference));
    return this;
  }

  AssertFor widthDifference(int expectedWidthDifference) {
    assertThat(imageComparisonResult.getWidthDifference(), is(expectedWidthDifference));
    return this;
  }

  AssertFor numberOfDifferentPixels(int expectedDifferentPixelsCount) {
    assertThat(imageComparisonResult.getPixelDifferenceCount(), is(expectedDifferentPixelsCount));
    return this;
  }
}
