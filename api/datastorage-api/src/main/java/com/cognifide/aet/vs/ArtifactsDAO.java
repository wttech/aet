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


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Set;

public interface ArtifactsDAO extends Serializable {

  /**
   * Saves data into gridfs files collection with provided content type
   *
   * @param dbKey - key with project and company name
   * @param data - to save in raw form
   * @param contentType - to content type of saved artifact
   * @return saved artifact ID
   */
  String saveArtifact(DBKey dbKey, InputStream data, String contentType);

  /**
   * Saves data into gridfs files collection with default content type
   *
   * @param dbKey - key with project and company name
   * @param data - to save in raw form
   * @return saved artifact ID
   */
  String saveArtifact(DBKey dbKey, InputStream data);

  /**
   * Saves data into gridfs files collection.
   *
   * @param dbKey - key with project and company name
   * @param data - to save
   * @return saved artifact ID
   */
  String saveArtifact(DBKey dbKey, String data);

  /**
   * Saves data into gridfs files collection.
   *
   * @param dbKey - key with project and company name
   * @param data to save in String form
   * @param <T> - data to save
   * @return saved artifact ID
   */
  <T> String saveArtifactInJsonFormat(DBKey dbKey, T data);

  /**
   * @param dbKey - key with project and company name
   * @param objectID - suite run identificator
   * @return artifact object found by given criteria or null.
   */
  Artifact getArtifact(DBKey dbKey, String objectID);

  /**
   * @param dbKey - key with project and company name
   * @param objectID - suite run identificator
   * @return artifact object found by given criteria or null as String
   */
  String getArtifactAsString(DBKey dbKey, String objectID) throws IOException;

  /**
   * @param dbKey - key with project and company name
   * @param objectID - suite run identificator
   * @param type - type of artifact to deserialize
   * @param <T> - returned artifact.
   * @return artifact object found by given criteria or null
   */
  <T> T getJsonFormatArtifact(DBKey dbKey, String objectID, Type type) throws IOException;

  /**
   * @param dbKey - key with project and company name
   * @param objectID - suite run identificator
   * @return MD5 of artifact without fetching all binary content or null if pattern does not exists.
   */
  String getArtifactMD5(DBKey dbKey, String objectID);

  /**
   * @param dbKey - key with project and company name
   * @param objectID - suite run identificator
   * @return artifact Upload Date.
   */
  Date getArtifactUploadDate(DBKey dbKey, String objectID);

  /**
   * Removes artifacts with provided ids from gridfs files collection.
   *
   * @param dbKey - key with project and company name
   * @param artifactsToRemove - set of Artifact objectIDs.
   */
  void removeArtifacts(DBKey dbKey, Set<String> artifactsToRemove);
}
