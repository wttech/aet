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
package com.cognifide.aet.job.common;

import com.cognifide.aet.vs.Artifact;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ArtifactDAOMock implements ArtifactsDAO {

  private static final String MOCK_ROOT = "mock";

  private static final Gson GSON = new Gson();

  private List<byte[]> savedArtifactDatas = new ArrayList<>();

  private final String mocksPath;

  public ArtifactDAOMock(final Class clazz) {
    String module = clazz.getSimpleName();
    this.mocksPath = MOCK_ROOT + "/" + module;
  }

  @Override
  public String saveArtifact(DBKey dbKey, InputStream data, String contentType) {
    return null;
  }

  @Override
  public String saveArtifact(DBKey dbKey, InputStream data) {
    try {
      savedArtifactDatas.add(IOUtils.toByteArray(data));
    } catch (IOException e) {
      return null;
    }
    return "";
  }

  @Override
  public String saveArtifact(DBKey dbKey, String data) {
    savedArtifactDatas.add(data.getBytes(StandardCharsets.UTF_8));
    return "";
  }

  @Override
  public <T> String saveArtifactInJsonFormat(DBKey dbKey, T data) {
    return saveArtifact(dbKey, GSON.toJson(data));
  }

  @Override
  public Artifact getArtifact(DBKey dbKey, String objectID) {

    URL filePath = getClass().getClassLoader().getResource(mocksPath + "/" + objectID);
    Artifact result = null;
    try {
      result = new Artifact(new FileInputStream(filePath.getFile()), StringUtils.EMPTY);
    } catch (FileNotFoundException e) {
      return null;
    }
    return result;
  }

  @Override
  public String getArtifactAsString(DBKey dbKey, String objectID) throws IOException {
    return IOUtils
        .toString(getArtifact(null, objectID).getArtifactStream(), StandardCharsets.UTF_8);
  }

  @Override
  public <T> T getJsonFormatArtifact(DBKey dbKey, String objectID, Type type) throws IOException {
    return GSON.fromJson(getArtifactAsString(dbKey, objectID), type);
  }

  @Override
  public String getArtifactMD5(DBKey dbKey, String objectID) {
    return null;
  }

  @Override
  public Date getArtifactUploadDate(DBKey dbKey, String objectID) {
    return null;
  }

  @Override
  public void removeArtifacts(DBKey dbKey, Set<String> artifactsToRemove) {
    //do nothing here
  }

  /**
   * workaround to get data for verification
   *
   * @return first saved data as byte Array or null
   */
  public byte[] getFirstSavedArtifactData() {
    return savedArtifactDatas.isEmpty() ? null : savedArtifactDatas.get(0);
  }

  public String getArtifactAsUTF8String(DBKey dbKey, String objectID) throws IOException {
    String result;
    URL filePath = getClass().getClassLoader().getResource(mocksPath + "/" + objectID);
    FileInputStream fsi;
    fsi = new FileInputStream(filePath.getFile());
    result = IOUtils.toString(fsi, StandardCharsets.UTF_8);
    return result;
  }
}
