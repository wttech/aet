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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparisonResult;
import com.cognifide.aet.vs.ArtifactsDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LayoutComparatorTest {

  @Mock
  private ComparatorProperties comparatorProperties;

  @Mock
  private ArtifactsDAO artifactsDAO;

  @Mock
  private ImageComparisonResult imageComparisonResult;

  private LayoutComparator layoutComparator;

  @Before
  public void setUp() {
    //given
    this.layoutComparator = new LayoutComparator(this.comparatorProperties, this.artifactsDAO);
  }

  @Test
  public void hasMaskThresholdWithAcceptableDifference_withoutThreshold_expectFalse() {
    //when
    when(imageComparisonResult.getPercentagePixelDifference()).thenReturn(12.567);
    when(imageComparisonResult.getPixelDifferenceCount()).thenReturn(300);

    //then
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(false));
  }

  @Test
  public void hasMaskThresholdWithAcceptableDifference_withThreshold_expectFalse() {
    //when
    when(imageComparisonResult.getPercentagePixelDifference()).thenReturn(12.567);
    when(imageComparisonResult.getPixelDifferenceCount()).thenReturn(300);

    this.layoutComparator.setPixelThreshold(299);
    this.layoutComparator.setPercentageThreshold(null);

    //then
    assertThat(this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(false));

    //when
    this.layoutComparator.setPixelThreshold(null);
    this.layoutComparator.setPercentageThreshold(12.566);

    //then
    assertThat(this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(false));
  }

  @Test
  public void hasMaskThresholdWithAcceptableDifference_withThreshold_expectTrue() {
    //when
    when(imageComparisonResult.getPercentagePixelDifference()).thenReturn(12.567);
    when(imageComparisonResult.getPixelDifferenceCount()).thenReturn(300);

    this.layoutComparator.setPixelThreshold(300);
    this.layoutComparator.setPercentageThreshold(null);

    //then
    assertThat(this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(true));

    //when
    this.layoutComparator.setPixelThreshold(null);
    this.layoutComparator.setPercentageThreshold(12.567);

    //then
    assertThat(this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(true));
  }

  @Test
  public void hasMaskThresholdWithAcceptableDifference_withBothThreshold_expectFalse() {
    //when
    when(imageComparisonResult.getPercentagePixelDifference()).thenReturn(12.567);
    when(imageComparisonResult.getPixelDifferenceCount()).thenReturn(300);

    this.layoutComparator.setPixelThreshold(299);
    this.layoutComparator.setPercentageThreshold(30.0);

    //then
    assertThat(this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(false));

    //when
    this.layoutComparator.setPixelThreshold(301);
    this.layoutComparator.setPercentageThreshold(12.566);

    //then
    assertThat(this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(false));
  }

  @Test
  public void hasMaskThresholdWithAcceptableDifference_withBothThreshold_expectTrue() {
    //when
    when(imageComparisonResult.getPercentagePixelDifference()).thenReturn(12.567);
    when(imageComparisonResult.getPixelDifferenceCount()).thenReturn(300);

    this.layoutComparator.setPixelThreshold(300);
    this.layoutComparator.setPercentageThreshold(12.567);

    //then
    assertThat(this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(true));
  }

}
