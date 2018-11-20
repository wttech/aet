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
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

final class SuiteMergeStrategy {

  private static final NamedToMapFunction<Test> TEST_TO_MAP = new NamedToMapFunction<>();

  private static final NamedToMapFunction<Url> URL_TO_MAP = new NamedToMapFunction<>();

  private static final NamedToMapFunction<Step> STEP_TO_MAP = new NamedToMapFunction<>();

  private static final NamedToMapFunction<Comparator> COMPARATOR_TO_MAP = new NamedToMapFunction<>();

  private static final BiConsumer<Step, Step> ERASE_PATTERNS_IN_CURRENT = (current, pattern) -> current
      .erasePatterns();

  private static final BiConsumer<Step, Step> COPY_PATTERNS_TO_CURRENT = (current, pattern) -> current
      .addPatterns(pattern.getPatterns());

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
   * @return current suite after merge.
   */
  public static Suite merge(Suite current, Suite lastVersion) {
    merge(current, lastVersion, Collections.singletonList(lastVersion));
    return current;
  }

  /**
   * Merges current and patterns suites. All comments, version and patterns in current suite are
   * overwritten from patterns suites. If multiple pattern suites have the same test, all patterns
   * from these tests will be placed in current run suite test. Last version suite is used only to
   * update current version number.
   *
   * @param current - current run suite.
   * @param lastVersion - latest suite run version.
   * @param patterns - patterns suites.
   * @return current suite after merge.
   */
  public static Suite merge(Suite current, Suite lastVersion, List<Suite> patterns) {
    erasePatterns(current, patterns);
    setVersion(current, lastVersion);
    setPatterns(current, patterns);
    return current;
  }

  private static void erasePatterns(Suite current, List<Suite> patterns) {
    patterns.forEach(pattern -> modifyStep(current, pattern, ERASE_PATTERNS_IN_CURRENT));
  }

  private static void setPatterns(Suite current, List<Suite> patterns) {
    patterns.forEach(pattern -> modifyStep(current, pattern, COPY_PATTERNS_TO_CURRENT));

  }

  private static void setVersion(Suite current, Suite lastVersion) {
    if (isFirstRun(lastVersion)) {
      current.setVersion(1L);
    } else if (isNotTestOrUrlRerun(current)) {
      current.setVersion(lastVersion.getVersion() + 1);
    }
  }

  private static boolean isNotTestOrUrlRerun(Suite current) {
    return current.getVersion() == null;
  }

  private static boolean isFirstRun(Suite lastVersion) {
    return lastVersion == null;
  }


  private static void modifyStep(Suite current, Suite pattern,
      BiConsumer<Step, Step> stepModifier) {
    if (pattern != null) {
      final ImmutableMap<String, Test> tests = FluentIterable.from(current.getTests())
          .uniqueIndex(TEST_TO_MAP);
      updateComment(current, pattern);
      for (Test patternTest : pattern.getTests()) {
        if (tests.containsKey(patternTest.getName())) {
          mergeTest(tests.get(patternTest.getName()), patternTest, stepModifier);
        }
      }
    }
  }

  private static void mergeTest(Test currentTest, Test patternTest,
      BiConsumer<Step, Step> stepModifier) {
    updateComment(currentTest, patternTest);
    final ImmutableMap<String, Url> urlsMap = FluentIterable.from(currentTest.getUrls())
        .uniqueIndex(URL_TO_MAP);

    for (Url patternUrl : patternTest.getUrls()) {
      if (urlsMap.containsKey(patternUrl.getName())) {
        mergeUrl(urlsMap.get(patternUrl.getName()), patternUrl, stepModifier);
      }
    }
  }

  private static void mergeUrl(Url currentUrl, Url patternUrl,
      BiConsumer<Step, Step> stepModifier) {
    updateComment(currentUrl, patternUrl);
    final ImmutableMap<String, Step> collectors =
        FluentIterable.from(currentUrl.getSteps()).filter(COMPARATORS_FILTER)
            .uniqueIndex(STEP_TO_MAP);

    for (Step patternStep : patternUrl.getSteps()) {
      if (isCollector(patternStep) && collectors.containsKey(patternStep.getName())) {
        mergeStep(collectors.get(patternStep.getName()), patternStep, stepModifier);
      }
    }
  }

  private static void mergeStep(Step currentStep, Step patternStep,
      BiConsumer<Step, Step> stepModifier) {
    updateComment(currentStep, patternStep);

    stepModifier.accept(currentStep, patternStep);

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
