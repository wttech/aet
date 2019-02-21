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

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.Operation;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SuiteComparator {

  /**
   * Takes collection of suites and generates info messages if provided suites differ from each
   * other. Compares each suite with all other suites. Compares tests (by count and names), and
   * urls/steps (by count, types, names and parameters).
   *
   * @returns List of messages outlining which suites/test differ from each other.
   */
  static List<String> compare(List<Suite> suites) {
    if (suites.size() <= 1) {
      return Collections.emptyList();
    }

    List<List<Suite>> suitesUniquePairs = findUniquePairs(suites);
    List<String> comparisonMessages = new ArrayList<>();

    suitesUniquePairs.forEach(pair -> compareSuitesPair(pair, comparisonMessages));

    return comparisonMessages;
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

  private static void compareSuitesPair(List<Suite> pair, List<String> comparisonMessages) {
    Suite first = pair.get(0);
    Suite second = pair.get(1);

    compareTests(first, second, comparisonMessages);
    compareSteps(first, second, comparisonMessages);
  }

  private static void compareTests(Suite first, Suite second, List<String> comparisonMessages) {
    if (suitesHaveDifferentTests(first.getTests(), second.getTests())) {
      comparisonMessages.add(
          String.format("Suite: %s, with id: %s and suite: %s, with id: %s have different tests.",
              first.getName(), first.getCorrelationId(), second.getName(),
              second.getCorrelationId()));
    }
  }

  private static boolean suitesHaveDifferentTests(List<Test> firstTests, List<Test> secondTests) {
    return areCollectionsDifferent(firstTests, secondTests, SuiteComparator::areTestsDifferent);
  }

  private static boolean areTestsDifferent(Collection<Test> firstTests,
      Collection<Test> secondTests) {
    List<String> firstTestsNames = firstTests.stream()
        .map(Test::getName)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    List<String> secondTestsNames = secondTests.stream()
        .map(Test::getName)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    boolean suitesHaveDifferentTestsCount = firstTestsNames.size() != secondTestsNames.size();

    boolean firstHasUniqueTests =
        Sets.difference(new HashSet<>(firstTestsNames),
            new HashSet<>(secondTestsNames)).size() != 0;
    boolean secondHasUniqueTests = Sets.difference(new HashSet<>(secondTestsNames),
        new HashSet<>(firstTestsNames)).size() != 0;

    boolean suitesHaveDifferentTests = firstHasUniqueTests || secondHasUniqueTests;

    return suitesHaveDifferentTestsCount || suitesHaveDifferentTests;
  }

  private static void compareSteps(Suite first, Suite second, List<String> comparisonMessages) {
    if (isNullOrEmpty(first.getTests()) || isNullOrEmpty(first.getTests())) {
      return;
    }

    first.getTests().forEach(firstTest -> {
      Optional<Test> secondTest = second.getTests().stream()
          .filter(found -> found.getName().equals(firstTest.getName()))
          .findFirst();

      if (secondTest.isPresent()) {
        if (testsHaveDifferentSteps(firstTest, secondTest.get())) {
          comparisonMessages.add(String.format(
              "Tests: %s in suite: %s, with id: %s and suite: %s, with id: %s have different structure.",
              firstTest.getName(), first.getName(), first.getCorrelationId(),
              second.getName(), second.getCorrelationId()));
        }
      }
    });
  }

  private static boolean testsHaveDifferentSteps(Test first, Test second) {
    if (isNullOrEmpty(first.getUrls()) && !isNullOrEmpty(second.getUrls())) {
      return true;
    }

    Collection<List<Url>> urlPairs = Stream
        .concat(first.getUrls().stream(), second.getUrls().stream())
        .collect(Collectors.groupingBy(Url::getName))
        .values();

    if (anyHasMissingPair(urlPairs)) {
      return true;
    }

    List<Url> firstUrlPair = urlPairs.iterator().next();

    return stepsHaveDifferentCollectorsOrComparators(firstUrlPair.get(0).getSteps(),
        firstUrlPair.get(1).getSteps());
  }

  private static boolean stepsHaveDifferentCollectorsOrComparators(
      List<Step> firstStep, List<Step> secondStep) {
    return areCollectionsDifferent(firstStep, secondStep, SuiteComparator::areOperationsDifferent);
  }

  private static <T> boolean areCollectionsDifferent(Collection<T> first, Collection<T> second,
      BiFunction<Collection<T>, Collection<T>, Boolean> compareDifference) {
    if (isNullOrEmpty(first, second)) {
      return false;
    }

    if (isNullOrEmpty(first) ^ isNullOrEmpty(second) || first.size() != second.size()) {
      return true;
    }

    return compareDifference.apply(first, second);
  }

  private static <T extends Operation> boolean areOperationsDifferent(Collection<T> first,
      Collection<T> second) {
    return first.size() != second.size() ||
        forEachFindFirst(first, second,
            (operation1, operation2) -> {
              boolean typeAndParamsDifferent = !areTypeAndParamsEqual(operation1, operation2);

              if (operation1 instanceof Step && operation2 instanceof Step) {
                return typeAndParamsDifferent ||
                    areComparatorsDifferent(((Step) operation1).getComparators(),
                        ((Step) operation2).getComparators());
              }

              return typeAndParamsDifferent;
            });
  }

  private static boolean areComparatorsDifferent(Set<Comparator> first, Set<Comparator> second) {
    return areCollectionsDifferent(first, second,
        (a, b) -> areOperationsDifferent(new HashSet<>(a), new HashSet<>(b)));
  }

  private static boolean areTypeAndParamsEqual(Operation first, Operation second) {
    return first.getType().equals(second.getType()) &&
        first.getParameters().equals(second.getParameters());
  }

  private static <T> boolean anyHasMissingPair(Collection<List<T>> pairs) {
    return pairs.stream().anyMatch(list -> list.size() != 2);
  }

  private static <T extends Operation> boolean forEachFindFirst(Iterable<T> first,
      Iterable<T> second,
      BiFunction<T, T, Boolean> predicate) {
    Iterator<T> i1 = first.iterator();
    Iterator<T> i2 = second.iterator();
    while (i1.hasNext() && i2.hasNext()) {
      if (predicate.apply(i1.next(), i2.next())) {
        return true;
      }
    }
    return false;
  }

  private static boolean isNullOrEmpty(Collection... tests) {
    return Arrays.stream(tests).allMatch(test -> test == null || test.isEmpty());
  }
}
