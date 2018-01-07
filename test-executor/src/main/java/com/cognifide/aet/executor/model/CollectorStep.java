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
 * Represents single step for collector. Contains basic information about collection step.
 */
public class CollectorStep implements ParametrizedStep {

  private static final long serialVersionUID = 1862328343196919369L;

  private final String module;

  private final String name;

  private final Map<String, String> parameters;

  /**
   * @param module - name (identifier) of module, with this parameter system decides which collector
   * will handle this step.
   * @param name - unique name (within collectors with the same module) for collection step.
   * Comparator can distinguish collection results made by the same collector but with different
   * names to treat them as separate results.
   * @param parameters - all additional parameters passed to Collector, e.g. width and height of
   * screenshot, duration of sleep. Each parameter is stored as separate map entry (key-&gt;value).
   */
  public CollectorStep(String module, String name, Map<String, String> parameters) {
    this.module = module;
    this.name = name;
    this.parameters = parameters;
  }

  /**
   * @return identifier of collector module which will handle this collection step.
   */
  public String getModule() {
    return module;
  }

  /**
   * @return unique name of collection step (within the same collectors module group)
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @return map of additional parameters for collection step
   */
  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }
}
