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

import java.util.List;
import java.util.Map;

/**
 * Represents single step for comparator. Contains basic information about comparison step.
 */
public class ComparatorStep implements ParametrizedStep {

  private static final long serialVersionUID = 554429243982531816L;

  private final String module;

  private final String type;

  private final String name;

  private final String collectorName;

  private final Map<String, String> parameters;

  private final List<DataModifierStep> dataModifierSteps;

  /**
   * @param module - name (identifier) of module, with this parameter system decides which
   * comparator will handle this step.
   * @param type - identifies type of resource which should be compared (e.g. source, screen).
   * @param name - unique (within comparators with the same module) name for comparator.
   * @param collectorName - name of collector which results will be handled by this comparator.
   * @param parameters - all additional parameters passed to Comparator, e.g. elementId of source.
   * Each parameter is stored as separate map entry (key-&gt;value).
   * @param dataModifierSteps - list of modifiers which will have impact on comparison results.
   */
  public ComparatorStep(String module, String type, String name, String collectorName,
      Map<String, String> parameters, List<DataModifierStep> dataModifierSteps) {
    this.module = module;
    this.type = type;
    this.name = name;
    this.collectorName = collectorName;
    this.parameters = parameters;
    this.dataModifierSteps = dataModifierSteps;
  }

  /**
   * @return identifier of comparator module which will handle this comparison step.
   */
  public String getModule() {
    return module;
  }

  /**
   * @return name of the resource type that will be handled by this comparator.
   */
  public String getType() {
    return type;
  }

  /**
   * @return unique (within comparators with the same module) name for comparator.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @return name of collector which results will be handled by this comparator.
   */
  public String getCollectorName() {
    return collectorName;
  }

  /**
   * @return map of additional comparator parameters.
   */
  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }

  /**
   * @return <b>true</b> when comparator should use default module, <b>false</b> otherwise.
   */
  public boolean isUseDefault() {
    return module == null;
  }

  /**
   * @return list of modifiers which will have impact on comparison results.
   */
  public List<DataModifierStep> getDataModifierSteps() {
    return dataModifierSteps;
  }

}
