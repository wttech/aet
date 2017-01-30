/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.executor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents single test in test suite. Consists of sequence of steps: 1) Collection 2) Comparison And set of
 * urls on which tests will be performed.
 */
public class TestRun implements Serializable {

  private static final long serialVersionUID = 7276830417015175985L;

  private final List<CollectorStep> collectorSteps;

  private final Map<String, List<ComparatorStep>> comparatorSteps;

  private final List<ExtendedUrl> urls;

  private final String name;

  private final String useProxy;

  private final int zIndex;

  /**
   * @param collectorSteps - list of collector steps.
   * @param comparatorSteps - set of comparison steps.
   * @param urls - list of urls.
   * @param name - unique name of test.
   * @param useProxy - says what kind of proxy should be used, backward compatibility: set 'true' to use
   * embedded, set 'false' to use none.
   * @param zIndex - specifies order of tests. A test with greater value is always before test with lower zIndex.
   */
  public TestRun(List<CollectorStep> collectorSteps, Set<ComparatorStep> comparatorSteps,
                 List<ExtendedUrl> urls, String name, String useProxy, int zIndex) {
    this.collectorSteps = collectorSteps;
    this.comparatorSteps = getMap(comparatorSteps);
    this.urls = urls;
    this.name = name;
    this.useProxy = useProxy;
    this.zIndex = zIndex;
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
   * @return zIndex of test.
   */
  public int getzIndex() {
    return zIndex;
  }
}
