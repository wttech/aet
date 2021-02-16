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

import com.cognifide.aet.cleaner.processors.*;
import com.cognifide.aet.cleaner.route.OrphanCleanerRouteBuilder;
import com.cognifide.aet.cleaner.time.LocalDateTimeProvider;
import com.cognifide.aet.rest.LockService;
import com.cognifide.aet.vs.artifacts.ArtifactsDAOMongoDBImpl;
import com.cognifide.aet.vs.metadata.MetadataDAOMongoDBImpl;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import static org.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(ZohhakRunner.class)
public class OrphanCleanerIntegrationTest {


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

    private OrphanCleanerRouteBuilder orphanCleanerRouteBuilder;
    private LockService lockService;
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
        context.registerInjectActivateService(new FetchProjectMetadataProcessor());
        context.registerInjectActivateService(new SuiteSplitterProcessor());
        context.registerInjectActivateService(new RemoveArtifactsProcessor());
        context.registerInjectActivateService(new GetMetadataArtifactsProcessor());
        context.registerInjectActivateService(new GetAllOrphanedArtifactsProcessor());
        lockService = context.registerInjectActivateService(new LockService());
        context.registerInjectActivateService(new SuiteLockProcessor());
        context.registerInjectActivateService(new SuiteUnlockProcessor());
        orphanCleanerRouteBuilder = context
                .registerInjectActivateService(new OrphanCleanerRouteBuilder());
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @TestWith({
            "projectA",
            "projectB"
    })
    public void clean_whenAllArtifactsHaveParents_keepEverything(String projectDataDir)
            throws JobExecutionException, IOException {
        setUpDataForTest(projectDataDir);

        long metadataCountBefore = db.getMetadataDocsCount();
        long artifactCountBefore = db.getArtifactsDocsCount();

        new CleanerJob().execute(jobExecutionContext);

        assertEquals(metadataCountBefore, db.getMetadataDocsCount());
        assertEquals(artifactCountBefore, db.getArtifactsDocsCount());
    }

    @TestWith({
            "projectC,3"
    })
    public void clean_someArtifactsAreOrphan_keepOnlyWithParent(String projectDataDir, int expectedArtifactsLeft)
            throws JobExecutionException, IOException {
        setUpDataForTest(projectDataDir);

        long metadataCountBefore = db.getMetadataDocsCount();

        new CleanerJob().execute(jobExecutionContext);

        assertEquals(metadataCountBefore, db.getMetadataDocsCount());
        assertEquals(expectedArtifactsLeft, db.getArtifactsDocsCount());
    }

    @TestWith({
            "projectC,5c7e291f6798f408cf3d1da7;5c7e2d436798f408cf3d1dea;5c7e2d446798f408cf3d1df6,5c7e2d436798f408cf3d1ded"
    })
    public void clean_removeOrphanedArtifacts_keepOnlyReferencedArtifacts(String projectDataDir, String artifactsToKeep,
                                                                          String artifactToRemove)
            throws IOException, JobExecutionException {
        Set<String> artifactsToKeepIds = Sets.newHashSet(artifactsToKeep.split(";"));
        Set<String> artifactsToRemoveIds = Sets.newHashSet(artifactToRemove.split(";"));

        setUpDataForTest(projectDataDir);

        Set<String> allArtifacts = db.getArtifactsIds();
        new CleanerJob().execute(jobExecutionContext);
        Set<String> artifactsAfterCleaning = db.getArtifactsIds();

        Set<String> removedArtifacts = Sets.difference(allArtifacts, artifactsAfterCleaning);

        assertEquals(removedArtifacts, artifactsToRemoveIds);
        assertEquals(artifactsAfterCleaning, artifactsToKeepIds);
    }

    @TestWith({
            "projectC,3"
    })
    public void clean_startCleanerWhenTestIsRunning_waitUntilTestStops(String projectDataDir, long expectedArtifactsLeft)
            throws Exception{
        lockService.acquireSlot();

        setUpDataForTest(projectDataDir);
        TestRunner testRunner = new TestRunner();
        Thread thread = new Thread(testRunner);
        thread.start();

        await("Cleaner wait until tests stop").atMost(Duration.FIVE_SECONDS).until(threadQueueSize(), equalTo(1));
        lockService.releaseSlot();
        await("Cleaner start working").atMost(Duration.FIVE_SECONDS).until(threadQueueSize(), equalTo(0));
        with().pollInterval(Duration.TWO_HUNDRED_MILLISECONDS)
                .await("Cleaner stop working").atMost(Duration.FIVE_SECONDS).until(countArtifactsInDatabase(), equalTo(expectedArtifactsLeft));
    }

    private void setUpDataForTest(String projectDataDir)
            throws IOException {
        createJobData();
        db.loadProjectData(projectDataDir);
    }

    private void createJobData() {
        final JobDataMap jobData = new JobDataMap(ImmutableMap.<String, Object>builder()
                .put(CleanerJob.KEY_ROUTE_BUILDER, orphanCleanerRouteBuilder)
                .put(CleanerJob.KEY_KEEP_N_VERSIONS, 0L)
                .put(CleanerJob.KEY_REMOVE_OLDER_THAN, 0L)
                .put(CleanerJob.KEY_COMPANY_FILTER, MOCKED_COMPANY_NAME)
                .put(CleanerJob.KEY_PROJECT_FILTER, MOCKED_PROJECT_NAME)
                .put(CleanerJob.KEY_DRY_RUN, false)
                .put(CleanerJob.KEY_CAMEL_CONTEXT_CREATOR, new TimeoutCamelContextCreator(CAMEL_CONTEXT_STOP_TIMEOUT))
                .build());
        when(jobDetail.getJobDataMap()).thenReturn(jobData);
        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
    }

    private Callable<Long> countArtifactsInDatabase() {
        return () -> db.getArtifactsDocsCount();
    }

    private Callable<Integer> threadQueueSize() {
        return () -> fieldIn(lockService)
                .ofType(Semaphore.class)
                .call()
                .getQueueLength();
    }

    private class TestRunner implements Runnable {

        @Override
        public void run() {
            try {
                new CleanerJob().execute(jobExecutionContext);
            } catch (JobExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
