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
package com.cognifide.aet.vs.artifacts;

import com.cognifide.aet.vs.Artifact;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class ArtifactsDAOMongoDBImpl implements ArtifactsDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactsDAOMongoDBImpl.class);

  private static final String ARTIFACTS_COLLECTION_NAME = "artifacts";

  private static final String FILES_COLLECTION_SUFFIX = ".files";

  private static final String MD5_FIELD_NAME = "md5";

  private static final String ID_FIELD_NAME = "_id";

  private static final String UPLOAD_DATE_FIELD_NAME = "uploadDate";

  private static final String DEFAULT_CONTENT_TYPE = "application/json";

  private static final Gson GSON = new Gson();

  @Reference
  private transient MongoDBClient client;

  @Override
  public String saveArtifact(DBKey dbKey, InputStream data, String contentType) {
    String resultObjectId = null;
    GridFS gfs = getGridFS(dbKey);
    GridFSInputFile file = gfs.createFile(data);
    if (file != null) {
      file.setContentType(contentType);
      file.save();
      resultObjectId = file.getId().toString();
    }
    return resultObjectId;
  }

  @Override
  public String saveArtifact(DBKey dbKey, InputStream data) {
    return saveArtifact(dbKey, data, DEFAULT_CONTENT_TYPE);
  }

  @Override
  public String saveArtifact(DBKey dbKey, String data) {
    return saveArtifact(dbKey, IOUtils.toInputStream(data, StandardCharsets.UTF_8));
  }

  @Override
  public <T> String saveArtifactInJsonFormat(DBKey dbKey, T data) {
    return saveArtifact(dbKey, GSON.toJson(data));
  }

  @Override
  public Artifact getArtifact(DBKey dbKey, String objectID) {
    Artifact artifact = null;

    GridFS gfs = getGridFS(dbKey);
    BasicDBObject query = new BasicDBObject();
    query.put(ID_FIELD_NAME, new ObjectId(objectID));
    GridFSDBFile file = gfs.findOne(query);
    if (file != null) {
      artifact = new Artifact(file.getInputStream(), file.getContentType());
    }
    return artifact;
  }

  @Override
  public String getArtifactAsString(DBKey dbKey, String objectID) throws IOException {
    String result = null;
    InputStream artifactStream = null;
    try {
      artifactStream = getArtifact(dbKey, objectID).getArtifactStream();
      if (artifactStream != null) {
        result = IOUtils.toString(artifactStream, StandardCharsets.UTF_8);
      }
    } catch (IOException e) {
      throw new IOException(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(artifactStream);
    }
    return result;
  }

  @Override
  public <T> T getJsonFormatArtifact(DBKey dbKey, String objectID, Type type) throws
      IOException {
    return GSON.fromJson(getArtifactAsString(dbKey, objectID), type);
  }

  @Override
  public String getArtifactMD5(DBKey dbKey, String objectID) {
    final String dbName = MongoDBClient.getDbName(dbKey.getCompany(), dbKey.getProject());

    FindIterable findIterable =
        client.getDatabase(dbName)
            .getCollection(ARTIFACTS_COLLECTION_NAME + FILES_COLLECTION_SUFFIX)
            .find(new Document(ID_FIELD_NAME, new ObjectId(objectID)));
    Document fileMetadata = (Document) findIterable.first();

    if (fileMetadata == null) {
      LOGGER.error("Unable to find file artifact with ObjectID: {}", objectID);
    }

    return fileMetadata != null ? fileMetadata.get(MD5_FIELD_NAME).toString() : null;
  }

  @Override
  public Date getArtifactUploadDate(DBKey dbKey, String objectID) {
    final String dbName = MongoDBClient.getDbName(dbKey.getCompany(), dbKey.getProject());

    FindIterable findIterable =
        client.getDatabase(dbName)
            .getCollection(ARTIFACTS_COLLECTION_NAME + FILES_COLLECTION_SUFFIX)
            .find(new Document(ID_FIELD_NAME, new ObjectId(objectID)));
    Document fileMetadata = (Document) findIterable.first();

    if (fileMetadata == null) {
      LOGGER.error("Unable to find file artifact with ObjectID: {}", objectID);
    }

    return fileMetadata != null ? (Date) fileMetadata.get(UPLOAD_DATE_FIELD_NAME) : null;
  }

  @Override
  public void removeArtifacts(DBKey dbKey, Set<String> artifactsToRemove) {
    GridFS gfs = getGridFS(dbKey);
    for (String artifactId : artifactsToRemove) {
      LOGGER.debug("Removing artifact {} from {}", artifactId, dbKey);
      gfs.remove(new ObjectId(artifactId));
    }
  }

  private GridFS getGridFS(DBKey dbKey) {
    final String dbName = MongoDBClient.getDbName(dbKey.getCompany(), dbKey.getProject());
    return new GridFS(client.getDB(dbName, true), ARTIFACTS_COLLECTION_NAME);
  }
}


