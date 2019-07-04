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
import static org.junit.Assert.assertTrue;
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
import com.mongodb.BasicDBObject;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.bson.types.ObjectId;
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

  private static final Long MOCKED_CURRENT_TIMESTAMP = 1551428149000L;  //March 1, 2019 8:15:49 AM
  private static final Long CAMEL_CONTEXT_STOP_TIMEOUT = 1L;
  private static final String MOCKED_COMPANY_NAME = "company";
  private static final String MOCKED_PROJECT_NAME = "project";
  private static final String DATA_DIR = "/integrationTest";
  private static final String METADATA_COL_NAME = "metadata";
  private static final String ARTIFACTS_COL_NAME = "artifacts.files";
  private static final String MOCKED_DB_NAME = String
      .format("%s_%s", MOCKED_COMPANY_NAME, MOCKED_PROJECT_NAME);

  @Rule
  public final OsgiContext context = new OsgiContext();

  private JobExecutionContext jobExecutionContext = Mockito.mock(JobExecutionContext.class);
  private JobDetail jobDetail = Mockito.mock(JobDetail.class);

  private InMemoryDB db;

  private MetadataCleanerRouteBuilder metadataCleanerRouteBuilder;
  private LocalDateTimeProvider mockedDateTimeProvider = zone -> LocalDateTime
      .ofInstant(Instant.ofEpochMilli(MOCKED_CURRENT_TIMESTAMP), zone);

  @Before
  public void setUp() {
    db = new InMemoryDB(MOCKED_DB_NAME, DATA_DIR, METADATA_COL_NAME, ARTIFACTS_COL_NAME);

    context.registerInjectActivateService(new MongoDBClient(), "mongoURI", db.getURI());
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

  @After
  public void tearDown() {
    db.shutdown();
  }

  @TestWith({
      //keeping based on age
      "1,6,projectA",
      "1,6,projectB",
      //keeping based on versions
      "5,0,projectA",
      "5,0,projectB",
      //both
      "5,6,projectA",
      "5,6,projectB"
  })
  public void clean_whenNoSuiteMatchesRemoveCondition_keepEverything(Long versionsToKeep,
      Long maxAge,
      String projectDataDir) throws JobExecutionException, IOException {
    setUpDataForTest(versionsToKeep, maxAge, projectDataDir);
    long metadataCountBefore = db.getMetadataDocsCount();
    long artifactCountBefore = db.getArtifactsDocsCount();
    new CleanerJob().execute(jobExecutionContext);
    assertEquals(metadataCountBefore, db.getMetadataDocsCount());
    assertEquals(artifactCountBefore, db.getArtifactsDocsCount());
  }

  @TestWith({
      "1,0,projectA,2",
      "1,1,projectA,2",
      "1,0,projectB,3",
      "1,1,projectB,3"
  })
  public void clean_whenAllSuitesTooOld_keepOnlyNewestSuite(Long versionsToKeep, Long maxAge,
      String projectDataDir, int expectedArtifactsLeft) throws JobExecutionException, IOException {
    setUpDataForTest(versionsToKeep, maxAge, projectDataDir);
    new CleanerJob().execute(jobExecutionContext);
    assertEquals(1, db.getMetadataDocsCount());
    assertEquals(expectedArtifactsLeft, db.getArtifactsDocsCount());
  }

  @TestWith({
      "1,0,projectA,5c7e2d446798f408cf3d1df6;5c7e2d436798f408cf3d1dea",
      "2,0,projectA,5c7e2d446798f408cf3d1df6;5c7e2d436798f408cf3d1dea;5c7e291f6798f408cf3d1da7",
      "1,0,projectB,5c7e349e6798f408cf3d1e10;5c7e35236798f408cf3d1e19;5c7e35236798f408cf3d1e1b",
      "2,0,projectB,5c7e349e6798f408cf3d1e10;5c7e35236798f408cf3d1e19;5c7e35236798f408cf3d1e1b;5c7e34c86798f408cf3d1e13"
  })
  public void clean_whenRemoveSomeSuites_keepOnlyReferencedArtifacts(Long versionsToKeep,
      Long maxAge, String projectDataDir, String artifactsToKeep)
      throws IOException, JobExecutionException {
    String[] artifactsToKeepIds = artifactsToKeep.split(";");
    setUpDataForTest(versionsToKeep, maxAge, projectDataDir);
    new CleanerJob().execute(jobExecutionContext);
    Arrays.stream(artifactsToKeepIds).forEach(artifactId -> {
      BasicDBObject query = new BasicDBObject();
      query.put("_id", new ObjectId(artifactId));
      assertTrue(db.getArtifactDocs().find(query).iterator().hasNext());
    });
    assertEquals(artifactsToKeepIds.length, db.getArtifactDocs().countDocuments());
  }

  private void setUpDataForTest(Long versionsToKeep, Long maxAge, String projectDataDir)
      throws IOException {
    createJobData(versionsToKeep, maxAge);
    db.loadProjectData(projectDataDir);
  }

  private void createJobData(Long versionsToKeep, Long maxAge) {
    final JobDataMap jobData = new JobDataMap(ImmutableMap.<String, Object>builder()
        .put(CleanerJob.KEY_ROUTE_BUILDER, metadataCleanerRouteBuilder)
        .put(CleanerJob.KEY_KEEP_N_VERSIONS, versionsToKeep)
        .put(CleanerJob.KEY_REMOVE_OLDER_THAN, maxAge)
        .put(CleanerJob.KEY_COMPANY_FILTER, MOCKED_COMPANY_NAME)
        .put(CleanerJob.KEY_PROJECT_FILTER, MOCKED_PROJECT_NAME)
        .put(CleanerJob.KEY_DRY_RUN, false)
        .put(CleanerJob.KEY_CAMEL_CONTEXT_CREATOR, new TimeoutCamelContextCreator(CAMEL_CONTEXT_STOP_TIMEOUT))
        .build());
    when(jobDetail.getJobDataMap()).thenReturn(jobData);
    when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
  }
}