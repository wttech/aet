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
import javax.annotation.Nullable;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuiteRerun {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerun.class);

  @Reference
  private static MetadataDAO metadataDAO;

  private SuiteRerun() {
  }

  public static Suite getAndPrepareSuite(DBKey dbKey, String correlationId, String suiteName,
      @Nullable String testName) {
    Suite suite = null;
    try {
      suite = getSuiteFromMetadata(dbKey, correlationId, suiteName);
    } catch (StorageException e) {
      LOGGER.error("Read metadata from DB problem!", e);
    }
    prepareSuiteToRerun(suite, testName);
    return suite;
  }

  private static Suite getSuiteFromMetadata(DBKey dbKey, String correlationId, String suiteName)
      throws StorageException {
    if (isValidCorrelationId(correlationId)) {
      return metadataDAO.getSuite(dbKey, correlationId);
    } else if (isValidName(suiteName)) {
      return metadataDAO.getLatestRun(dbKey, suiteName);
    } else {
      return null;
    }
  }

  private static void prepareSuiteToRerun(Suite suite, @Nullable String testName) {
    suite.setRerunned(true);
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
                step.getComparators().stream()
                    .filter(Objects::nonNull)
                    .forEach(comparator -> {
                      comparator.setStepResult(null);
                      comparator.setFilters(new ArrayList<>());
                    });
              });
        });
  }
}

