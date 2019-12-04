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

import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class SourceErrorWrapper {

  public static final String ERROR_TYPE = "source";
  public static final String COMPARATOR_PARAM = "comparator";
  public static final String COMPARATOR_TYPE = "source";
  public static final Type ARTIFACT_TYPE = new TypeToken<Map<String, List<ResultDelta>>>() {
  }.getType();

  private final Map<String, List<ResultDelta>> result;
  private final String urlName;
  private final Map<String, String> data;

  public SourceErrorWrapper(Map<String, List<ResultDelta>> result, String urlName,
      Map<String, String> data) {
    this.result = result;
    this.urlName = urlName;
    this.data = data;
  }

  public Map<String, List<ResultDelta>> getResult() {
    return result;
  }

  public String getUrlName() {
    return urlName;
  }

  public Map<String, String> getData() {
    return data;
  }
}
