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

import com.cognifide.aet.communication.api.messages.ValidationMessage;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.runner.processing.MessagesSender;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = SuiteDataService.class)
public class SuiteDataService {

  @Reference
  private MetadataDAO metadataDAO;

  public SuiteDataService() {
  }

  public SuiteDataService(MetadataDAO metadataDAO) {
    this.metadataDAO = metadataDAO;
  }

  /**
   * @param currentRun - current suite run
   * @return suite wrapper with all patterns from the last or specified (see
   * Suite.patternCorrelationIds) runs, if this is the first run of the suite, patterns will be
   * empty.
   */
  public Suite enrichWithPatterns(final Suite currentRun)
      throws StorageException {
    final SimpleDBKey dbKey = new SimpleDBKey(currentRun);
    Suite lastVersion = metadataDAO.getLatestRun(dbKey, currentRun.getName());

    List<Suite> patterns = getPatternSuites(currentRun, dbKey, lastVersion);

    return SuiteMergeStrategy.merge(currentRun, lastVersion, patterns);
  }

  private List<Suite> getPatternSuites(Suite currentRun, SimpleDBKey dbKey, Suite lastVersion)
      throws StorageException {
    List<Suite> patterns = new ArrayList<>();
    if (hasSharedPatterns(currentRun)) {
      getPatternsById(currentRun, dbKey, patterns);
      getPatternByChecksum(currentRun, dbKey, patterns);
    } else {
      patterns.add(lastVersion);
    }

    patterns.removeIf(Objects::isNull);
    return patterns;
  }

  private void getPatternsById(Suite currentRun, SimpleDBKey dbKey, List<Suite> patterns)
      throws StorageException {
    if (currentRun.getPatternCorrelationIds() != null) {
      for (String id : currentRun.getPatternCorrelationIds()) {
        patterns.add(metadataDAO.getSuite(dbKey, id));
      }
    }
  }

  private void getPatternByChecksum(Suite currentRun, SimpleDBKey dbKey, List<Suite> patterns)
      throws StorageException {
    if (currentRun.getProjectChecksum() != null) {
      Suite suiteByChecksum = metadataDAO
          .getSuiteByChecksum(dbKey, currentRun.getProjectChecksum());

      if (suiteByChecksum != null) {
        patterns.add(suiteByChecksum);
      }
    }
  }

  private boolean hasSharedPatterns(Suite currentRun) {
    return currentRun.getPatternCorrelationIds() != null &&
        !currentRun.getPatternCorrelationIds().isEmpty() ||
        currentRun.getProjectChecksum() != null &&
            !currentRun.getProjectChecksum().isEmpty();
  }

  public Suite saveSuite(final Suite suite) throws ValidatorException, StorageException {
    return metadataDAO.saveSuite(suite);
  }

  public Suite replaceSuite(final Suite oldSuite, final Suite newSuite) throws StorageException {
    metadataDAO.replaceSuite(oldSuite, newSuite);
    return newSuite;
  }

  public Suite getSuite(DBKey dbKey, String correlationId) throws StorageException {
    return metadataDAO.getSuite(dbKey, correlationId);
  }

  public List<String> comparePatternSuites(Suite currentRun) throws StorageException {
    final SimpleDBKey dbKey = new SimpleDBKey(currentRun);
    Suite lastVersion = metadataDAO.getLatestRun(dbKey, currentRun.getName());

    List<Suite> allSuites = getPatternSuites(currentRun, dbKey, lastVersion);
    allSuites.add(currentRun);

    return SuiteComparator.compare(allSuites);
  }
}
