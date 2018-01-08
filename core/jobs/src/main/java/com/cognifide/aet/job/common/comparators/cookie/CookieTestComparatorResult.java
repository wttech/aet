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
package com.cognifide.aet.job.common.comparators.cookie;

import java.util.Set;
import org.openqa.selenium.Cookie;

public class CookieTestComparatorResult extends CookieComparatorResult {

  private static final long serialVersionUID = -8076313812156184448L;

  private final String cookieName;

  private final String cookieValue;

  public CookieTestComparatorResult(CompareAction compareAction, Set<Cookie> cookies,
      String cookieName, String cookieValue) {
    super(compareAction, cookies);
    this.cookieName = cookieName;
    this.cookieValue = cookieValue;
  }

  public String getCookieName() {
    return cookieName;
  }

  public String getCookieValue() {
    return cookieValue;
  }

}
