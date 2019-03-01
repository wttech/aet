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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cognifide.aet.cleaner.processors.FetchAllProjectSuitesProcessor;
import com.cognifide.aet.cleaner.processors.GetMetadataArtifactsProcessor;
import com.cognifide.aet.cleaner.processors.RemoveArtifactsProcessor;
import com.cognifide.aet.cleaner.processors.RemoveMetadataProcessor;
import com.cognifide.aet.cleaner.processors.StartMetadataCleanupProcessor;
import com.cognifide.aet.cleaner.processors.SuitesRemovePredicateProcessor;
import com.cognifide.aet.cleaner.route.MetadataCleanerRouteBuilder;
import com.cognifide.aet.cleaner.time.LocalDateTimeProvider;
import com.cognifide.aet.vs.artifacts.ArtifactsDAOMongoDBImpl;
import com.cognifide.aet.vs.metadata.MetadataDAOMongoDBImpl;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.google.common.collect.ImmutableMap;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import org.apache.commons.io.FileUtils;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RunWith(ZohhakRunner.class)
public class CleanerIntegrationTest {

  private static final Long MOCKED_CURRENT_TIMESTAMP = 1551428149000L;  //March 1, 2019
  private static final String MOCKED_COMPANY_NAME = "company";
  private static final String MOCKED_PROJECT_NAME = "project";
  private static final String MOCKED_DB_NAME = String
      .format("%s_%s", MOCKED_COMPANY_NAME, MOCKED_PROJECT_NAME);

  @Rule
  public final OsgiContext context = new OsgiContext();

  private JobExecutionContext jobExecutionContext = Mockito.mock(JobExecutionContext.class);
  private JobDetail jobDetail = Mockito.mock(JobDetail.class);

  private MongoClient client;
  private MongoServer server;
  private MetadataCleanerRouteBuilder metadataCleanerRouteBuilder;
  private LocalDateTimeProvider mockedDateTimeProvider = zone -> LocalDateTime
      .ofInstant(Instant.ofEpochMilli(MOCKED_CURRENT_TIMESTAMP), zone);

  @Before
  public void setUp() {
    server = new MongoServer(new MemoryBackend());
    InetSocketAddress address = server.bind();
    String mongoURI = String.format("mongodb://localhost:%d", address.getPort());
    client = new MongoClient(new MongoClientURI(mongoURI));

    context.registerInjectActivateService(new MongoDBClient(), "mongoURI", mongoURI);
    context.registerInjectActivateService(new MetadataDAOMongoDBImpl());
    context.registerInjectActivateService(new ArtifactsDAOMongoDBImpl());

    context.registerService(LocalDateTimeProvider.class, mockedDateTimeProvider);
    context.registerInjectActivateService(new StartMetadataCleanupProcessor());
    context.registerInjectActivateService(new FetchAllProjectSuitesProcessor());
    context.registerInjectActivateService(new SuitesRemovePredicateProcessor());
    context.registerInjectActivateService(new RemoveMetadataProcessor());
    context.registerInjectActivateService(new GetMetadataArtifactsProcessor());
    context.registerInjectActivateService(new RemoveArtifactsProcessor());
    metadataCleanerRouteBuilder = context
        .registerInjectActivateService(new MetadataCleanerRouteBuilder());
  }

  @TestWith({
      "1,3,projectA",
      "1,4,projectA",
      "1,5,projectA"
  })
  public void clean_whenAllSuitesYounger_keepAllSuites(Long versionsToKeep, Long maxAge,
      String projectDataDir) throws JobExecutionException, IOException {
    setUpDataForTest(versionsToKeep, maxAge, projectDataDir);
    long metadataCountBefore = getMetadataDocs().countDocuments();
    long artifactCountBefore = getArtifactDocs().countDocuments();
    new CleanerJob().execute(jobExecutionContext);
    assertEquals(metadataCountBefore, getMetadataDocs().countDocuments());
    assertEquals(artifactCountBefore, getArtifactDocs().countDocuments());
  }

  @TestWith({
      "1,0,projectA",
      "1,1,projectA"
  })
  public void clean_whenAllSuitesOlder_keepNewestSuite(Long versionsToKeep, Long maxAge,
      String projectDataDir) throws JobExecutionException, IOException {
    setUpDataForTest(versionsToKeep, maxAge, projectDataDir);
    new CleanerJob().execute(jobExecutionContext);
    assertEquals(1, getMetadataDocs().countDocuments());
    assertEquals(1, getMetadataDocs().countDocuments());
  }

  @After
  public void tearDown() {
    client.close();
    server.shutdown();
  }

  private void setUpDataForTest(Long versionsToKeep, Long maxAge, String projectDataDir)
      throws IOException {
    createJobData(versionsToKeep, maxAge);
    insertDataToDb(projectDataDir);
  }

  private void createJobData(Long versionsToKeep, Long maxAge) {
    final JobDataMap jobData = new JobDataMap(ImmutableMap.<String, Object>builder()
        .put(CleanerJob.KEY_ROUTE_BUILDER, metadataCleanerRouteBuilder)
        .put(CleanerJob.KEY_KEEP_N_VERSIONS, versionsToKeep)
        .put(CleanerJob.KEY_REMOVE_OLDER_THAN, maxAge)
        .put(CleanerJob.KEY_COMPANY_FILTER, MOCKED_COMPANY_NAME)
        .put(CleanerJob.KEY_PROJECT_FILTER, MOCKED_PROJECT_NAME)
        .put(CleanerJob.KEY_DRY_RUN, false)
        .build());
    when(jobDetail.getJobDataMap()).thenReturn(jobData);
    when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
  }

  private void insertDataToDb(String projectDataDir) throws IOException {
    String[] collectionNames = new String[]{"artifacts.files", "metadata"};
    for (String collectionName : collectionNames) {
      String collectionDir = String
          .format("/integrationTest/%s/%s", projectDataDir, collectionName);
      File[] collectionFiles = new File(getClass().getResource(collectionDir).getFile())
          .listFiles();
      if (collectionFiles != null) {
        for (File file : collectionFiles) {
          String json = FileUtils.readFileToString(file, "UTF-8");
          client.getDatabase(MOCKED_DB_NAME).getCollection(collectionName)
              .insertOne(Document.parse(json));
        }
      }
    }
  }

  private MongoCollection<Document> getMetadataDocs() {
    return client.getDatabase(MOCKED_DB_NAME).getCollection("metadata");
  }

  private MongoCollection<Document> getArtifactDocs(){
    return client.getDatabase(MOCKED_DB_NAME).getCollection("artifacts.files");
  }
}