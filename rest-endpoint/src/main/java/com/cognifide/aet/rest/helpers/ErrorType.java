/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.rest.helpers;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.common.comparators.cookie.CookieCompareComparatorResult;
import com.cognifide.aet.job.common.comparators.statuscodes.StatusCodesComparatorResult;
import com.cognifide.aet.models.CookieResult;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ErrorType {
  JS_ERRORS("js-errors", new TypeToken<Set<JsErrorLog>>() {
  }.getType()),
  STATUS_CODES("status-codes", new TypeToken<StatusCodesComparatorResult>() {
  }.getType()),
  COOKIE("cookie", new TypeToken<CookieCompareComparatorResult>() {
  }.getType()),
  LAYOUT("layout", null),
  SOURCE("source", null);

  private final String errorName;
  private final Type type;

  ErrorType(String errorName, Type type) {
    this.errorName = errorName;
    this.type = type;
  }

  private static final Map<String, Type> map;

  static {
    map = new HashMap<>();
    for (ErrorType type : ErrorType.values()) {
      map.put(type.errorName, type.type);
    }
  }

  public static Type getTypeByName(String name) {
    return map.get(name);
  }

  public String getErrorName() {
    return errorName;
  }
}
