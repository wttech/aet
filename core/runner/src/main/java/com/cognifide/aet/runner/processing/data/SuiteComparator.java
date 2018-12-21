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
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class SuiteComparator {

  static List<String> compare(Set<Suite> suites) {
    if (suites.size() <= 1) {
      return Collections.emptyList();
    }

    List<List<Suite>> suitesUniquePairs = findUniquePairs(new ArrayList<>(suites));

    List<String> comparisonMessages = new ArrayList<>();
    suitesUniquePairs.forEach(pair -> compareSuitesPair(pair, comparisonMessages));

    return comparisonMessages;
  }

  private static void compareSuitesPair(List<Suite> pair, List<String> comparisonMessages) {
    Suite first = pair.get(0);
    Suite second = pair.get(1);

    if (suitesHaveDifferentTests(first, second)) {
      comparisonMessages.add(
          String.format("Suite: %s, with id: %s and suite: %s, with id: %s have different tests.",
              first.getName(), first.getCorrelationId(), second.getName(),
              second.getCorrelationId()));
    }
  }

  private static boolean suitesHaveDifferentTests(Suite first, Suite second) {
    if (isNullOrEmpty(first.getTests(), second.getTests())) {
      return false;
    }

    if (isNullOrEmpty(first.getTests()) ^ isNullOrEmpty(second.getTests())) {
      return true;
    }

    List<String> firstTestsNames = first.getTests().stream()
        .map(Test::getName)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    List<String> secondTestsNames = second.getTests().stream()
        .map(Test::getName)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    boolean suitesHaveDifferentNumberOfTests = firstTestsNames.size() != secondTestsNames.size();

    boolean suitesHaveDifferentTests =
        Sets.difference(new HashSet<>(firstTestsNames), new HashSet<>(secondTestsNames)).size() != 0
            ||
            Sets.difference(new HashSet<>(secondTestsNames), new HashSet<>(firstTestsNames)).size()
                != 0;

    return suitesHaveDifferentNumberOfTests || suitesHaveDifferentTests;
  }

  private static List<List<Suite>> findUniquePairs(List<Suite> suites) {
    List<List<Suite>> pairs = new ArrayList<>();

    for (int i = 0; i < suites.size(); i++) {
      for (int j = i + 1; j < suites.size(); j++) {
        Suite first = suites.get(i);
        Suite second = suites.get(j);

        pairs.add(Arrays.asList(first, second));
      }
    }
    return pairs;
  }

  private static boolean isNullOrEmpty(Collection... tests) {
    return Arrays.stream(tests).allMatch(test -> test == null || test.isEmpty());
  }
}
