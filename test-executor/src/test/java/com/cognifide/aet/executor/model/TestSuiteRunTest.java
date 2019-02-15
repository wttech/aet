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

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestSuiteRunTest {

  @Test
  public void constructor_whenOverridingNameAndDomain_expectUpdatedValues() {
    String company = "company";
    String project = "project";
    String name = "abcd";
    String domain = "ijkl";
    TestSuiteRun baseSuiteRun = new TestSuiteRun(name, company, project, domain, Collections.emptyList());

    String newName = "efgh";
    String newDomain = "mnop";
    TestSuiteRun updatedSuiteRun = new TestSuiteRun(baseSuiteRun, newName, newDomain, Collections.emptyList());
    String correlationRegex = String.format("^%s-%s-%s-[0-9]*$", company, project, newName);

    assertThat(updatedSuiteRun.getName(), equalTo(newName));
    assertThat(updatedSuiteRun.getDomain(), equalTo(newDomain));
    assertThat(updatedSuiteRun.getCorrelationId(), updatedSuiteRun.getCorrelationId().matches(correlationRegex));
  }

}