/*
 * Cognifide AET :: Data Storage GridFs Implementation
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.vs.gridfs.version;

import static com.google.common.testing.GuavaAsserts.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.OperationStatus;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * BasicArtifactsVersionManagerTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 25.02.15
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicArtifactsVersionManagerTest extends
		ArtifactsVersionManagerTest<BasicArtifactsVersionManager> {

	private static final String METADATA_PREFIX = "metadata.";

	@Override
	protected BasicArtifactsVersionManager getVersionManagerInstance() {
		return new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX, Sets.newSet(ArtifactType.DATA, ArtifactType.REPORTS,
				ArtifactType.RESULTS));
	}

	@Test
	public void testRemoveArtifactsWithEmptyArtifactTypesThenExpectFailedOperationStatus() throws Exception {
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX, null);
		OperationStatus operationStatus = versionManager.removeArtifacts(false, new Date(), 1);
		assertFalse(operationStatus.isSuccess());
	}

	@Test
	public void testRemoveArtifactsWithPatternArtifactTypeThenExpectFailedOperationStatus() throws Exception {
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX,
				Sets.newSet(ArtifactType.PATTERNS));
		OperationStatus operationStatus = versionManager.removeArtifacts(false, new Date(), 1);
		assertFalse(operationStatus.isSuccess());
	}

	@Test
	public void verifyRemoveArtifactsPerformedWithProperQuery() throws Exception {
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX,
				Sets.newSet(ArtifactType.DATA));
		versionManager.removeArtifacts(true, null, 1);
		verify(gridFS, times(1)).find(getBasicMockedQuery(1L, null, Collections.singletonList("DATA")));
	}

	@Test
	public void verifyRemoveArtifactsRemoveNotUsedInDryRunMode() throws Exception {
		List<GridFSDBFile> filesList = Collections.singletonList(file);
		Date keepAllAfter = new Date();
		when(
				gridFS.find(getBasicMockedQuery(1L, keepAllAfter,
						Lists.newArrayList("DATA", "REPORTS", "RESULTS")))).thenReturn(filesList);
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX,
				Sets.newSet(ArtifactType.DATA, ArtifactType.REPORTS, ArtifactType.RESULTS));
		OperationStatus operationStatus = versionManager.removeArtifacts(true, keepAllAfter, 1);
		assertTrue(operationStatus.isSuccess());
		verify(gridFS, times(1)).find(Matchers.<DBObject> any());
		verify(gridFS, times(0)).remove(Matchers.<DBObject> any());
	}

	@Test
	public void verifyRemoveArtifactsWithSingleObjectToRemoveFoundAndRemoved() throws Exception {
		List<GridFSDBFile> filesList = Collections.singletonList(file);
		when(gridFS.find(Matchers.<DBObject> any())).thenReturn(filesList);
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX,
				Sets.newSet(ArtifactType.DATA, ArtifactType.REPORTS, ArtifactType.RESULTS));
		OperationStatus operationStatus = versionManager.removeArtifacts(false, null, 1);
		assertTrue(operationStatus.isSuccess());

		verify(gridFS, times(1)).find(
				getBasicMockedQuery(1L, null, Lists.newArrayList("DATA", "REPORTS", "RESULTS")));
		verify(gridFS, times(1)).remove(Matchers.<DBObject> any());
	}

	@Test
	public void verifyRemoveArtifactsWithOneArtifactTypesPerformed() throws Exception {
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX,
				Sets.newSet(ArtifactType.DATA));
		versionManager.removeArtifacts(true, null, 1);

		DBObject basicQuery = getBasicMockedQuery(1L, null, Lists.newArrayList("DATA"));
		verify(gridFS, times(1)).find(basicQuery);
	}

	@Test
	public void verifyRemoveArtifactsWithTwoArtifactTypesPerformed() throws Exception {
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX,
				Sets.newSet(ArtifactType.REPORTS, ArtifactType.RESULTS));
		versionManager.removeArtifacts(true, null, 1);

		DBObject basicQuery = getBasicMockedQuery(1L, null, Lists.newArrayList("REPORTS", "RESULTS"));
		verify(gridFS, times(1)).find(basicQuery);
	}

	@Test
	public void verifyRemoveArtifactsWithAllArtifactTypesPerformed() throws Exception {
		BasicArtifactsVersionManager versionManager = new BasicArtifactsVersionManager(gridFS, METADATA_PREFIX,
				Sets.newSet(ArtifactType.DATA, ArtifactType.REPORTS, ArtifactType.RESULTS));
		versionManager.removeArtifacts(true, null, 1);

		DBObject basicQuery = getBasicMockedQuery(1L, null, Lists.newArrayList("DATA", "REPORTS", "RESULTS"));
		verify(gridFS, times(1)).find(basicQuery);
	}

	private DBObject getBasicMockedQuery(Long version, Date keepAllAfter, List<String> artifactTypes) {
		Map parametersMap = new HashMap();
		parametersMap.put("metadata.artifactType", Collections.singletonMap("$in", artifactTypes));
		if (keepAllAfter != null) {
			parametersMap.put("uploadDate", Collections.singletonMap("$gte", keepAllAfter));
		}
		if (version != null) {
			parametersMap.put("metadata.version", Collections.singletonMap("$gt", version));
		}
		return BasicDBObjectBuilder.start(parametersMap).get();
	}
}
