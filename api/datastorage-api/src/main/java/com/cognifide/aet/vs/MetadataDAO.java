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
package com.cognifide.aet.vs;


import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface MetadataDAO extends Serializable {

  /**
   * Saves suite into .metadata collection.
   *
   * @param suite to save
   * @return updated suite.
   */
  Suite saveSuite(Suite suite) throws StorageException, ValidatorException;

  /**
   * Updates suite in .metadata collection only if older version exist
   *
   * @param suite new suite version to save
   * @return updated suite.
   */
  Suite updateSuite(Suite suite) throws StorageException, ValidatorException;

  /**
   * @param dbKey - key with project and company name
   * @param correlationId - suite run identificator
   * @return Suite object found by given criteria or null.
   */
  Suite getSuite(DBKey dbKey, String correlationId) throws StorageException;

  /**
   * @param dbKey - key with project and company name
   * @param name - name of suite
   * @param version - version of suite
   * @return Suite object found by given criteria or null.
   */
  Suite getSuite(DBKey dbKey, String name, String version) throws StorageException;

  /**
   * @param dbKey - key with project and company name
   * @param name - name of suite
   * @return Suite object from the latest run (with currently maxVersion) or null if no run were
   * performed before for this suite.
   */
  Suite getLatestRun(DBKey dbKey, String name) throws StorageException;

  /**
   * @param dbKey - key with project and company name
   * @return List of suites that were found in database.
   */
  List<Suite> listSuites(DBKey dbKey) throws StorageException;

  /**
   * @param dbKey - key with project and company name
   * @param name - name of suite
   * @return List of suite's correlationsIds & versions that were found in database.
   */
  List<SuiteVersion> listSuiteVersions(DBKey dbKey, String name) throws StorageException;

  /**
   * Removes suite from .metadata collection.
   *
   * @param dbKey - key with project and company name
   * @param correlationId - suite run identificator
   * @param version - suite run version
   * @return true when suite removed successfully, false otherwise.
   */
  boolean removeSuite(DBKey dbKey, String correlationId, Long version) throws StorageException;

  /**
   * Returns list of projects in for given company or all projects
   *
   * @param company -  company name, re
   * @return list of projects in as DBKeys
   */
  Collection<DBKey> getProjects(String company) throws StorageException;

  Suite getSuiteByChecksum(String hashFromSuite);
}
