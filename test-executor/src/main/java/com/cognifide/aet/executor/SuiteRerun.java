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

import static com.cognifide.aet.rest.BasicDataServlet.isValidCorrelationId;
import static com.cognifide.aet.rest.BasicDataServlet.isValidName;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SuiteRerun {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerun.class);

  private SuiteRerun() {
  }

  static Suite getAndPrepareSuite(MetadataDAO metadataDAO, DBKey dbKey, String correlationId,
      String suiteName,
      String testName) {
    Suite suite = null;
    try {
      suite = getSuiteFromMetadata(metadataDAO, dbKey, correlationId, suiteName);
    } catch (StorageException e) {
      LOGGER.error("Read metadata from DB problem!", e);
    }
    prepareSuiteToRerun(suite, testName);
    return suite;
  }

  private static Suite getSuiteFromMetadata(MetadataDAO metadataDAO, DBKey dbKey,
      String correlationId, String suiteName)
      throws StorageException {
    if (isValidCorrelationId(correlationId)) {
      return metadataDAO.getSuite(dbKey, correlationId);
    } else if (isValidName(suiteName)) {
      return metadataDAO.getLatestRun(dbKey, suiteName);
    } else {
      return null;
    }
  }

  private static void prepareSuiteToRerun(Suite suite, String testName) {
    Optional.ofNullable(suite)
        .ifPresent(s -> {
          suite.setRerunned(false);
          Optional.ofNullable(testName)
              .map(s::getTest)
              .ifPresent(testToRerun -> {
                s.setRerunned(true);
                s.removeAllTests();
                s.addTest(testToRerun);
              });
          cleanDataFromSuite(s);
        });
  }

  private static void cleanDataFromSuite(Suite suite) {
    suite.getTests().stream()
        .map(Test::getUrls)
        .flatMap(Collection::stream)
        .forEach(url -> {
          url.setCollectionStats(null);
          url.getSteps()
              .forEach(step -> {
                step.setStepResult(null);
                if (step.getComparators() != null) {
                  step.getComparators()
                      .forEach(comparator -> {
                        comparator.setStepResult(null);
                        comparator.setFilters(new ArrayList<>());
                      });
                }
              });
        });
  }
}

