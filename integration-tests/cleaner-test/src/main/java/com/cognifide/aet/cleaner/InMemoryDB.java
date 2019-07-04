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
package com.cognifide.aet.cleaner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

public class InMemoryDB {

  private static final String URI_PATTERN = "mongodb://localhost:%d";

  private final MongoClient client;
  private final MongoServer server;
  private final String URI;
  private final String dbName;
  private final String dataDir;
  private final String artifactsColName;
  private final String metadataColName;

  public InMemoryDB(String dbName, String dataDir, String metadataColName, String artifactsColName) {
    this.dbName = dbName;
    this.dataDir = dataDir;
    this.metadataColName = metadataColName;
    this.artifactsColName = artifactsColName;

    server = new MongoServer(new MemoryBackend());
    InetSocketAddress address = server.bind();
    URI = String.format(URI_PATTERN, address.getPort());
    client = new MongoClient(new MongoClientURI(URI));
  }

  public void shutdown() {
    server.shutdown();
    client.close();
  }


  public void loadProjectData(String projectDataDir) throws IOException {
    String[] collectionNames = new String[]{artifactsColName, metadataColName};
    for (String collectionName : collectionNames) {
      String collectionDir = String
          .format("%s/%s/%s", dataDir, projectDataDir, collectionName);
      File[] collectionFiles = new File(getClass().getResource(collectionDir).getFile())
          .listFiles();
      if (collectionFiles != null) {
        for (File file : collectionFiles) {
          String json = FileUtils.readFileToString(file, "UTF-8");
          getDatabase().getCollection(collectionName).insertOne(Document.parse(json));
        }
      }
    }
  }

  public String getURI() {
    return URI;
  }

  public MongoCollection<Document> getMetadataDocs() {
    return getDatabase().getCollection(metadataColName);
  }

  public MongoCollection<Document> getArtifactDocs() {
    return getDatabase().getCollection(artifactsColName);
  }

  public long getMetadataDocsCount() {
    return getMetadataDocs().countDocuments();
  }

  public long getArtifactsDocsCount() {
    return getArtifactDocs().countDocuments();
  }

  private MongoDatabase getDatabase() {
    return client.getDatabase(dbName);
  }
}
