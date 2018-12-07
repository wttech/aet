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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Pattern;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparisonResult;
import com.cognifide.aet.vs.Artifact;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LayoutComparatorTest {

  private static final Pattern PATTERN_1 = new Pattern("1", null);

  private static final Pattern PATTERN_2 = new Pattern("2", null);

  private static final String ARTIFACT_MD5_1 = "1";

  private static final String ARTIFACT_MD5_2 = "2";

  private static final String COLLECTED_ID = "3";

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

    List<Pattern> patterns = Arrays.asList(PATTERN_1, PATTERN_2);
    when(comparatorProperties.getPatternsIds()).thenReturn(new HashSet<>(patterns));
    when(comparatorProperties.getCollectedId()).thenReturn(COLLECTED_ID);
    when(comparatorProperties.getPayload()).thenReturn(null);

    when(artifactsDAO.getArtifactUploadDate(any(), any())).thenReturn(new Date());
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
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(false));

    //when
    this.layoutComparator.setPixelThreshold(null);
    this.layoutComparator.setPercentageThreshold(12.566);

    //then
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
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
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(true));

    //when
    this.layoutComparator.setPixelThreshold(null);
    this.layoutComparator.setPercentageThreshold(12.567);

    //then
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
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
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(false));

    //when
    this.layoutComparator.setPixelThreshold(301);
    this.layoutComparator.setPercentageThreshold(12.566);

    //then
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
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
    assertThat(
        this.layoutComparator.hasMaskThresholdWithAcceptableDifference(imageComparisonResult),
        is(true));
  }

  @Test
  public void shouldReturnPassedComparatorResults_whenAllPatternsMatch()
      throws ProcessingException {
    //given
    when(artifactsDAO.getArtifactMD5(any(), eq(COLLECTED_ID))).thenReturn(ARTIFACT_MD5_1);
    when(artifactsDAO.getArtifactMD5(any(), eq(PATTERN_1.getPattern()))).thenReturn(ARTIFACT_MD5_1);
    when(artifactsDAO.getArtifactMD5(any(), eq(PATTERN_2.getPattern()))).thenReturn(ARTIFACT_MD5_1);

    //when
    List<ComparatorStepResult> comparatorStepResults = this.layoutComparator.compare();

    //then
    comparatorStepResults.forEach(result -> assertThat(result.getStatus(), equalTo(Status.PASSED)));
    assertThat(comparatorStepResults, hasSize(2));
  }

  @Test
  public void shouldReturnMultipleFailedComparatorResults_whenAllPatternsDontMatch()
      throws ProcessingException, IOException {
    //given
    when(artifactsDAO.getArtifactMD5(any(), eq(COLLECTED_ID))).thenReturn(ARTIFACT_MD5_1);
    when(artifactsDAO.getArtifactMD5(any(), eq(PATTERN_1.getPattern()))).thenReturn(ARTIFACT_MD5_2);
    when(artifactsDAO.getArtifactMD5(any(), eq(PATTERN_2.getPattern()))).thenReturn(ARTIFACT_MD5_2);

    when(artifactsDAO.getArtifact(any(), eq(COLLECTED_ID)))
        .thenReturn(getMockImageArtifact(Color.RED))
        .thenReturn(getMockImageArtifact(Color.RED));
    when(artifactsDAO.getArtifact(any(), eq(PATTERN_1.getPattern())))
        .thenReturn(getMockImageArtifact(Color.BLACK));
    when(artifactsDAO.getArtifact(any(), eq(PATTERN_2.getPattern())))
        .thenReturn(getMockImageArtifact(Color.BLUE));

    //when
    List<ComparatorStepResult> comparatorStepResults = this.layoutComparator.compare();

    //then
    comparatorStepResults.forEach(result -> assertThat(result.getStatus(), equalTo(Status.FAILED)));
    assertThat(comparatorStepResults, hasSize(2));
  }

  @Test
  public void shouldReturnPassedAndFailedComparatorResults_whenSomePatternsMatch()
      throws IOException, ProcessingException {
    //given
    when(artifactsDAO.getArtifactMD5(any(), eq(COLLECTED_ID))).thenReturn(ARTIFACT_MD5_1);
    when(artifactsDAO.getArtifactMD5(any(), eq(PATTERN_1.getPattern()))).thenReturn(ARTIFACT_MD5_2);
    when(artifactsDAO.getArtifactMD5(any(), eq(PATTERN_2.getPattern()))).thenReturn(ARTIFACT_MD5_2);

    when(artifactsDAO.getArtifact(any(), eq(COLLECTED_ID)))
        .thenReturn(getMockImageArtifact(Color.GREEN))
        .thenReturn(getMockImageArtifact(Color.GREEN));
    when(artifactsDAO.getArtifact(any(), eq(PATTERN_1.getPattern())))
        .thenReturn(getMockImageArtifact(Color.GREEN));
    when(artifactsDAO.getArtifact(any(), eq(PATTERN_2.getPattern())))
        .thenReturn(getMockImageArtifact(Color.RED));

    //when
    List<ComparatorStepResult> comparatorStepResults = this.layoutComparator.compare();

    //then
    assertThat(comparatorStepResults.get(0).getStatus(), equalTo(Status.PASSED));
    assertThat(comparatorStepResults.get(1).getStatus(), equalTo(Status.FAILED));
    assertThat(comparatorStepResults, hasSize(2));
  }

  private Artifact getMockImageArtifact(Color seed) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(2, 2, 1);
    bufferedImage.setRGB(0, 0, seed.getRGB());

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "jpg", os);
    InputStream is = new ByteArrayInputStream(os.toByteArray());

    os.flush();
    return new Artifact(is, StringUtils.EMPTY);
  }
}
