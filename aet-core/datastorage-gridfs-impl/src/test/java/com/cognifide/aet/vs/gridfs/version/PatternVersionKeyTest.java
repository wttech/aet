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

import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * PatternVersionKeyTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 26.02.15
 */
public class PatternVersionKeyTest {

	private PatternMetadata patternMetadata;

	@Before
	public void setUp() throws Exception {
		patternMetadata = mockPatternMetadata("cognifide", "firefox", "project-name", "t-s-name",
				"collector", "collectorName");
	}

	@Test
	public void testEqualsWithTheSameObjectThenExpectTrue() throws Exception {
		PatternVersionKey patternVersionKey = new PatternVersionKey(patternMetadata);
		assertTrue(patternVersionKey.equals(patternVersionKey));
	}

	@Test
	public void testEqualsWithEqualObjectThenExpectTrue() throws Exception {
		assertTrue(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(patternMetadata)));
	}

	@Test
	public void testEqualsWithOtherObjectThenExpectFalse() throws Exception {
		assertFalse(new PatternVersionKey(patternMetadata).equals("xxxxxx"));
	}

	@Test
	public void testEqualsWhenOnlyVersionDiffersThenExpectTrue() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("cognifide", "firefox", "project-name",
				"t-s-name", "collector", "collectorName");
		when(mockedMetadata.getVersion()).thenReturn(3000L);
		assertTrue(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyCompanyDiffersThenExpectFalse() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("xxxxxxxxx", "firefox", "project-name",
				"t-s-name", "collector", "collectorName");
		assertFalse(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyEnvironmentDiffersThenExpectFalse() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("cognifide", "xxxxxxxxx", "project-name",
				"t-s-name", "collector", "collectorName");
		assertFalse(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyProjectDiffersThenExpectFalse() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("cognifide", "firefox", "xxxxxxxxx", "t-s-name",
				"collector", "collectorName");
		assertFalse(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlySuiteNameDiffersThenExpectFalse() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("cognifide", "firefox", "project-name",
				"xxxxxxxxx", "collector", "collectorName");
		assertFalse(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyCollectorDiffersThenExpectFalse() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("cognifide", "firefox", "project-name",
				"t-s-name", "xxxxxxxxx", "collectorName");
		assertFalse(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyCollectorNameDiffersThenExpectFalse() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("cognifide", "firefox", "project-name",
				"t-s-name", "collector", "xxxxxxxxx");
		assertFalse(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testEqualsWhenOnlyArtifactTypeDiffersThenExpectFalse() throws Exception {
		PatternMetadata mockedMetadata = mockPatternMetadata("cognifide", "firefox", "project-name",
				"t-s-name", "collector", "collectorName");
		when(mockedMetadata.getArtifactType()).thenReturn("DATA");
		assertFalse(new PatternVersionKey(patternMetadata).equals(new PatternVersionKey(mockedMetadata)));
	}

	@Test
	public void testPatternFromDBObjectContainsAllFields() throws Exception {
		DBObject dbObject = ArtifactsVersionManagerTest.mockMetadataObject();
		PatternVersionKey patternVersionKey = PatternVersionKey.patternFromDBObject(dbObject);
		Map<String, Object> objectMap = patternVersionKey.toMap();
		assertThat(objectMap.size(), is(10));
		assertThat((String) objectMap.get(NodeMetadata.COMPANY_ATTRIBUTE_NAME), is("cognifide"));
		assertThat((String) objectMap.get(NodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME), is("firefox"));
		assertThat((String) objectMap.get(NodeMetadata.PROJECT_ATTRIBUTE_NAME), is("project-name"));
		assertThat((String) objectMap.get(NodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME), is("t-s-name"));
		assertThat((String) objectMap.get(NodeMetadata.ARTIFACT_TYPE_ATTRIBUTE_NAME), is("PATTERNS"));
		assertThat((String) objectMap.get(UrlNodeMetadata.TEST_NAME_ATTRIBUTE_NAME), is("test-name"));
		assertThat((String) objectMap.get(UrlNodeMetadata.URL_NAME_ATTRIBUTE_NAME), is("url-name"));
		assertThat((String) objectMap.get(CollectMetadata.COLLECTOR_ATTRIBUTE_TYPE), is("collector"));
		assertThat((String) objectMap.get(CollectMetadata.COLLECTOR_ATTRIBUTE_NAME), is("collectorName"));
		assertThat((Boolean) objectMap.get(PatternMetadata.CURRENT_PATTERN), is(false));
	}

	private PatternMetadata mockPatternMetadata(String company, String environment, String projectName,
			String testSuiteName, String collector, String collectorName) {
		PatternMetadata mockedMetadata = Mockito.mock(PatternMetadata.class);
		when(mockedMetadata.getCompany()).thenReturn(company);
		when(mockedMetadata.getEnvironment()).thenReturn(projectName);
		when(mockedMetadata.getProject()).thenReturn(environment);
		when(mockedMetadata.getTestSuiteName()).thenReturn(testSuiteName);
		when(mockedMetadata.getCollectorModule()).thenReturn(collector);
		when(mockedMetadata.getCollectorModuleName()).thenReturn(collectorName);
		when(mockedMetadata.getArtifactType()).thenReturn("PATTERNS");
		when(mockedMetadata.getVersion()).thenReturn(10L);
		return mockedMetadata;
	}

}
