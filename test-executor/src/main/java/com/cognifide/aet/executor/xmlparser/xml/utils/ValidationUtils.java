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

import com.cognifide.aet.executor.model.CollectorStep;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ValidationUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

  private static final String PARAM_DURATION = "duration";

  private static final int MAX_TOTAL_SLEEP_DURATION = 120000;

  private static final int MAX_SLEEP_DURATION = 30000;

  private static final String SLEEP_MODIFIER_NAME = "sleep";

  private static final String LOWERCASE_NAME_ATTRIBUTE_PATTERN = "[a-z\\-_0-9]+";

  private static final String CASE_INSENSITIVE_NAME_ATTRIBUTE_PATTERN = "[a-zA-Z\\-_0-9]+";

  private ValidationUtils() {
    // empty utils constructor
  }

  public static String validateNotHavingUnderscore(String value, String warnMessage)
      throws ParseException {
    String fixedValue = StringUtils.remove(value, "_");

    if (!StringUtils.equals(value, fixedValue)) {
      LOGGER.warn(warnMessage);
    }

    return fixedValue;
  }


  public static void validateSleep(List<CollectorStep> collectorSteps) {
    validateSleepCheckConsecutiveSleeps(collectorSteps);
    int sleepTotal = validateSleepCheckMaxDuration(collectorSteps);

    // check total sleep duration
    if (sleepTotal > MAX_TOTAL_SLEEP_DURATION) {
      throw new IllegalArgumentException(String.format(
          "Total sleep duration cannot be longer than %d seconds per test!",
          MAX_TOTAL_SLEEP_DURATION / 1000));
    }
  }

  private static int validateSleepCheckMaxDuration(List<CollectorStep> collectorSteps) {
    int sleepTotal = 0;
    // check max sleep duration for one sleep
    for (CollectorStep collectorStep : collectorSteps) {
      if (collectorStep.getName().equals(SLEEP_MODIFIER_NAME)) {
        int sleepDuration = Integer.valueOf(collectorStep.getParameters().get(PARAM_DURATION));
        if (sleepDuration > MAX_SLEEP_DURATION) {
          throw new IllegalArgumentException(String.format(
              "Sleep duration cannot be longer than %d seconds!", MAX_SLEEP_DURATION / 1000));
        }
        sleepTotal += sleepDuration;
      }
    }
    return sleepTotal;
  }

  private static void validateSleepCheckConsecutiveSleeps(List<CollectorStep> collectorSteps) {
    // check consecutive sleeps
    for (int i = 0; i < collectorSteps.size() - 1; i++) {
      if (collectorSteps.get(i).getName().equals(SLEEP_MODIFIER_NAME)
          && collectorSteps.get(i + 1).getName().equals(SLEEP_MODIFIER_NAME)) {
        throw new IllegalArgumentException("You cannot use more than one sleep modifier in a row!");
      }
    }
  }

  public static String validateLowerCase(String string, String warnMessage) {
    String fixedString = string.toLowerCase();
    if (!StringUtils.equals(string, fixedString)) {
      LOGGER.warn(warnMessage);
    }
    return fixedString;
  }

  public static String validateWhitespace(String string, String warnMessage) throws ParseException {
    String fixedString = StringUtils.deleteWhitespace(string);
    if (StringUtils.isEmpty(fixedString)) {
      throw new ParseException("Provided string is empty", null);
    } else if (!StringUtils.equals(string, fixedString)) {
      LOGGER.warn(warnMessage);
    }
    return fixedString;
  }

  public static String validateCaseSensitiveNameAttribute(String parentElement, String name,
      String value)
      throws ParseException {
    String result = validateLowerCase(
        value,
        String.format(
            "Attribute: %s in %s definition shouldn't have uppercase letters (current: '%s'). Please fix it.",
            name, parentElement, value));
    return validateNameAttribute(parentElement, name, result, LOWERCASE_NAME_ATTRIBUTE_PATTERN);
  }

  public static String validateCaseInsensitiveNameAttribute(String parentElement, String name,
      String value)
      throws ParseException {
    return validateNameAttribute(parentElement, name, value,
        CASE_INSENSITIVE_NAME_ATTRIBUTE_PATTERN);
  }

  private static String validateNameAttribute(String parentElement, String name, String value,
      String pattern) throws ParseException {
    String result;
    try {
      result = validateWhitespace(
          value,
          String.format(
              "Attribute: %s in %s definition shouldn't have whitespace (current: '%s'). Please fix it.",
              name, parentElement, value));
    } catch (ParseException e) {
      throw new ParseException(
          String.format("Problem with attribute %s: '%s' in %s definition.", name,
              value, parentElement), e);
    }

    if (!Pattern.matches(pattern, result)) {
      throw new ParseException(String.format(
          "Value '%s' of attribute %s in %s does not match pattern `%s`!", value, name,
          parentElement, pattern));
    }
    return result;
  }
}
