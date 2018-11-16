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
package com.cognifide.aet.job.common.modifiers.resolution;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobResult;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

@RunWith(MockitoJUnitRunner.class)
public class ResolutionModifierTest {

  private static final String WIDTH_PARAM = "width";

  private static final String HEIGHT_PARAM = "height";

  private static final String NOT_A_NUMBER = "NaN";

  private static final String JAVASCRIPT_GET_BODY_HEIGHT = "return document.body.scrollHeight";

  private static final int WINDOW_WIDTH = 1024;

  private static final int WINDOW_HEIGHT = 768;

  private static final int CUSTOM_WIDTH = 800;

  private static final int CUSTOM_HEIGHT = 600;

  private static final int BROWSER_HEIGHT_LIMIT = 35000;

  @Mock
  private RemoteWebDriver webDriver;

  @Mock
  private Capabilities capabilities;

  @Mock
  private Map<String, String> params;

  @Mock
  private WebDriver.Options options;

  @Mock
  private WebDriver.Window window;

  @Mock
  private Dimension windowDimension;

  @Mock
  private JavaScriptJobExecutor jsExecutor;

  @InjectMocks
  private ResolutionModifier tested;

  @Before
  public void setUp() {
    when(webDriver.manage()).thenReturn(options);
    when(webDriver.getCapabilities()).thenReturn(capabilities);
    when(options.window()).thenReturn(window);
    when(window.getSize()).thenReturn(windowDimension);
    when(windowDimension.getWidth()).thenReturn(WINDOW_WIDTH);
    when(windowDimension.getHeight()).thenReturn(WINDOW_HEIGHT);
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_widthNotANumber() throws ParametersException {
    when(params.containsKey(WIDTH_PARAM)).thenReturn(true);
    when(params.get(WIDTH_PARAM)).thenReturn(NOT_A_NUMBER);

    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_heightNotANumber() throws ParametersException {
    when(params.containsKey(HEIGHT_PARAM)).thenReturn(true);
    when(params.get(HEIGHT_PARAM)).thenReturn(NOT_A_NUMBER);

    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_width0() throws ParametersException {
    when(params.containsKey(WIDTH_PARAM)).thenReturn(true);
    when(params.get(WIDTH_PARAM)).thenReturn("0");

    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParametersTest_height0() throws ParametersException {
    when(params.containsKey(HEIGHT_PARAM)).thenReturn(true);
    when(params.get(HEIGHT_PARAM)).thenReturn("0");

    tested.setParameters(params);
  }

  @Test
  public void collectTest_setWidthHeight() throws ParametersException, ProcessingException {
    when(params.containsKey(HEIGHT_PARAM)).thenReturn(true);
    when(params.containsKey(WIDTH_PARAM)).thenReturn(true);
    when(params.get(HEIGHT_PARAM)).thenReturn("" + CUSTOM_HEIGHT);
    when(params.get(WIDTH_PARAM)).thenReturn("" + CUSTOM_WIDTH);

    tested.setParameters(params);
    tested.collect();

    verify(windowDimension, never()).getWidth();
    verify(windowDimension, never()).getHeight();
    verify(window, times(1)).setSize(new Dimension(CUSTOM_WIDTH, CUSTOM_HEIGHT));
  }

  @Test(expected = ParametersException.class)
  public void collectTest_setOnlyHeight() throws ParametersException {
    when(params.containsKey(HEIGHT_PARAM)).thenReturn(true);
    when(params.get(HEIGHT_PARAM)).thenReturn("" + CUSTOM_HEIGHT);
    tested.setParameters(params);
  }

  private void setUp_setOnlyWidth(){
    when(params.containsKey(WIDTH_PARAM)).thenReturn(true);
    when(params.containsKey(HEIGHT_PARAM)).thenReturn(false);
    when(params.get(WIDTH_PARAM)).thenReturn("" + CUSTOM_WIDTH);
  }

  @Test
  public void collectTest_setOnlyWidth() throws ParametersException, ProcessingException {
    setUp_setOnlyWidth();
    when(jsExecutor.execute(JAVASCRIPT_GET_BODY_HEIGHT))
        .thenReturn(new JavaScriptJobResult(CUSTOM_HEIGHT));

    tested.setParameters(params);
    tested.collect();

    verify(windowDimension, never()).getWidth();
    verify(windowDimension, never()).getHeight();
    verify(window, times(1)).setSize(new Dimension(CUSTOM_WIDTH, CUSTOM_HEIGHT));
  }

  @Test
  public void collectTest_setOnlyWidth_reachedBrowserHeightLimit()
      throws ParametersException, ProcessingException {
    setUp_setOnlyWidth();
    when(jsExecutor.execute(JAVASCRIPT_GET_BODY_HEIGHT))
        .thenReturn(new JavaScriptJobResult(BROWSER_HEIGHT_LIMIT + 5000L));

    tested.setParameters(params);
    tested.collect();

    verify(windowDimension, never()).getWidth();
    verify(windowDimension, never()).getHeight();
    verify(window, times(1)).setSize(new Dimension(CUSTOM_WIDTH, BROWSER_HEIGHT_LIMIT));
  }
}
