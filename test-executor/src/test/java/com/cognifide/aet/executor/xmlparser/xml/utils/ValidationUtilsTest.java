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
package com.cognifide.aet.executor.xmlparser.xml.utils;

import static org.junit.Assert.assertEquals;

import com.cognifide.aet.executor.model.CollectorStep;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ValidationUtilsTest {

  private static final String SUITE = "suite";

  private static final String NAME = "name";

  @Test
  public void testValidateOneSleep() {
    List<CollectorStep> collectorSteps = new ArrayList<>();
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    ValidationUtils.validateSleep(collectorSteps);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateSleepTooLong() {
    List<CollectorStep> collectorSteps = new ArrayList<>();
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30001")));
    ValidationUtils.validateSleep(collectorSteps);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateTotalSleepTooLong() {
    List<CollectorStep> collectorSteps = new ArrayList<>();
    collectorSteps.add(new CollectorStep("", "open", new HashMap<String, String>()));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "screen", ImmutableMap.of("name", "desktop")));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "screen", ImmutableMap.of("name", "iphone")));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "screen", ImmutableMap.of("name", "tablet")));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "source", new HashMap<String, String>()));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "1")));
    collectorSteps.add(new CollectorStep("", "status-codes", new HashMap<String, String>()));
    ValidationUtils.validateSleep(collectorSteps);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateConsecutiveSleeps() {
    List<CollectorStep> collectorSteps = new ArrayList<>();
    collectorSteps.add(new CollectorStep("", "open", new HashMap<String, String>()));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "screen", ImmutableMap.of("name", "iphone")));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "screen", ImmutableMap.of("name", "tablet")));
    collectorSteps.add(new CollectorStep("", "sleep", ImmutableMap.of("duration", "30000")));
    collectorSteps.add(new CollectorStep("", "status-codes", new HashMap<String, String>()));
    ValidationUtils.validateSleep(collectorSteps);
  }

  @Test
  public void testValidateLowerCase() throws ParseException {
    assertEquals("cognifide", ValidationUtils.validateLowerCase("cognifide", null));
    assertEquals("cognifide", ValidationUtils.validateLowerCase("Cognifide", null));
    assertEquals("cognifide", ValidationUtils.validateLowerCase("COGNIFIDE", null));
    assertEquals("  cognifide   ", ValidationUtils.validateLowerCase("  COGNIFIDE   ", null));
    assertEquals("cog    nifide", ValidationUtils.validateLowerCase("Cog    nifide", null));
  }

  @Test
  public void testValidateWhitespace() throws ParseException {
    assertEquals("cognifide", ValidationUtils.validateWhitespace("cognifide", null));
    assertEquals("Cognifide", ValidationUtils.validateWhitespace("Cognifide", null));
    assertEquals("COGNIFIDE", ValidationUtils.validateWhitespace("COGNIFIDE", null));
    assertEquals("COGNIFIDE", ValidationUtils.validateWhitespace("  COGNIFIDE   ", null));
    assertEquals("Cognifide", ValidationUtils.validateWhitespace("Cog    nifide", null));
  }

  @Test(expected = ParseException.class)
  public void testValidateWhitespaceEmpty() throws ParseException {
    ValidationUtils.validateWhitespace("", null);
  }

  @Test(expected = ParseException.class)
  public void testValidateWhitespaceOnly() throws ParseException {
    ValidationUtils.validateWhitespace("\t \n", null);
  }

  @Test(expected = ParseException.class)
  public void testValidateNameAttributeWithEmptyValueThenExpectException() throws ParseException {
    ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "");
  }

  @Test(expected = ParseException.class)
  public void testValidateNameAttributeWithOnlyWhitespacesThenExpectException()
      throws ParseException {
    ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "\t 	\n ");
  }

  @Test(expected = ParseException.class)
  public void testValidateNameAttributeWithInvalidValueThenExpectException() throws ParseException {
    ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "CO[];';lGNIFIDE");
  }

  @Test(expected = ParseException.class)
  public void testValidateNameAttributeWithSlashesThenExpectException() throws ParseException {
    ValidationUtils.validateCaseInsensitiveNameAttribute(SUITE, NAME, "//////");
  }

  @Test
  public void testValidateCaseSensitiveNameAttribute() throws ParseException {
    assertEquals("cognifide",
        ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "cognifide"));
    assertEquals("cognifide",
        ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "Cognifide"));
    assertEquals("cognifide",
        ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "COGNIFIDE"));
    assertEquals("cognifide",
        ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "  COGNIFIDE   "));
    assertEquals("cognifide",
        ValidationUtils.validateCaseSensitiveNameAttribute(SUITE, NAME, "Cog    nifide"));
  }

  @Test
  public void testValidateCaseInsensitiveNameAttribute() throws ParseException {
    assertEquals("cognifide",
        ValidationUtils.validateCaseInsensitiveNameAttribute(SUITE, NAME, "cognifide"));
    assertEquals("Cognifide",
        ValidationUtils.validateCaseInsensitiveNameAttribute(SUITE, NAME, "Cognifide"));
    assertEquals("COGNIFIDE",
        ValidationUtils.validateCaseInsensitiveNameAttribute(SUITE, NAME, "COGNIFIDE"));
    assertEquals("COGNIFIDE",
        ValidationUtils.validateCaseInsensitiveNameAttribute(SUITE, NAME, "  COGNIFIDE   "));
    assertEquals("Cognifide",
        ValidationUtils.validateCaseInsensitiveNameAttribute(SUITE, NAME, "Cog    nifide"));
  }

  @Test
  public void testValidateNotHavingUnderscore() throws ParseException {
    assertEquals("cognifide",
        ValidationUtils.validateNotHavingUnderscore("cogni_fide", StringUtils.EMPTY));
    assertEquals("cognifide",
        ValidationUtils.validateNotHavingUnderscore("c_ogni_fide", StringUtils.EMPTY));
    assertEquals("strange-company-name",
        ValidationUtils.validateNotHavingUnderscore("strange-company-name", StringUtils.EMPTY));
  }

}
