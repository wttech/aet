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
import com.cognifide.aet.communication.api.metadata.Suite.Timestamp;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.wrappers.MetadataRunDecorator;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.communication.api.wrappers.SuiteRunWrapper;
import com.cognifide.aet.communication.api.wrappers.TestRunWrapper;
import com.cognifide.aet.communication.api.wrappers.UrlRunWrapper;
import com.cognifide.aet.executor.model.CorrelationIdGenerator;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SuiteRerun {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerun.class);

  private SuiteRerun() {
  }

  static Run getAndPrepareObject(MetadataDAO metadataDAO, DBKey dbKey, String correlationId,
      String suiteName, String testName, String urlName) {
    Suite suite = null;
    try {
      suite = getSuiteFromMetadata(metadataDAO, dbKey, correlationId, suiteName);
      suite.setRunTimestamp(new Timestamp(System.currentTimeMillis()));
    } catch (StorageException e) {
      LOGGER.error("Read metadata from DB problem!", e);
    }
    Run objectToRunWrapper = null;
    if (isSuiteRerun(testName, urlName)) {
      prepareSuiteToRerun(suite);
      objectToRunWrapper = new SuiteRunWrapper(suite);
    } else if (isTestRerun(testName, urlName)) {
      Test test = suite.getTest(testName);
      objectToRunWrapper = new MetadataRunDecorator(new TestRunWrapper(test), suite);
    } else if (isUrlRerun(testName, urlName)) {
      Test test = suite.getTest(testName);
      Url url = test.getUrl(urlName);
      objectToRunWrapper = new MetadataRunDecorator(new UrlRunWrapper(url, test), suite);
    }
    return objectToRunWrapper;
  }

  private static boolean isSuiteRerun(String testName, String urlName) {
    return testName == null && urlName == null;
  }

  private static boolean isTestRerun(String testName, String urlName) {
    return testName != null && urlName == null;
  }

  private static boolean isUrlRerun(String testName, String urlName) {
    return testName != null && urlName != null;
  }

  public static Suite getSuiteFromMetadata(MetadataDAO metadataDAO, DBKey dbKey,
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

  private static void prepareSuiteToRerun(Suite suite) {
    Optional.ofNullable(suite)
        .ifPresent(s ->
          s.setCorrelationId(CorrelationIdGenerator
              .generateCorrelationId(s.getCompany(), s.getProject(), s.getName()))
        );
  }

}
