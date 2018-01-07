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
package com.cognifide.aet.communication.api.job;

import com.cognifide.aet.communication.api.metadata.Step;

/**
 * Model which stores comparison job description (configuration).
 */
public class ComparatorJobData extends JobData {

  private static final long serialVersionUID = 6348652101635569660L;

  private final String urlName;

  private final Step step;

  /**
   * @param company - company name.
   * @param project - project name.
   * @param suiteName - suite name.
   * @param testName - test name.
   * @param urlName - name of url.
   * @param step - step with comparisons.
   */
  public ComparatorJobData(String company, String project, String suiteName, String testName,
      String urlName, Step step) {
    super(company, project, suiteName, testName);
    this.urlName = urlName;
    this.step = step;
  }

  public String getUrlName() {
    return urlName;
  }

  public Step getStep() {
    return step;
  }
}
