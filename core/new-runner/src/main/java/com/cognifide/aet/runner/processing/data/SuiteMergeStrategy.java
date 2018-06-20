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


import com.cognifide.aet.communication.api.metadata.Commentable;
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

final class SuiteMergeStrategy {

  private static final NamedToMapFunction<Test> TEST_TO_MAP = new NamedToMapFunction<>();

  private static final NamedToMapFunction<Url> URL_TO_MAP = new NamedToMapFunction<>();

  private static final NamedToMapFunction<Step> STEP_TO_MAP = new NamedToMapFunction<>();

  private static final NamedToMapFunction<Comparator> COMPARATOR_TO_MAP = new NamedToMapFunction<>();

  private static final Predicate<Step> COMPARATORS_FILTER = new Predicate<Step>() {
    @Override
    public boolean apply(Step input) {
      return input != null && isCollector(input);
    }
  };

  private SuiteMergeStrategy() {
    //util private constructor
  }

  /**
   * Merges current and pattern suite. All comments, version and patterns in current suite are
   * overwritten from pattern suite.
   *
   * @param current - current run suite.
   * @param lastVersion - latest version of this suite execution, treated also as a pattern suite.
   * @return merged suite.
   */
  public static Suite merge(Suite current, Suite lastVersion) {
    return merge(current, lastVersion, lastVersion);
  }

  /**
   * Merges current and pattern suite. All comments, version and patterns in current suite are
   * overwritten from pattern suite. Last version suite is used only to update current version
   * number.
   *
   * @param current - current run suite.
   * @param lastVersion - latest suite run version.
   * @param pattern - pattern suite.
   * @return merged suite.
   */
  public static Suite merge(Suite current, Suite lastVersion, Suite pattern) {
    setVersion(current, lastVersion);
    setPatterns(current, pattern);
    return current;
  }

  private static void setVersion(Suite current, Suite lastVersion) {
    if (lastVersion != null) {
      current.setVersion(lastVersion.getVersion() + 1);
    } else {
      current.setVersion(1L);
    }
  }

  private static void setPatterns(Suite current, Suite pattern) {
    if (pattern != null) {
      final ImmutableMap<String, Test> tests = FluentIterable.from(current.getTests())
          .uniqueIndex(TEST_TO_MAP);
      updateComment(current, pattern);
      for (Test patternTest : pattern.getTests()) {
        if (tests.containsKey(patternTest.getName())) {
          mergeTest(tests.get(patternTest.getName()), patternTest);
        }
      }
    }
  }

  private static void mergeTest(Test currentTest, Test patternTest) {
    updateComment(currentTest, patternTest);
    currentTest.setComment(patternTest.getComment());
    final ImmutableMap<String, Url> urlsMap = FluentIterable.from(currentTest.getUrls())
        .uniqueIndex(URL_TO_MAP);

    for (Url patternUrl : patternTest.getUrls()) {
      if (urlsMap.containsKey(patternUrl.getName())) {
        mergeUrl(urlsMap.get(patternUrl.getName()), patternUrl);
      }
    }
  }

  private static void mergeUrl(Url currentUrl, Url patternUrl) {
    updateComment(currentUrl, patternUrl);
    final ImmutableMap<String, Step> collectors =
        FluentIterable.from(currentUrl.getSteps()).filter(COMPARATORS_FILTER)
            .uniqueIndex(STEP_TO_MAP);

    for (Step patternStep : patternUrl.getSteps()) {
      if (isCollector(patternStep) && collectors.containsKey(patternStep.getName())) {
        mergeStep(collectors.get(patternStep.getName()), patternStep);
      }
    }
  }

  private static void mergeStep(Step currentStep, Step patternStep) {
    updateComment(currentStep, patternStep);
    currentStep.updatePattern(patternStep.getPattern());

    final ImmutableMap<String, Comparator> comparators = FluentIterable
        .from(currentStep.getComparators())
        .uniqueIndex(COMPARATOR_TO_MAP);

    for (Comparator patternComparator : patternStep.getComparators()) {
      if (comparators.containsKey(patternComparator.getName())) {
        updateComment(comparators.get(patternComparator.getName()), patternComparator);
      }
    }
  }

  private static void updateComment(Commentable current, Commentable pattern) {
    current.setComment(pattern.getComment());
  }

  private static boolean isCollector(Step step) {
    return step.getComparators() != null && !step.getComparators().isEmpty();
  }
}
