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
package com.cognifide.aet.vs.mongodb;

import com.cognifide.aet.vs.metadata.MetadataDAOMongoDBImpl;
import com.cognifide.aet.vs.mongodb.configuration.MongoDBClientConf;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = MongoDBClient.class, immediate = true)
@Designate(ocd = MongoDBClientConf.class)
public class MongoDBClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBClient.class);

  private static final int MAX_DB_NAME_LENGTH = 64;

  public static final String DB_NAME_SEPARATOR = "_";

  private String mongoUri;

  private Boolean allowAutoCreate;

  private MongoClient mongoClient;

  public MongoClient getMongoClient() {
    return mongoClient;
  }

  @Activate
  public void activate(MongoDBClientConf config) {
    setupConfiguration(config);
    try {
      setupMongoDBConnection();
    } catch (Exception e) {
      this.deactivate();
      LOGGER.error("Unable to connect to Mongo instance", e);
    }
  }

  @Deactivate
  public void deactivate() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }

  /**
   * Get collection of companies in AET system
   *
   * @return collection of companies names unless database names matches convention:
   * $company_$project and $company dosen't contain any underscrore characters
   */
  public Collection<String> getCompanies() {
    final Collection<String> companies = new ArrayList<>();

    for (String dbName : getAetsDBNames()) {
      if (!StringUtils.containsAny(dbName, DB_NAME_SEPARATOR)) {
        LOGGER.error(
            "Database name format is incorrect. It must contain at least one underscore character. Couldn't fetch company name from database name. Skip.");
      }
      String companyName = StringUtils.substringBefore(dbName, DB_NAME_SEPARATOR);
      if (StringUtils.isBlank(companyName)) {
        LOGGER.error("Comapny name is blank. It couldn't've been fetched from database name [{}] ",
            dbName);
      } else if (StringUtils.isNotBlank(companyName)) {
        companies.add(companyName);
      }

    }

    return companies;
  }

  protected void setupConfiguration(MongoDBClientConf config) {
    mongoUri = Optional.ofNullable(config.mongoURI())
        .filter(StringUtils::isNotBlank)
        .orElseGet(() -> Optional.ofNullable(System.getenv(MongoDBClientConf.MONGODB_URI_ENV))
            .orElse(MongoDBClientConf.DEFAULT_MONGODB_URI));
    allowAutoCreate = config.allowAutoCreate();
  }

  private void setupMongoDBConnection() throws UnknownHostException {
    mongoClient = new MongoClient(new MongoClientURI(mongoUri));
    testConnection();
    LOGGER.info("Mongo client connected at: " + mongoUri);
  }

  private void testConnection() {
    mongoClient.getDatabaseNames();
  }

  /**
   * Gets MonogoDB database object. It does not auto-creates database if given db name does not
   * corresponds to any db's.
   *
   * @param dbName name of database
   * @return MongoDB databaase object or null if base does not exists.
   *
   * This method is depricated. Use getDatabase(String dbName) instead.
   * @deprecated this method should be used only to get databases for GridFs
   *
   * TODO remove this method
   */
  @Deprecated
  public DB getDB(String dbName) {
    return getDB(dbName, false);
  }

  /**
   * Gets MonogoDB database object. Given that autoCreate param is set to true it will auto-create
   * database with provided name (if one does not exists yet).
   *
   * @param dbName name of database
   * @param autoCreate defines if database creation auto-creation should occur
   * @return MongoDB databaase object or null if base does not exists.
   *
   * This method is depricated. Use getDatabase(String dbName, Boolean autoCreate) instead.
   * @deprecated this method should be used only to get databases for GridFs
   *
   * TODO remove this method
   */
  @Deprecated
  public DB getDB(String dbName, Boolean autoCreate) {
    DB db = null;
    String lowerCaseDbName = dbName.toLowerCase();
    if ((allowAutoCreate && autoCreate) || getAetsDBNames().contains(lowerCaseDbName)) {
      db = mongoClient.getDB(lowerCaseDbName);
    } else {
      LOGGER.error("Database {} does not exists and was not created automatically!", dbName);
    }
    return db;
  }

  /**
   * Gets MonogoDB database object. It does not auto-creates database if given db name does not
   * corresponds to any db's.
   *
   * @param dbName name of database
   * @return MongoDB databaase object or null if base does not exists.
   */
  public MongoDatabase getDatabase(String dbName) {
    return getDatabase(dbName, false);
  }

  /**
   * Gets MonogoDB database object. Given that autoCreate param is set to true it will auto-create
   * database with provided name (if one does not exists yet).
   *
   * @param dbName name of database
   * @param autoCreate defines if database creation auto-creation should occur
   * @return MongoDB databaase object or null if base does not exists.
   */
  public MongoDatabase getDatabase(String dbName, Boolean autoCreate) {
    MongoDatabase database = null;
    String lowerCaseDbName = dbName.toLowerCase();

    if (getAetsDBNames().contains(lowerCaseDbName)) {
      database = mongoClient.getDatabase(lowerCaseDbName);
    } else if (allowAutoCreate && autoCreate) {
      database = mongoClient.getDatabase(lowerCaseDbName);
      database.createCollection(MetadataDAOMongoDBImpl.METADATA_COLLECTION_NAME);
      MongoCollection<Document> collection = database
          .getCollection(MetadataDAOMongoDBImpl.METADATA_COLLECTION_NAME);
      collection.createIndex(new BsonDocument().append("version", new BsonInt32(-1)));
      collection.createIndex(new BsonDocument()
          .append("correlationId", new BsonInt32(1))
          .append("version", new BsonInt32(-1)));
      collection.createIndex(new BsonDocument()
          .append("name", new BsonInt32(1))
          .append("version", new BsonInt32(-1)));
    }
    return database;
  }

  /**
   * Gets collection of database names that are AET system specific
   *
   * @return collection of db names
   */
  public Collection<String> getAetsDBNames() {
    final Collection<String> databaseNames = new ArrayList<>();

    for (String dbName : mongoClient.listDatabaseNames()) {
      if (!(dbName.charAt(0) == '_') && !"local".equals(dbName) && !"admin".equals(dbName)) {
        databaseNames.add(dbName);
      }
    }

    return databaseNames;
  }

  /**
   * Checks if database contains specified collection.
   *
   * @param dbName name of database
   * @param collectionName name of collection
   * @return true if database contains specified collection.
   */
  public boolean hasCollection(String dbName, String collectionName) {
    return getCollectionsList(dbName).contains(collectionName);
  }

  private Set<String> getCollectionsList(String dbName) {
    Set<String> result = Collections.emptySet();
    DB db = getDB(dbName);
    if (db != null) {
      result = db.getCollectionNames();
    }

    return result;
  }

  /**
   * @param companyName company name
   * @param projectName project name
   * @return dataBase name with applied db naming rules, based on project and company names
   */
  public static String getDbName(String companyName, String projectName) {
    String result = companyName + DB_NAME_SEPARATOR + projectName;
    return StringUtils.substring(result, 0, MAX_DB_NAME_LENGTH);
  }

  public static String getCompanyNameFromDbName(String dbName) {
    return StringUtils.substringBefore(dbName, DB_NAME_SEPARATOR);
  }

  public static String getProjectNameFromDbName(String dbName) {
    return StringUtils.substringAfter(dbName, DB_NAME_SEPARATOR);
  }

  protected String getMongoUri() {
    return mongoUri;
  }
}
