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
    // default constructor
  }

  // for unit tests
  SuiteValidator(MetadataDAO metadataDAO) {
    this.metadataDAO = metadataDAO;
  }

  public String validateTestSuiteRun(TestSuiteRun testSuiteRun) {
    boolean patternFromSameProject = isPatternFromSameProject(testSuiteRun);
    if (!patternFromSameProject) {
      return String
          .format("Incorrect pattern: '%s'. Must belong to same company (%s) and project (%s).",
              testSuiteRun.getPatternCorrelationId(),
              testSuiteRun.getCompany(),
              testSuiteRun.getProject());
    }
    boolean patternValid = isPatternInDatabase(testSuiteRun);
    if (!patternValid) {
      return String
          .format("Incorrect pattern: correlationId='%s', suiteName='%s'. Not found in database.",
              testSuiteRun.getPatternCorrelationId(), testSuiteRun.getPatternSuite());
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
   * Validates if the pattern is from the same project and company. This is because currently AET is
   * not supporting cross-projects patterns.
   *
   * @param testSuiteRun suite to be tested
   * @return true if suite is OK
   */
  private boolean isPatternFromSameProject(TestSuiteRun testSuiteRun) {
    boolean sameProject;
    String pattern = testSuiteRun.getPatternCorrelationId();
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

  private boolean isPatternInDatabase(TestSuiteRun testSuiteRun) {
    boolean valid = false;
    SimpleDBKey dbKey = new SimpleDBKey(testSuiteRun.getCompany(), testSuiteRun.getProject());
    String patternCorrelationId = testSuiteRun.getPatternCorrelationId();
    String patternSuiteName = testSuiteRun.getPatternSuite();
    if (patternCorrelationId == null && patternSuiteName == null) {
      valid = true;
    } else {
      Suite patternSuite = null;
      try {
        if (patternCorrelationId != null) {
          patternSuite = metadataDAO.getSuite(dbKey, patternCorrelationId);
        } else {
          patternSuite = metadataDAO.getLatestRun(dbKey, patternSuiteName);
        }
      } catch (StorageException se) {
        LOG.error(
            "error while retrieving suite from mongo db: '{}', correlationId: '{}', suiteName: '{}'",
            dbKey, patternCorrelationId, patternSuiteName, se);
      }
      if (patternSuite != null) {
        valid = true;
      }
    }
    return valid;
  }
}
