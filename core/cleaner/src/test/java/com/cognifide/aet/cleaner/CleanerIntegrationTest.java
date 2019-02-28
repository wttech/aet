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

import static org.mockito.Mockito.when;

import com.cognifide.aet.cleaner.processors.FetchAllProjectSuitesProcessor;
import com.cognifide.aet.cleaner.processors.GetMetadataArtifactsProcessor;
import com.cognifide.aet.cleaner.processors.RemoveArtifactsProcessor;
import com.cognifide.aet.cleaner.processors.RemoveMetadataProcessor;
import com.cognifide.aet.cleaner.processors.StartMetadataCleanupProcessor;
import com.cognifide.aet.cleaner.processors.SuitesRemovePredicateProcessor;
import com.cognifide.aet.cleaner.route.MetadataCleanerRouteBuilder;
import com.cognifide.aet.vs.artifacts.ArtifactsDAOMongoDBImpl;
import com.cognifide.aet.vs.metadata.MetadataDAOMongoDBImpl;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.commons.io.IOUtils;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RunWith(MockitoJUnitRunner.class)
public class CleanerIntegrationTest {

  @Rule
  public final OsgiContext context = new OsgiContext();

  @Mock
  JobExecutionContext jobExecutionContext;
  @Mock
  JobDetail jobDetail;

  private MongoClient client;
  private MongoServer server;
  private String mongoURI;
  private MetadataCleanerRouteBuilder metadataCleanerRouteBuilder;

  @Before
  public void setUp() {
    server = new MongoServer(new MemoryBackend());
    InetSocketAddress address = server.bind();
    mongoURI = String.format("mongodb://localhost:%d", address.getPort());
    client = new MongoClient(new MongoClientURI(mongoURI));

    context.registerInjectActivateService(new MongoDBClient(), "mongoURI", mongoURI);
    context.registerInjectActivateService(new MetadataDAOMongoDBImpl());
    context.registerInjectActivateService(new ArtifactsDAOMongoDBImpl());

    context.registerInjectActivateService(new StartMetadataCleanupProcessor());
    context.registerInjectActivateService(new FetchAllProjectSuitesProcessor());
    context.registerInjectActivateService(new SuitesRemovePredicateProcessor());
    context.registerInjectActivateService(new RemoveMetadataProcessor());
    context.registerInjectActivateService(new GetMetadataArtifactsProcessor());
    context.registerInjectActivateService(new RemoveArtifactsProcessor());
    metadataCleanerRouteBuilder = context
        .registerInjectActivateService(new MetadataCleanerRouteBuilder());
  }

  @Test
  public void test() throws JobExecutionException, IOException {
    setUpJobData(1L, 1L, "companyTest", "projectTest");
    insertDataToDb("companyTest","projectTest");
    new CleanerJob().execute(jobExecutionContext);
  }

  @After
  public void tearDown() {
    client.close();
    server.shutdown();
  }

  private void setUpJobData(Long versionsToKeep, Long maxAge, String companyName,
      String projectName) {
    final JobDataMap jobData = new JobDataMap(ImmutableMap.<String, Object>builder()
        .put(CleanerJob.KEY_ROUTE_BUILDER, metadataCleanerRouteBuilder)
        .put(CleanerJob.KEY_KEEP_N_VERSIONS, versionsToKeep)
        .put(CleanerJob.KEY_REMOVE_OLDER_THAN, maxAge)
        .put(CleanerJob.KEY_COMPANY_FILTER, companyName)
        .put(CleanerJob.KEY_PROJECT_FILTER, projectName)
        .put(CleanerJob.KEY_DRY_RUN, false)
        .build());
    when(jobDetail.getJobDataMap()).thenReturn(jobData);
    when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
  }

  private void insertDataToDb(String companyName, String projectName) throws IOException {
    String dbName = String.format("%s_%s", companyName, projectName);
    String json = IOUtils.toString(this.getClass().getResource("/integrationTest/projectA/metadata/test1.json"), "UTF-8");
    client.getDatabase(dbName).getCollection("metadata").insertOne(Document.parse(json));
  }
}