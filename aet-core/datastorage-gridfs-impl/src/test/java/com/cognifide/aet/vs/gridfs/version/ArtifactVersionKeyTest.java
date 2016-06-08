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

import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * ArtifactVersionKeyTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 26.02.15
 */
public class ArtifactVersionKeyTest {

	private NodeMetadata nodeMetadata;

	@Before
	public void setUp() throws Exception {
		nodeMetadata = mockNodeMetadata("cognifide", "firefox", "project-name", "t-s-name", "DATA");
	}

	@Test
	public void testToMapWithoutDomainThenExpectNoDomainInMap() throws Exception {
		ArtifactVersionKey<NodeMetadata> artifactVersionKey = new ArtifactVersionKey<>(nodeMetadata);
		Map<String, Object> artifactMap = artifactVersionKey.toMap();

		assertThat(artifactMap.size(), is(4));
		assertNull(artifactMap.get(NodeMetadata.DOMAIN_ATTRIBUTE_NAME));
	}

	@Test
	public void testToMapWithDomainThenExpectDomainInMap() throws Exception {
		when(nodeMetadata.getDomain()).thenReturn("http://cognifide.com");
		ArtifactVersionKey<NodeMetadata> artifactVersionKey = new ArtifactVersionKey<>(nodeMetadata);
		Map<String, Object> artifactMap = artifactVersionKey.toMap();

		assertThat(artifactMap.size(), is(5));
		assertNotNull(artifactMap.get(NodeMetadata.DOMAIN_ATTRIBUTE_NAME));
	}

	@Test
	public void testEqualsWithTheSameObjectThenExpectTrue() throws Exception {
		ArtifactVersionKey<NodeMetadata> artifactVersionKey = new ArtifactVersionKey<>(nodeMetadata);
		assertTrue(artifactVersionKey.equals(artifactVersionKey));
	}

	@Test
	public void testEqualsWithEqualObjectThenExpectTrue() throws Exception {
		assertTrue(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(nodeMetadata)));
	}

	@Test
	public void testEqualsWithOtherObjectThenExpectFalse() throws Exception {
		assertFalse(new ArtifactVersionKey<>(nodeMetadata).equals("xxxxxx"));
	}

	@Test
	public void testEqualsWhenOnlyArtifactTypeDiffersThenExpectTrue() throws Exception {
		NodeMetadata mockedMetadata = mockNodeMetadata("cognifide", "firefox", "project-name", "t-s-name",
				"PATTERNS");
		when(mockedMetadata.getVersion()).thenReturn(5L);
		assertTrue(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyVersionDiffersThenExpectTrue() throws Exception {
		NodeMetadata mockedMetadata = mockNodeMetadata("cognifide", "firefox", "project-name", "t-s-name",
				"DATA");
		when(mockedMetadata.getVersion()).thenReturn(3000L);
		assertTrue(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyCompanyDiffersThenExpectFalse() throws Exception {
		NodeMetadata mockedMetadata = mockNodeMetadata("xxxxxx", "firefox", "project-name", "t-s-name",
				"DATA");
		assertFalse(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyEnvironmentDiffersThenExpectFalse() throws Exception {
		NodeMetadata mockedMetadata = mockNodeMetadata("cognifide", "xxxxxx", "project-name", "t-s-name",
				"DATA");
		assertFalse(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyProjectDiffersThenExpectFalse() throws Exception {
		NodeMetadata mockedMetadata = mockNodeMetadata("cognifide", "firefox", "xxxxxx", "t-s-name", "DATA");
		assertFalse(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlySuiteNameDiffersThenExpectFalse() throws Exception {
		NodeMetadata mockedMetadata = mockNodeMetadata("cognifide", "firefox", "project-name", "xxxxxx",
				"DATA");
		assertFalse(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyDomainDiffersThenExpectFalse() throws Exception {
		NodeMetadata mockedMetadata = mockNodeMetadata("cognifide", "firefox", "project-name", "t-s-name",
				"DATA");
		when(nodeMetadata.getDomain()).thenReturn("http://cognifide.com");
		when(mockedMetadata.getDomain()).thenReturn("http://aet-rulezzzz.com");
		assertFalse(new ArtifactVersionKey<>(nodeMetadata).equals(new ArtifactVersionKey<>(mockedMetadata)));
	}

	@Test
	public void testFromDBObjectContainsAllFields() throws Exception {
		DBObject dbObject = ArtifactsVersionManagerTest.mockMetadataObject();
		ArtifactVersionKey<NodeMetadata> artifactVersionKey = ArtifactVersionKey.fromDBObject(dbObject);
		Map<String, Object> objectMap = artifactVersionKey.toMap();
		assertThat(objectMap.size(), is(5));
		assertThat((String) objectMap.get(NodeMetadata.COMPANY_ATTRIBUTE_NAME), is("cognifide"));
		assertThat((String) objectMap.get(NodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME), is("firefox"));
		assertThat((String) objectMap.get(NodeMetadata.PROJECT_ATTRIBUTE_NAME), is("project-name"));
		assertThat((String) objectMap.get(NodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME), is("t-s-name"));
		assertThat((String) objectMap.get(NodeMetadata.DOMAIN_ATTRIBUTE_NAME), is("http://cognifide.com"));
	}

	private NodeMetadata mockNodeMetadata(String company, String projectName, String environment,
			String testSuiteName, String artifactType) {
		NodeMetadata mockedMetadata = Mockito.mock(NodeMetadata.class);
		when(mockedMetadata.getCompany()).thenReturn(company);
		when(mockedMetadata.getEnvironment()).thenReturn(projectName);
		when(mockedMetadata.getProject()).thenReturn(environment);
		when(mockedMetadata.getTestSuiteName()).thenReturn(testSuiteName);
		when(mockedMetadata.getArtifactType()).thenReturn(artifactType);
		return mockedMetadata;
	}
}
