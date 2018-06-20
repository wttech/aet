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
package com.cognifide.aet.runner.processing.data;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.Map;

public class SuiteIndexWrapper {

  private final Map<String, Test> tests;

  private final Suite suite;

  public SuiteIndexWrapper(Suite suite) {
    this.suite = suite;
    this.tests = FluentIterable.from(suite.getTests()).uniqueIndex(new NamedToMapFunction<Test>());
  }

  public Test getTest(String testName) {
    return tests.get(testName);
  }

  public Optional<Url> getTestUrl(String testName, final String urlName) {
    Test test = tests.get(testName);

    return FluentIterable.from(test.getUrls()).firstMatch(new Predicate<Url>() {
      @Override
      public boolean apply(Url url) {
        return url.getName().equals(urlName);
      }
    });
  }

  public Suite get() {
    return suite;
  }
}
