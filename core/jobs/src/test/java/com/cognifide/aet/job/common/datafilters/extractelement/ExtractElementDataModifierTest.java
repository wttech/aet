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
package com.cognifide.aet.job.common.datafilters.extractelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class ExtractElementDataModifierTest {

  private ExtractElementDataModifier extractElementDataModifier;

  @Before
  public void setUp() throws Exception {
    extractElementDataModifier = new ExtractElementDataModifier();
  }

  @Test
  public void setParameters_whenElementIdIsDefined_elementIdIsNotNull() throws ParametersException {
    Map<String, String> parametersMap = new HashMap<String, String>();
    parametersMap.put(ExtractElementDataModifier.PARAM_ELEMENT_ID, "some_element_id");

    extractElementDataModifier.setParameters(parametersMap);

    assertNotNull(extractElementDataModifier.getElementId());
  }

  @Test
  public void setParameters_whenClassIsDefined_classIsNotNull() throws ParametersException {
    Map<String, String> parametersMap = new HashMap<String, String>();
    parametersMap.put(ExtractElementDataModifier.PARAM_ELEMENT_CLASS, "some_class");

    extractElementDataModifier.setParameters(parametersMap);

    assertNotNull(extractElementDataModifier.getClassAttribute());
  }

  @Test(expected = ParametersException.class)
  public void setParameters_whenElementIdAndClassAreDefined_throwException()
      throws ParametersException {
    Map<String, String> parametersMap = new HashMap<String, String>();
    parametersMap.put(ExtractElementDataModifier.PARAM_ELEMENT_ID, "some_element_id");
    parametersMap.put(ExtractElementDataModifier.PARAM_ELEMENT_CLASS, "some_class");

    extractElementDataModifier.setParameters(parametersMap);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_whenNoneAttributeIsDefined_throwException() throws ParametersException {
    Map<String, String> parametersMap = new HashMap<String, String>();

    extractElementDataModifier.setParameters(parametersMap);
  }

  @Test
  public void modifyData_whenDataWithElementIdAreDefined_returnDataWithInput()
      throws ProcessingException,
      ParametersException {
    configureProperlyElementId(extractElementDataModifier, "element_id");
    String expectedOutput = "<input id=\"input_id\" />";
    String inputData = "<div id='element_id'><form>" + expectedOutput + "</form></div>";

    extractElementDataModifier.modifyData(inputData);
  }

  @Test
  public void modifyData_whenDataWithOneClassAttributeIsDefined_returnDataWithThisClass()
      throws ProcessingException, ParametersException {
    configureProperlyClassAttribute(extractElementDataModifier, "class_name");
    String expectedOutput = "<form class=\"class_name\"> \n <input id=\"input_id\" /> \n</form>";
    String inputData = "<div>" + expectedOutput + "</div>";

    extractElementDataModifier.modifyData(inputData);
  }

  @Test
  public void modifyData_whenDataWithTwoClassAttributeIsDefined_returnDataForThoseTwoClasses()
      throws ProcessingException, ParametersException {
    configureProperlyClassAttribute(extractElementDataModifier, "class_name");
    String expectedOutput = "<span class=\"class_name\"> Test 1 </span>\n<span class=\"class_name\"> Test 2 </span>";
    String inputData = "<div>" + expectedOutput + "</div>";

    String actualData = extractElementDataModifier.modifyData(inputData);

    assertEquals(actualData, expectedOutput);
  }

  @Test(expected = ProcessingException.class)
  public void modifyData_noClassAttributeFoundInDataIsDefinedWithClassAttributeProvided_throwsException()
      throws ProcessingException, ParametersException {
    configureProperlyClassAttribute(extractElementDataModifier, "class_name");
    String inputData = "<div><form class=\"different_class_name\"> \n <input id=\"input_id\" /> \n</form></div>";

    String actualData = extractElementDataModifier.modifyData(inputData);

    assertEquals(actualData, "");
  }

  @Test(expected = ProcessingException.class)
  public void modifyData_noElementIdFoundInDataIsDefinedWithElementIdProvided_throwsException()
      throws ProcessingException, ParametersException {
    configureProperlyElementId(extractElementDataModifier, "element_id");
    String inputData = "<div><form class=\"different_class_name\"> \n <input id=\"input_id\" /> \n</form></div>";

    String actualData = extractElementDataModifier.modifyData(inputData);

    assertEquals(actualData, "");
  }

  private void configureProperlyElementId(ExtractElementDataModifier extractElementDataModifier,
      String elementId) throws ParametersException {
    Map<String, String> parametersMap = new HashMap<String, String>();
    parametersMap.put(ExtractElementDataModifier.PARAM_ELEMENT_ID, elementId);
    extractElementDataModifier.setParameters(parametersMap);
  }

  private void configureProperlyClassAttribute(
      ExtractElementDataModifier extractElementDataModifier,
      String classAttribute) throws ParametersException {
    Map<String, String> parametersMap = new HashMap<String, String>();
    parametersMap.put(ExtractElementDataModifier.PARAM_ELEMENT_CLASS, classAttribute);
    extractElementDataModifier.setParameters(parametersMap);
  }
}
