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
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuiteRerun {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerun.class);

  private SuiteRerun() {
  }

  public static Suite getAndPrepareSuite(MetadataDAO metadataDAO, DBKey dbKey, String correlationId, String suiteName,
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

  private static Suite getSuiteFromMetadata(MetadataDAO metadataDAO, DBKey dbKey, String correlationId, String suiteName)
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
        .ifPresent(s -> s.setRerunned(true));
    Optional.ofNullable(testName)
        .map(test -> suite.getTest(testName))
        .ifPresent(testToRerun -> {
          suite.removeAllTests();
          suite.addTest(testToRerun);
          cleanDataFromSuite(testToRerun);
        });
  }

  private static void cleanDataFromSuite(Test test) {
    test.getUrls().stream()
        .forEach(url -> {
          url.setCollectionStats(null);
          url.getSteps()
              .forEach(step -> {
                step.setStepResult(null);
                if(step.getComparators() != null){
                  step.getComparators().stream()
                      .forEach(comparator -> {
                        comparator.setStepResult(null);
                        comparator.setFilters(new ArrayList<>());
                      });
                }
              });
        });
  }
}

