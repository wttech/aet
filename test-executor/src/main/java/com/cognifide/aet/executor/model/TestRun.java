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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents single test in test suite. Consists of sequence of steps: 1) Collection 2) Comparison
 * And set of urls on which tests will be performed.
 */
public class TestRun implements Serializable {

  private static final long serialVersionUID = 1453942700093647211L;

  private final List<CollectorStep> collectorSteps;

  private final Map<String, List<ComparatorStep>> comparatorSteps;

  private final List<ExtendedUrl> urls;

  private final String name;

  private final String useProxy;

  private final String browser;

  /**
   * @param collectorSteps - list of collector steps.
   * @param comparatorSteps - set of comparison steps.
   * @param urls - list of urls.
   * @param name - unique name of test.
   * @param useProxy - says what kind of proxy should be used, backward compatibility: set 'true' to
   * use 'rest' proxy, set 'false' to use none.
   * @param browser - id of preferred browser or null if the default one should be used
   */
  public TestRun(List<CollectorStep> collectorSteps, Set<ComparatorStep> comparatorSteps,
      List<ExtendedUrl> urls, String name, String useProxy, String browser) {
    this.collectorSteps = collectorSteps;
    this.comparatorSteps = getMap(comparatorSteps);
    this.urls = urls;
    this.name = name;
    this.useProxy = useProxy;
    this.browser = browser;
  }

  private Map<String, List<ComparatorStep>> getMap(Set<ComparatorStep> comparatorSteps) {
    Map<String, List<ComparatorStep>> result = new HashMap<>();
    for (ComparatorStep comparatorStep : comparatorSteps) {
      List<ComparatorStep> list = result.get(comparatorStep.getType());
      if (list == null) {
        list = new ArrayList<>();
        result.put(comparatorStep.getType(), list);
      }
      list.add(comparatorStep);
    }
    return result;
  }

  /**
   * @return list of collector steps in this test.
   */
  public List<CollectorStep> getCollectorSteps() {
    return collectorSteps;
  }

  /**
   * @return list of comparison steps in this test.
   */
  public Map<String, List<ComparatorStep>> getComparatorSteps() {
    return comparatorSteps;
  }

  /**
   * @return list of urls.
   */
  public List<ExtendedUrl> getUrls() {
    return urls;
  }

  /**
   * @return unique name of test.
   */
  public String getName() {
    return name;
  }

  /**
   * @return name of kind of proxy that will be used.
   */
  public String getUseProxy() {
    return useProxy;
  }

  /**
   * @return id of preferred browser.
   */
  public String getBrowser() {
    return browser;
  }
}
