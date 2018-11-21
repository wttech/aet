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
package com.cognifide.aet.executor;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.executor.model.CollectorStep;
import com.cognifide.aet.executor.model.ComparatorStep;
import com.cognifide.aet.executor.model.TestRun;
import com.cognifide.aet.executor.model.TestSuiteRun;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import com.google.common.base.Joiner;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = SuiteValidator.class, immediate = true)
public class SuiteValidator {

  private static final Logger LOG = LoggerFactory.getLogger(SuiteValidator.class);

  private static final String SCREEN = "screen";

  private static final String CORRELATION_ID_SEPARATOR = "-";

  @Reference
  private MetadataDAO metadataDAO;

  public SuiteValidator() {
  }

  SuiteValidator(MetadataDAO metadataDAO) {
    this.metadataDAO = metadataDAO;
  }

  public String validateTestSuiteRun(TestSuiteRun testSuiteRun) {
    Set<String> differentProjectPatterns = getAnyPatternsFromDifferentProject(testSuiteRun);
    if (!differentProjectPatterns.isEmpty()) {
      return String
          .format("Incorrect patterns: '%s'. Must belong to same company (%s) and project (%s).",
              differentProjectPatterns.toString(),
              testSuiteRun.getCompany(),
              testSuiteRun.getProject());
    }
    Set<String> notFoundPatterns = anyPatternsNotInDatabase(testSuiteRun);
    if (!notFoundPatterns.isEmpty()) {
      return String
          .format("Incorrect patterns: '%s'. Not found in database.", notFoundPatterns.toString());
    }
    for (TestRun testRun : testSuiteRun.getTestRunMap().values()) {
      if (hasScreenCollector(testRun) && !hasScreenComparator(testRun)) {
        return String.format(
            "Test suite does not contain screen comparator for screen collector in '%s' test, please fix it",
            testRun.getName());
      }
    }
    return null;
  }

  /**
   * Validates if patterns are from the same project and company. This is because currently AET is
   * not supporting cross-projects patterns.
   *
   * @param testSuiteRun suite to be tested
   * @return true if suite is OK
   */
  private Set<String> getAnyPatternsFromDifferentProject(TestSuiteRun testSuiteRun) {
    return testSuiteRun.getPatternsCorrelationIds().stream()
        .filter(pattern -> !isPatternFromSameProject(testSuiteRun, pattern))
        .collect(Collectors.toSet());
  }

  private boolean isPatternFromSameProject(TestSuiteRun testSuiteRun, String pattern) {
    boolean sameProject;
    if (pattern == null) {
      // patterns will be taken from same suite automatically
      sameProject = true;
    } else {
      String suiteCorrelationIdPrefix = Joiner.on(CORRELATION_ID_SEPARATOR).join(
          testSuiteRun.getCompany(),
          testSuiteRun.getProject(),
          StringUtils.EMPTY
      );
      sameProject = pattern.startsWith(suiteCorrelationIdPrefix);
    }
    return sameProject;
  }

  private boolean hasScreenCollector(TestRun testRun) {
    for (CollectorStep collectorStep : testRun.getCollectorSteps()) {
      if (SCREEN.equalsIgnoreCase(collectorStep.getModule())) {
        return true;
      }
    }
    return false;
  }

  private boolean hasScreenComparator(TestRun testRun) {
    for (List<ComparatorStep> comparatorSteps : testRun.getComparatorSteps().values()) {
      for (ComparatorStep comparatorStep : comparatorSteps) {
        if (SCREEN.equalsIgnoreCase(comparatorStep.getType())) {
          return true;
        }
      }
    }
    return false;
  }

  private Set<String> anyPatternsNotInDatabase(TestSuiteRun testSuiteRun) {
    Set<String> notFoundPatterns = testSuiteRun.getPatternsCorrelationIds().stream()
        .filter(id -> !isPatternCorrelationIdInDatabase(testSuiteRun, id))
        .collect(Collectors.toSet());

    notFoundPatterns.addAll(testSuiteRun.getPatternsSuite().stream()
        .filter(pattern -> !isPatternSuiteInDatabase(testSuiteRun, pattern))
        .collect(Collectors.toSet()));

    return notFoundPatterns;
  }

  private boolean isPatternCorrelationIdInDatabase(TestSuiteRun testSuiteRun,
      String patternCorrelationId) {
    return isInDatabase(testSuiteRun, patternCorrelationId, true);
  }

  private boolean isPatternSuiteInDatabase(TestSuiteRun testSuiteRun, String patternSuiteName) {
    return isInDatabase(testSuiteRun, patternSuiteName, false);
  }

  private boolean isInDatabase(TestSuiteRun testSuiteRun, String identifier,
      boolean isCorrelationId) {
    SimpleDBKey dbKey = new SimpleDBKey(testSuiteRun.getCompany(), testSuiteRun.getProject());
    try {
      Suite patternSuite;
      if (isCorrelationId) {
        patternSuite = metadataDAO.getSuite(dbKey, identifier);
      } else {
        patternSuite = metadataDAO.getLatestRun(dbKey, identifier);
      }

      return patternSuite != null;

    } catch (StorageException se) {
      LOG.error(
          "error while retrieving suite from mongo db: '{}', suiteName: '{}'",
          dbKey, identifier, se);
      return false;
    }
  }

}
