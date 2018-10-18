/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.aet.runner.processing.data;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = SuiteDataService.class)
public class SuiteDataService {

  @Reference
  private MetadataDAO metadataDAO;

  /**
   * @param currentRun - current suite run
   * @return suite wrapper with all patterns from the last or specified (see Suite.patternCorrelationId) run, if this is the first run of the suite, patterns will be empty.
   */
  public Suite enrichWithPatterns(final Suite currentRun) throws StorageException {
    final SimpleDBKey dbKey = new SimpleDBKey(currentRun);
    Suite lastVersion = metadataDAO.getLatestRun(dbKey, currentRun.getName());
    String checkSumCurrentRunProject = currentRun.getCheckSum();

    if (isNullOrEmpty(checkSumCurrentRunProject)) {// run without checksum
      Suite pattern;

      if (!isNullOrEmpty(currentRun.getPatternCorrelationId())) {
        //if exist id
        pattern = metadataDAO.getSuite(dbKey, currentRun.getPatternCorrelationId());
      } else {
        pattern = lastVersion;
      }
      return SuiteMergeStrategy.merge(currentRun, lastVersion, pattern);


    } else {
      Suite patternByCorrelationId;
      Suite patternByChecksum = metadataDAO.getSuiteByChecksum(dbKey, checkSumCurrentRunProject);
      // in db exits checksum
      if (patternByChecksum != null) {//if pattern exist for checksum
        return SuiteMergeStrategy.merge(currentRun, lastVersion, patternByChecksum);
      } else {
        if (!isNullOrEmpty(currentRun.getPatternCorrelationId())) {//// exist pattern
          patternByCorrelationId = metadataDAO.getSuite(dbKey, currentRun.getPatternCorrelationId());
          updateSuit(patternByCorrelationId);
        } else { //no exist, first time?
          patternByCorrelationId = lastVersion;
        }
        return SuiteMergeStrategy.merge(currentRun, lastVersion, patternByCorrelationId);
      }
    }
  }

  private void updateSuit(Suite currentRun) throws StorageException {
    try {
      metadataDAO.updateSuite(currentRun);
    } catch (ValidatorException e) {
      e.printStackTrace();
    }
  }


  public Suite saveSuite(final Suite suite) throws ValidatorException, StorageException {
    return metadataDAO.saveSuite(suite);
  }
}
