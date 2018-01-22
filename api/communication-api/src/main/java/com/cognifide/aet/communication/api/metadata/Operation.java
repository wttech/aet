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
package com.cognifide.aet.communication.api.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents operation
 */
public class Operation implements Serializable {

  private static final long serialVersionUID = -6037522620321892492L;

  protected final String type;

  protected final Map<String, String> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  public Operation(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String addParameter(String key, String value) {
    return parameters.put(key, value);
  }

  public void addParameters(Map<String, String> parameters) {
    this.parameters.putAll(parameters);
  }

  public Map<String, String> getParameters() {
    return parameters != null ? ImmutableMap.copyOf(parameters)
        : Collections.<String, String>emptyMap();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("type", type)
        .add("parameters", parameters)
        .toString();
  }
}
