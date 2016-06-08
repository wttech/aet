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
package com.cognifide.aet.vs.visitors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;

/**
 * MetadataArtifactQueryParametersVisitorTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 20.02.15
 */
public class MetadataArtifactQueryParametersVisitorTest {

	@Test
	public void testGetQueryParametersWhenNoValuesThenExpectEmptyMap() throws Exception {
		NodeMetadata nodeMetadata = new NodeMetadata(null, null, null, null, null);
		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		nodeMetadata.accept(visitor);
		assertTrue(visitor.getQueryParameters().isEmpty());
	}

	@Test
	public void testGetQueryParametersWhenOnlyOneValuePresentThenExpectMapWithOneValue() throws Exception {
		NodeMetadata nodeMetadata = new NodeMetadata("cognifide", null, null, null, null);
		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		nodeMetadata.accept(visitor);
		Map<String, Object> queryParameters = visitor.getQueryParameters();
		assertThat(queryParameters.size(), is(1));
		assertTrue(queryParameters.containsKey("company"));
		assertThat((String) queryParameters.get("company"), is("cognifide"));
	}

	@Test
	public void testGetQueryParametersWithAllNodeMetadataValuesFilled() throws Exception {
		NodeMetadata nodeMetadata = new NodeMetadata("cognifide", "test-project", "name-of-suite", "firefox",
				ArtifactType.DATA);
		nodeMetadata.setDomain("http://cognifide.com");
		nodeMetadata.setVersion(5l);
		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		nodeMetadata.accept(visitor);
		Map<String, Object> queryParameters = visitor.getQueryParameters();

		assertThat(queryParameters.size(), is(7));
		checkNodeMetadataValues(queryParameters);
		assertThat((String) queryParameters.get("artifactType"), is("DATA"));
		assertThat((String) queryParameters.get("domain"), is("http://cognifide.com"));
	}

	@Test
	public void testGetQueryParametersWhenAllCollectMetadataFilledThenExpectFilledMap()
			throws Exception {
		UrlNodeMetadata urlNodeMetadata = new UrlNodeMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", ArtifactType.DATA, "company-name-project-name-t-s-name-1234567891011", "test-name", "/info", "infoUrlName", "collector",
				"nameOfCollector");
		urlNodeMetadata.setDomain("http://cognifide.com");
		urlNodeMetadata.setVersion(5l);

		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		urlNodeMetadata.accept(visitor);
		Map<String, Object> queryParameters = visitor.getQueryParameters();

