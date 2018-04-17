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
package com.cognifide.aet.job.common.utils;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public final class ParamsHelper {

  private ParamsHelper() {
    // private constructor for utility class
  }

  /***
   * @param key property name that should sore textual representation of Integer
   * @param params map of parameters
   * @return integer or null if there is no key in params map
   * @throws ParametersException if value under key is provided but can not be parsed into Integer
   */
  public static Integer getParamAsInteger(String key, Map<String, String> params)
      throws ParametersException {
    Integer result = null;
    if (params.containsKey(key)) {
      try {
        result = Integer.parseInt(params.get(key));
      } catch (NumberFormatException e) {
        throw new ParametersException(
            "Provided line: " + params.get(key) + " is not a numeric value.", e);
      }
    }
    return result;
  }


  public static String getParamAsString(String key, Map<String, String> params) {
    return params.getOrDefault(key, null);
  }

  /***
   * @param values array of objects
   * @throws ParametersException if all passed objects are null
   */
  public static void atLeastOneIsProvided(Object... values) throws ParametersException {
    Object firstNonNull = ObjectUtils.firstNonNull(values);
    boolean allNull = (firstNonNull == null);
    if (allNull) {
      throw new ParametersException("At least one parameter must be provided");
    }
  }

  /***
   * @param pattern
   * @param value
   * @return true if pattern is empty or if its match with given value, false otherwise
   */
  public static boolean matches(Pattern pattern, String value) {
    return pattern == null || pattern.matcher(value).matches();
  }


  public static boolean equalOrNotSet(Object expected, Object actual) {
    return expected == null || expected.equals(actual);
  }

  /**
   * @param key property name that should store plain text
   * @param params map of parameters
   * @return returns Pattern from quoted plain text under key property or null if there is no value
   * with given key
   */
  public static Pattern getPatternFromPlainText(String key, Map<String, String> params) {
    String plainMessage = ParamsHelper.getParamAsString(key, params);
    return StringUtils.isNotBlank(plainMessage) ? Pattern.compile(Pattern.quote(plainMessage))
        : null;
  }

  /***
   * @param regexpKey property name that should store regexp pattern
   * @param plainTextKey property name that should store plain text
   * @param params map of parameters
   * @return pattern under primaryKey or if retrurns null Pattern from quoted plain text under plainTextKey
   * @throws ParametersException if Pattern under primaryKey is provided but invalid
   */
  public static Pattern getPatternFromPatternParameterOrPlainText(String regexpKey,
      String plainTextKey,
      Map<String, String> params) throws ParametersException {
    Pattern result = getParamAsPattern(regexpKey, params);
    return result != null ? result : getPatternFromPlainText(plainTextKey, params);
  }

  public static Pattern getParamAsPattern(String regexpKey, Map<String, String> params)
      throws ParametersException {
    Pattern result = null;
    if (params.containsKey(regexpKey)) {
      try {
        result = Pattern.compile(params.get(regexpKey));
      } catch (PatternSyntaxException e) {
        throw new ParametersException("errorPattern value is invalid regular-expression pattern.",
            e);
      }
    }
    return result;
  }
}
