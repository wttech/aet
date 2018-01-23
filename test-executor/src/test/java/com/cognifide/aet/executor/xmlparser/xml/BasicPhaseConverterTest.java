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
package com.cognifide.aet.executor.xmlparser.xml;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;

@RunWith(MockitoJUnitRunner.class)
public class BasicPhaseConverterTest {

  @Mock
  private InputNode inputNode;

  @Test
  @SuppressWarnings("unchecked")
  public void getParameters_withNoAttributes_expectEmptyMap() throws Exception {
    NodeMap<InputNode> emptyNodeMap = Mockito.mock(NodeMap.class);
    when(emptyNodeMap.iterator()).thenReturn(Collections.emptyIterator());
    when(inputNode.getAttributes()).thenReturn(emptyNodeMap);
    Map<String, String> parameters = BasicPhaseConverter.getParameters(inputNode);
    assertTrue(parameters.isEmpty());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getParameters_withTwoAttributes_expectMapWithTwoProperties() throws Exception {
    NodeMap<InputNode> emptyNodeMap = Mockito.mock(NodeMap.class);
    when(inputNode.getAttributes()).thenReturn(emptyNodeMap);
    when(emptyNodeMap.iterator())
        .thenReturn(Arrays.asList("attribute1_key", "attribute2_key").iterator());
    InputNode inputValue1 = Mockito.mock(InputNode.class);
    when(inputValue1.getValue()).thenReturn("attribute1_value");
    InputNode inputValue2 = Mockito.mock(InputNode.class);
    when(inputValue2.getValue()).thenReturn("attribute2_value");
    when(inputNode.getAttribute("attribute1_key")).thenReturn(inputValue1);
    when(inputNode.getAttribute("attribute2_key")).thenReturn(inputValue2);
    Map<String, String> parameters = BasicPhaseConverter.getParameters(inputNode);
    assertThat(parameters.size(), is(2));
    assertThat(parameters.get("attribute1_key"), is("attribute1_value"));
    assertThat(parameters.get("attribute2_key"), is("attribute2_value"));
  }
}
