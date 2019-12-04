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
package com.cognifide.aet.models;

import com.cognifide.aet.job.common.comparators.cookie.CookieComparatorResult;
import com.cognifide.aet.job.common.comparators.cookie.CookieCompareComparatorResult;
import com.cognifide.aet.job.common.comparators.cookie.CookieTestComparatorResult;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class CookieErrorWrapper {

  public static final String ERROR_TYPE = "cookie";
  public static final String ACTION_PARAM = "action";
  public static final String ACTION_COMPARE = "compare";
  public static final String ACTION_TEST = "test";

  public static final Type ARTIFACT_COOKIE_COMPARE_TYPE = new TypeToken<CookieCompareComparatorResult>() {
  }.getType();
  public static final Type ARTIFACT_COOKIE_TEST_TYPE = new TypeToken<CookieTestComparatorResult>() {
  }.getType();

  private final CookieComparatorResult result;
  private final String urlName;

  public CookieErrorWrapper(CookieComparatorResult result, String urlName) {
    this.result = result;
    this.urlName = urlName;
  }

  public CookieComparatorResult getResult() {
    return result;
  }

  public String getUrlName() {
    return urlName;
  }
}
