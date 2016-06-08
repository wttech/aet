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

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * MetadataArtifactPathVisitorTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 20.02.15
 */
public class MetadataArtifactPathVisitorTest {
	private static final String PATH_SEPARATOR = "/";

	@Test
	public void testGetPathForNodeMetadata() throws Exception {
		NodeMetadata nodeMetadata = new NodeMetadata("cognifide", "test-project", "name-of-suite", "firefox",
				ArtifactType.DATA);
		nodeMetadata.setDomain("http://cognifide.com");
		nodeMetadata.setVersion(5l);

		MetadataArtifactPathVisitor visitor = new MetadataArtifactPathVisitor(PATH_SEPARATOR);
		nodeMetadata.accept(visitor);
		assertThat(visitor.getPath(), is("cognifide/test-project/name-of-suite/firefox/"));
	}

	@Test
	public void testGetPathForUrlNodeMetadata() throws Exception {
		UrlNodeMetadata urlNodeMetadata = new UrlNodeMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", ArtifactType.DATA, "company-name-project-name-t-s-name-1234567891011",
				"test-name", "/info", "infoUrlName", "collector", "nameOfCollector");
		urlNodeMetadata.setDomain("http://cognifide.com");
		urlNodeMetadata.setVersion(5l);

		MetadataArtifactPathVisitor visitor = new MetadataArtifactPathVisitor(PATH_SEPARATOR);
		urlNodeMetadata.accept(visitor);
		assertThat(
				visitor.getPath(),
				is("cognifide/test-project/name-of-suite/firefox/test-name/urls/infoUrlName/artifactTypes/data/collector/nameOfCollector/"));
	}

	@Test
	public void testGetPathForCollectMetadata() throws Exception {
		CollectMetadata collectMetadata = new CollectMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "company-name-project-name-t-s-name-1234567891011", "test-name", "/info",
				"infoUrlName", "collector", "nameOfCollector");
		collectMetadata.setDomain("http://cognifide.com");
		collectMetadata.setVersion(5l);
		collectMetadata.setDescription("blablabla");

		MetadataArtifactPathVisitor visitor = new MetadataArtifactPathVisitor(PATH_SEPARATOR);
		collectMetadata.accept(visitor);
		assertThat(
				visitor.getPath(),
				is("cognifide/test-project/name-of-suite/firefox/test-name/urls/infoUrlName/artifactTypes/data/collector/nameOfCollector/company-name-project-name-t-s-name-1234567891011/"));
	}

	@Test
	public void testGetPathForCompareMetadata() throws Exception {
		CompareMetadata compareMetadata = new CompareMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "company-name-project-name-t-s-name-1234567891011", "test-name", "/info",
				"infoUrlName", "collector", "nameOfCollector", "comparator", "comparatorName");
		compareMetadata.setDomain("http://cognifide.com");
		compareMetadata.setVersion(5l);
		compareMetadata.setDescription("blablabla");

		MetadataArtifactPathVisitor visitor = new MetadataArtifactPathVisitor(PATH_SEPARATOR);
		compareMetadata.accept(visitor);
		assertThat(
				visitor.getPath(),
				is("cognifide/test-project/name-of-suite/firefox/test-name/urls/infoUrlName/artifactTypes/results/collector/nameOfCollector/comparator/comparatorName/company-name-project-name-t-s-name-1234567891011/"));
	}

	@Test
	public void testGetPathForPatternMetadata() throws Exception {
		PatternMetadata patternMetadata = new PatternMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "test-name", "infoUrlName", "collector", "nameOfCollector", "rebase12345");
		patternMetadata.setDomain("http://cognifide.com");
		patternMetadata.setVersion(5l);
		patternMetadata.setDescription("blablabla");

		MetadataArtifactPathVisitor visitor = new MetadataArtifactPathVisitor(PATH_SEPARATOR);
		patternMetadata.accept(visitor);
		assertThat(
				visitor.getPath(),
				is("cognifide/test-project/name-of-suite/firefox/test-name/urls/infoUrlName/artifactTypes/patterns/collector/nameOfCollector/"));
	}

	@Test
	public void testGetPathForReportMetadata() throws Exception {
		ReportMetadata reportMetadata = new ReportMetadata("cognifide", "test-project", "name-of-suite",
				"firefox", "company-name-project-name-t-s-name-1234567891011", "reporter");
		reportMetadata.setVersion(5l);

		MetadataArtifactPathVisitor visitor = new MetadataArtifactPathVisitor(PATH_SEPARATOR);
		reportMetadata.accept(visitor);
		assertThat(
				visitor.getPath(),
				is("cognifide/test-project/name-of-suite/firefox/reports/reporter/company-name-project-name-t-s-name-1234567891011/"));
	}
}
