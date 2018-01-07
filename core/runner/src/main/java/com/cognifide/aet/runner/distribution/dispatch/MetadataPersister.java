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
package com.cognifide.aet.runner.distribution.dispatch;

import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.inject.Inject;
import java.util.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataPersister extends Observable {

  private static final Logger LOGGER = LoggerFactory.getLogger(MetadataPersister.class);

  private final SuiteIndexWrapper suite;

  private final MetadataDAO metadataDAO;

  private volatile boolean metadataPersistRequested;

  @Inject
  public MetadataPersister(MetadataDAO metadataDAO, SuiteIndexWrapper suite) {
    this.metadataDAO = metadataDAO;
    this.suite = suite;
  }

  public boolean persistMetadataAndNotifyObservers() {
    boolean result = persistMetadata();
    setChanged();
    notifyObservers();
    return result;
  }

  /*
   * Saves metadata into DB. This method may be executed only once per ReportDispatcher life. All other
   * attempts will be ignored and properly logged with error status.
   *
   * @return <b>true</b> if and only if metadata was persisted, <b>false</b> otherwise.

   */
  public synchronized boolean persistMetadata() {
    boolean requestSent = false;
    if (!metadataPersistRequested) {
      metadataPersistRequested = true;
      try {
        metadataDAO.saveSuite(suite.get());
        requestSent = true;
      } catch (StorageException e) {
        LOGGER.error("Unable to save suite metadata! CorrelationId: '{}'",
            suite.get().getCorrelationId(), e);
      } catch (ValidatorException e) {
        LOGGER.error("Invalid suite structure, suite won't be saved.: '{}'", e.getIssues(), e);
      }
    } else {
      LOGGER.error("Metadata persistence already requested! CorrelationId: {}",
          suite.get().getCorrelationId());
    }
    return requestSent;
  }

}
