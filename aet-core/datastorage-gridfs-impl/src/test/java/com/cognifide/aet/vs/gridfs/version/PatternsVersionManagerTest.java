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
import org.mockito.runners.MockitoJUnitRunner;

import com.cognifide.aet.communication.api.OperationStatus;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * PatternsVersionManagerTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 25.02.15
 */
@RunWith(MockitoJUnitRunner.class)
public class PatternsVersionManagerTest extends ArtifactsVersionManagerTest<PatternsVersionManager> {

	private static final String METADATA_PREFIX = "metadata.";

	@Override
	protected PatternsVersionManager getVersionManagerInstance() {
		return new PatternsVersionManager(gridFS, "company");
	}

	@Test
	public void verifyRemoveArtifactsWithOneArtifactTypesPerformed() throws Exception {
		PatternsVersionManager versionManager = new PatternsVersionManager(gridFS, METADATA_PREFIX);
		versionManager.removeArtifacts(true, null, 1);

		DBObject basicQuery = getBasicMockedQuery(1L);
		verify(gridFS, times(1)).find(basicQuery);
	}

	@Test
	public void verifyRemoveArtifactsWithSingleObjectToRemoveFoundAndRemoved() throws Exception {
		List<GridFSDBFile> filesList = Collections.singletonList(file);
		when(gridFS.find(Matchers.<DBObject> any())).thenReturn(filesList);
		PatternsVersionManager versionManager = new PatternsVersionManager(gridFS, METADATA_PREFIX);
		OperationStatus operationStatus = versionManager.removeArtifacts(false, null, 1);
		assertTrue(operationStatus.isSuccess());

		verify(gridFS, times(1)).find(getBasicMockedQuery(1L));
		verify(gridFS, times(1)).remove(Matchers.<DBObject> any());
	}

	@Test
	public void verifyRemoveArtifactsRemoveNotUsedInDryRunMode() throws Exception {
		List<GridFSDBFile> filesList = Collections.singletonList(file);
		when(gridFS.find(Matchers.<DBObject> any())).thenReturn(filesList);
		PatternsVersionManager versionManager = new PatternsVersionManager(gridFS, METADATA_PREFIX);
		OperationStatus operationStatus = versionManager.removeArtifacts(true, new Date(), 1);
		assertTrue(operationStatus.isSuccess());
		verify(gridFS, times(2)).find(Matchers.<DBObject> any());
		verify(gridFS, times(0)).remove(Matchers.<DBObject> any());
	}

	private DBObject getBasicMockedQuery(Long version) {
		Map parametersMap = new HashMap();
		parametersMap.put("metadata.version", Collections.singletonMap("$gt", version));
		parametersMap.put("metadata.artifactType", "PATTERNS");
		parametersMap.put("metadata.currentPattern", true);
		return BasicDBObjectBuilder.start(parametersMap).get();
	}
}
