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
package com.cognifide.aet.executor.model;

import java.util.Map;

/**
 * Represents single comparison result modification step. Contains basic information about
 * modification.
 */
public class DataModifierStep implements ParametrizedStep {

  private static final long serialVersionUID = 5763173533132946807L;

  private final String name;

  private final Map<String, String> parameters;

  /**
   * @param name - unique name of modifier which will be used to modify comparison result.
   * @param parameters - all parameters needed by modifier, e.g. 'error' for js errors filter to
   * filter out specific js errors before js console comparator.
   */
  public DataModifierStep(String name, Map<String, String> parameters) {
    this.name = name;
    this.parameters = parameters;
  }

  /**
   * @return name of modifier which will be used to modify comparison result.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @return map of parameters used by modifier.
   */
  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }

}
