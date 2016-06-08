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

import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertFalse;
import static org.mockito.Mockito.when;

/**
 * ArtifactsVersionManagerTest
 *
 * @Author: Maciej Laskowski
 * @Date: 25.02.15
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class ArtifactsVersionManagerTest<T extends ArtifactsVersionManager> {

	@Mock
	protected GridFS gridFS;

	@Mock
	protected GridFSDBFile file;

	protected DBObject metadataObject;

	@Before
	public void setUp() throws Exception {
		metadataObject = mockMetadataObject();
		when(file.getMetaData()).thenReturn(metadataObject);
	}

	protected static DBObject mockMetadataObject() {
		DBObject metadataObject = Mockito.mock(DBObject.class);
		when(metadataObject.get(NodeMetadata.COMPANY_ATTRIBUTE_NAME)).thenReturn("cognifide");
		when(metadataObject.get(NodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME)).thenReturn("firefox");
		when(metadataObject.get(NodeMetadata.DOMAIN_ATTRIBUTE_NAME)).thenReturn("http://cognifide.com");
		when(metadataObject.get(NodeMetadata.PROJECT_ATTRIBUTE_NAME)).thenReturn("project-name");
		when(metadataObject.get(NodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME)).thenReturn("t-s-name");
		when(metadataObject.get(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME)).thenReturn("DATA");
		when(metadataObject.get(UrlNodeMetadata.TEST_NAME_ATTRIBUTE_NAME)).thenReturn("test-name");
		when(metadataObject.get(UrlNodeMetadata.URL_NAME_ATTRIBUTE_NAME)).thenReturn("url-name");
		when(metadataObject.get(CollectMetadata.COLLECTOR_ATTRIBUTE_TYPE)).thenReturn("collector");
		when(metadataObject.get(CollectMetadata.COLLECTOR_ATTRIBUTE_NAME)).thenReturn("collectorName");
		when(metadataObject.get(NodeMetadata.VERSION_ATTRIBUTE_NAME)).thenReturn(5L);
		return metadataObject;
	}


	@Test
	public void testRemoveArtifactsWithNullParametersThenExpectFailedOperationStatus() throws Exception {
		T versionManager = getVersionManagerInstance();
		OperationStatus operationStatus = versionManager.removeArtifacts(false, null, null);
		assertFalse(operationStatus.isSuccess());
	}

	@Test
	public void testRemoveArtifactsWithInvalidVersionsToKeepThenExpectFailedOperationStatus()
			throws Exception {
		T versionManager = getVersionManagerInstance();
		OperationStatus operationStatus = versionManager.removeArtifacts(false, null, -1);
		assertFalse(operationStatus.isSuccess());
	}

	protected abstract T getVersionManagerInstance();

}
