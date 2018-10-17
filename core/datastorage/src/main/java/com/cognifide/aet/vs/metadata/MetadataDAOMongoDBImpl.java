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
package com.cognifide.aet.vs.metadata;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import com.cognifide.aet.vs.SuiteVersion;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class MetadataDAOMongoDBImpl implements MetadataDAO {

  private static final long serialVersionUID = 3031952772776598636L;

  private static final Logger LOGGER = LoggerFactory.getLogger(MetadataDAOMongoDBImpl.class);

  public static final String METADATA_COLLECTION_NAME = "metadata";

  private static final String SUITE_PARAM_NAME = "name";

  private static final String SUITE_VERSION_PARAM_NAME = "version";

  private static final String CORRELATION_ID_PARAM_NAME = "correlationId";

  @Reference
  private transient MongoDBClient client;

  @Override
  public Suite saveSuite(Suite suite) throws StorageException, ValidatorException {
    MongoCollection<Document> metadata = getMetadataCollection(new SimpleDBKey(suite));
    suite.validate(null);
    LOGGER.debug("Saving suite {} to metadata collection.", suite);
    metadata.insertOne(Document.parse(suite.toJson()));
    return getSuite(new SimpleDBKey(suite), suite.getCorrelationId());
  }

  /**
   * Updates suite in .metadata collection only if older version exist. Also updates version and
   * timestamp of a suite.
   *
   * @param suite new suite version to save
   * @return updated suite.
   */
  @Override
  public Suite updateSuite(Suite suite) throws StorageException, ValidatorException {
    MongoCollection<Document> metadata = getMetadataCollection(new SimpleDBKey(suite));
    LOGGER.debug("Updating suite {} in  metadata collection.", suite);
    if (isNewestSuite(suite)) {
      suite.incrementVersion();
      suite.setRunTimestamp(new Suite.Timestamp(System.currentTimeMillis()));
      suite.validate(null);
      metadata.insertOne(Document.parse(suite.toJson()));
    } else {
      throw new StorageException("Trying to update old version or not existing suite.");
    }
    return getSuite(new SimpleDBKey(suite), suite.getCorrelationId());
  }

  private boolean isNewestSuite(Suite suite) throws StorageException {
    final Suite latestSuite = getLatestRun(new SimpleDBKey(suite), suite.getName());
    return latestSuite == null || suite.getVersion().equals(latestSuite.getVersion());
  }


  @Override
  public Suite getSuite(DBKey dbKey, String correlationId) throws StorageException {
    MongoCollection<Document> metadata = getMetadataCollection(dbKey);

    LOGGER.debug("Fetching suite with correlationId: {} ", correlationId);

    final FindIterable<Document> found = metadata
        .find(Filters.eq(CORRELATION_ID_PARAM_NAME, correlationId))
        .sort(Sorts.descending(SUITE_VERSION_PARAM_NAME))
        .limit(1);
    final Document result = found.first();
    return new DocumentConverter(result).toSuite();
  }

  @Override
  public Suite getSuite(DBKey dbKey, String name, String version) throws StorageException {
    MongoCollection<Document> metadata = getMetadataCollection(dbKey);

    LOGGER.debug("Fetching suite with name: {}, version: {}", name, version);

    final FindIterable<Document> found = metadata
        .find(Filters.and(Filters.eq(SUITE_PARAM_NAME, name),
            Filters.eq(SUITE_VERSION_PARAM_NAME, Integer.parseInt(version))));
    final Document result = found.first();
    return new DocumentConverter(result).toSuite();
  }

  @Override
  public Suite getSuiteByChecksum(String checksum) {
    return null;//todo
  }

  @Override
  public Suite getLatestRun(DBKey dbKey, String name) throws StorageException {
    MongoCollection<Document> metadata = getMetadataCollection(dbKey);
    LOGGER.debug("Fetching latest suite run for company: `{}`, project: `{}`, name `{}`.",
        dbKey.getCompany(), dbKey.getProject(), name);

    final FindIterable<Document> found = metadata
        .find(Filters.eq("name", name))
        .sort(Sorts.descending(SUITE_VERSION_PARAM_NAME))
        .limit(1);
    final Document result = found.first();
    return new DocumentConverter(result).toSuite();
  }

  @Override
  public List<Suite> listSuites(DBKey dbKey) throws StorageException {
    MongoCollection<Document> metadata = getMetadataCollection(dbKey);
    LOGGER.debug("Fetching all suites for company: `{}`, project: `{}`.", dbKey.getCompany(),
        dbKey.getProject());

    final FindIterable<Document> found = metadata
        .find()
        .sort(Sorts.descending(SUITE_VERSION_PARAM_NAME));

    return StreamSupport.stream(found.spliterator(), false)
        .map(document -> new DocumentConverter(document).toSuite())
        .collect(Collectors.toList());
  }

  public List<SuiteVersion> listSuiteVersions(DBKey dbKey, String name) throws StorageException {
    MongoCollection<Document> metadata = getMetadataCollection(dbKey);
    LOGGER.debug("Fetching all versions of suite: `{}` , company: `{}`, project: `{}`.", name,
        dbKey.getCompany(),
        dbKey.getProject());

    final FindIterable<Document> found = metadata
        .find(Filters.eq(SUITE_PARAM_NAME, name))
        .sort(Sorts.descending(SUITE_VERSION_PARAM_NAME));

    return StreamSupport.stream(found.spliterator(), false)
        .map(document -> new SuiteVersion(
            document.getString(CORRELATION_ID_PARAM_NAME),
            document.getInteger(SUITE_VERSION_PARAM_NAME)))
        .collect(Collectors.toList());
  }

  @Override
  public boolean removeSuite(DBKey dbKey, String correlationId, Long version)
      throws StorageException {
    LOGGER
        .debug("Removing suite with correlationId {}, version {} from {}.", correlationId, version,
            dbKey);
    MongoCollection<Document> metadata = getMetadataCollection(dbKey);

    final DeleteResult deleteResult = metadata.deleteOne(
        Filters.and(
            Filters.eq(CORRELATION_ID_PARAM_NAME, correlationId),
            Filters.eq(SUITE_VERSION_PARAM_NAME, version)
        ));

    return deleteResult.getDeletedCount() == 1;
  }


  @Override
  public Collection<DBKey> getProjects(String company) {
    final Collection<DBKey> result = new ArrayList<>();

    final Collection<String> databaseNames;
    if (!StringUtils.isBlank(company)) {
      databaseNames = Collections2
          .filter(client.getAetsDBNames(), Predicates.containsPattern("^" + company));
    } else {
      databaseNames = client.getAetsDBNames();
    }

    for (String dbName : databaseNames) {
      String[] companyAndProject = StringUtils.split(dbName, MongoDBClient.DB_NAME_SEPARATOR);
      result.add(new SimpleDBKey(companyAndProject[0], companyAndProject[1]));
    }
    return result;
  }

  private MongoCollection<Document> getMetadataCollection(DBKey dbKey) throws StorageException {
    final String dbName = MongoDBClient.getDbName(dbKey.getCompany(), dbKey.getProject());
    final MongoDatabase database = client.getDatabase(dbName, true);
    if (database == null) {
      throw new StorageException(
          String.format("Database %s does not exist! Contact AET administrators.", dbName));
    }
    return database.getCollection(METADATA_COLLECTION_NAME);
  }
}