		assertThat(queryParameters.size(), is(13));
		checkUrlNodeMetadataValues(queryParameters);
		assertThat((String) queryParameters.get("artifactType"), is("DATA"));
		assertThat((String) queryParameters.get("domain"), is("http://cognifide.com"));
	}

	@Test
	public void testGetQueryParametersWhenAllUrlCollectMetadataFilledThenExpectFilledMap() throws Exception {
		CollectMetadata collectMetadata = new CollectMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "company-name-project-name-t-s-name-1234567891011", "test-name", "/info", "infoUrlName", "collector", "nameOfCollector");
		collectMetadata.setDomain("http://cognifide.com");
		collectMetadata.setVersion(5l);
		collectMetadata.setDescription("blablabla");

		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		collectMetadata.accept(visitor);
		Map<String, Object> queryParameters = visitor.getQueryParameters();

		assertThat(queryParameters.size(), is(14));
		checkCollectMetadataValues(queryParameters);
		assertThat((String) queryParameters.get("domain"), is("http://cognifide.com"));
		assertThat((String) queryParameters.get("artifactType"), is("DATA"));
	}

	@Test
	public void testGetQueryParametersWhenAllCompareMetadataParametersFilledThenExpectFilledMap()
			throws Exception {
		CompareMetadata compareMetadata = new CompareMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "company-name-project-name-t-s-name-1234567891011", "test-name", "/info", "infoUrlName", "collector", "nameOfCollector",
				"comparator", "comparatorName");
		compareMetadata.setDomain("http://cognifide.com");
		compareMetadata.setVersion(5l);
		compareMetadata.setDescription("blablabla");

		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		compareMetadata.accept(visitor);
		Map<String, Object> queryParameters = visitor.getQueryParameters();

		assertThat(queryParameters.size(), is(16));
		checkCollectMetadataValues(queryParameters);
		assertThat((String) queryParameters.get("artifactType"), is("RESULTS"));
		assertThat((String) queryParameters.get("domain"), is("http://cognifide.com"));
		assertThat((String) queryParameters.get("comparatorModule"), is("comparator"));
		assertThat((String) queryParameters.get("comparatorModuleName"), is("comparatorName"));
	}

	@Test
	public void testGetQueryParametersWhenAllPatternMetadataParametersFilledThenExpectFilledMap()
			throws Exception {
		PatternMetadata patternMetadata = new PatternMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "test-name", "infoUrlName", "collector", "nameOfCollector", "rebasecompany-name-project-name-t-s-name-1234567891011");
		patternMetadata.setDomain("http://cognifide.com");
		patternMetadata.setVersion(5l);
		patternMetadata.setDescription("blablabla");

		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		patternMetadata.accept(visitor);
		Map<String, Object> queryParameters = visitor.getQueryParameters();

		assertThat(queryParameters.size(), is(11));
		assertThat((String) queryParameters.get("company"), is("cognifide"));
		assertThat((String) queryParameters.get("project"), is("test-project"));
		assertThat((String) queryParameters.get("testSuiteName"), is("name-of-suite"));
		assertThat((String) queryParameters.get("environment"), is("firefox"));
		assertThat((Long) queryParameters.get("version"), is(5l));
		assertThat((String) queryParameters.get("collectorModule"), is("collector"));
		assertThat((String) queryParameters.get("collectorModuleName"), is("nameOfCollector"));
		assertThat((String) queryParameters.get("urlName"), is("infoUrlName"));
		assertThat((String) queryParameters.get("testName"), is("test-name"));
		assertThat((String) queryParameters.get("artifactType"), is("PATTERNS"));
		assertThat((String) queryParameters.get("description"), is("blablabla"));
	}

	@Test
	public void testGetQueryParametersWhenAllReportMetadataParametersFilledThenExpectFilledMap()
			throws Exception {
		ReportMetadata reportMetadata = new ReportMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "company-name-project-name-t-s-name-1234567891011", "reporter");
		reportMetadata.setVersion(5l);

		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		reportMetadata.accept(visitor);
		Map<String, Object> queryParameters = visitor.getQueryParameters();

		assertThat(queryParameters.size(), is(8));
		checkNodeMetadataValues(queryParameters);
		assertThat((String) queryParameters.get("artifactType"), is("REPORTS"));
		assertThat((String) queryParameters.get("reporterModule"), is("reporter"));
		assertThat((String) queryParameters.get("correlationId"), is("company-name-project-name-t-s-name-1234567891011"));
	}

	private void checkNodeMetadataValues(Map<String, Object> queryParameters) {
		assertThat((String) queryParameters.get("company"), is("cognifide"));
		assertThat((String) queryParameters.get("project"), is("test-project"));
		assertThat((String) queryParameters.get("testSuiteName"), is("name-of-suite"));
		assertThat((String) queryParameters.get("environment"), is("firefox"));
		assertThat((Long) queryParameters.get("version"), is(5l));
	}

	private void checkUrlNodeMetadataValues(Map<String, Object> queryParameters) {
		checkNodeMetadataValues(queryParameters);
		assertThat((String) queryParameters.get("collectorModule"), is("collector"));
		assertThat((String) queryParameters.get("collectorModuleName"), is("nameOfCollector"));
		assertThat((String) queryParameters.get("correlationId"), is("company-name-project-name-t-s-name-1234567891011"));
		assertThat((String) queryParameters.get("url"), is("/info"));
		assertThat((String) queryParameters.get("urlName"), is("infoUrlName"));
		assertThat((String) queryParameters.get("testName"), is("test-name"));
	}

	private void checkCollectMetadataValues(Map<String, Object> queryParameters) {
		checkUrlNodeMetadataValues(queryParameters);
		assertThat((String) queryParameters.get("description"), is("blablabla"));
	}

}
