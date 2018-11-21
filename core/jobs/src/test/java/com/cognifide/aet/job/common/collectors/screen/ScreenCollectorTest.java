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
package com.cognifide.aet.job.common.collectors.screen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.communication.api.metadata.CollectorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Pattern;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.Collections;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

@RunWith(MockitoJUnitRunner.class)
public class ScreenCollectorTest {

  private static final String PATTERN_ARTIFACT_ID = "foo";

  private static final byte[] SCREENSHOT = {1, 2, 3, 4};

  private static final String PATTERN_MD5 = DigestUtils.md5Hex(SCREENSHOT);

  @Mock
  private MockableDriver driver;

  @Mock
  private ArtifactsDAO artifactsDAO;

  @Mock
  private CollectorProperties properties;

  @InjectMocks
  private ScreenCollector screenCollector;

  @Test
  public void shouldReturnDuplicatesPatternResult_WhenScreenshotAndAllPatternsMd5Identical()
      throws ProcessingException {
    when(driver.getScreenshotAs(any())).thenReturn(SCREENSHOT);
    when(properties.getPatternsIds()).thenReturn(Collections.singleton(new Pattern(
        PATTERN_ARTIFACT_ID)));
    when(artifactsDAO.getArtifactMD5(any(), eq(PATTERN_ARTIFACT_ID))).thenReturn(PATTERN_MD5);

    CollectorStepResult stepResult = screenCollector.collect();

    assertEquals(Status.DUPLICATES_PATTERN, stepResult.getStatus());
    assertEquals(PATTERN_ARTIFACT_ID, stepResult.getArtifactId());
  }

  interface MockableDriver extends WebDriver, TakesScreenshot {

  }
}