/**
 * AET
 *
 * Copyright (C) 2018 Cognifide Limited
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
package com.cognifide.aet.job.common.modifiers.scroll;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.utils.javascript.JavaScriptJobExecutor;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScrollModifierTest {

  private Map<String, String> parameters;

  @Mock
  private JavaScriptJobExecutor jsExecutor;


  @InjectMocks
  private ScrollModifier scrollModifier;

  @Before
  public void init() {
    parameters = new HashMap<>();
  }

  @Test
  public void testDefaultForEmptyParameters() throws ParametersException, ProcessingException {
    veryfiExecutionOfJs(1, ScrollModifierParamsParser.BOTTOM);
  }

  @Test
  public void testScrollToBottomExecution() throws ParametersException, ProcessingException {
    parameters.put("position", "bottom");
    veryfiExecutionOfJs(1, ScrollModifierParamsParser.BOTTOM);
  }

  @Test
  public void testScrollToTopExecution() throws ParametersException, ProcessingException {
    parameters.put("position", "top");
    veryfiExecutionOfJs(1, ScrollModifierParamsParser.TOP);
  }

  @Test
  public void testScrollToCssElementExecution() throws ParametersException, ProcessingException {
    parameters.put("css", "#element_id");
    veryfiExecutionOfJs(1, String.format(ScrollModifierParamsParser.CSS, "#element_id"));
  }

  @Test
  public void testScrollToXPathExecution() throws ParametersException, ProcessingException {
    parameters.put("xpath", "//*[@id='element_id']");
    veryfiExecutionOfJs(1,
        String.format(ScrollModifierParamsParser.XPATH, "//*[@id='element_id']"));
  }

  @Test(expected = ParametersException.class)
  public void testInvalidScrollPosition() throws ParametersException, ProcessingException {
    parameters.put("position", "middle");
    collect();
  }

  @Test(expected = ParametersException.class)
  public void testToManyParameters() throws ParametersException, ProcessingException {
    parameters.put("position", "top");
    parameters.put("css", "#id");
    collect();
  }

  private void veryfiExecutionOfJs(int numberOfInvocations, String snippet)
      throws ParametersException, ProcessingException {
    collect();
    verify(jsExecutor, times(numberOfInvocations)).execute(snippet);
  }

  private void collect() throws ParametersException, ProcessingException {
    scrollModifier.setParameters(parameters);
    scrollModifier.collect();
  }

}
