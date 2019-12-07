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

import java.util.Map;

public class ScreenErrorWrapper {

  public static final String ERROR_TYPE = "screen";

  private final String name;
  private final Map<String, String> data;
  private final String urlName;

  public ScreenErrorWrapper(String name, Map<String, String> data, String urlName) {
    this.name = name;
    this.data = data;
    this.urlName = urlName;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getData() {
    return data;
  }

  public String getUrlName() {
    return urlName;
  }
}
