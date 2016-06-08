/*
 * Cognifide AET :: Version Storage
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
package com.cognifide.aet.vs.cleaner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.vs.VersionStorage;
import com.google.common.collect.Lists;

/**
 * CleanerJobTest
 *
 * @Author: Maciej Laskowski
 * @Date: 12.03.15
 */
@RunWith(MockitoJUnitRunner.class)
public class CleanerJobTest {

	private CleanerJob tested;

	@Mock
	private VersionStorage storage;

	@Mock
	private OperationStatus operationStatus;

	@Before
	public void setUp() throws Exception {
		tested = new CleanerJob();
		tested.setStorage(storage);
		tested.setRemoveOlderThan(5);
		tested.setKeepNVersions(5);
		tested.setDryRun(false);

		when(storage.removePatterns(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean())).thenReturn(operationStatus);
		when(storage.removeReports(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean())).thenReturn(operationStatus);
		when(storage.removeBasicArtifacts(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anySet(), anyBoolean())).thenReturn(operationStatus);
	}


	@Test
	public void execute_whenProjectAndCompanyAreWildcardsAndReportsArtifactType_expectRemovedAllReportsAndNoPatternsAndNoBasicArtifactsRemoved() throws Exception {
		tested.setCompanyName("*");
		tested.setProjectName("*");
		tested.setArtifactTypes("REPORTS");
		when(storage.getCompanies(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("company1", "company2", "company3"));
		when(storage.getProjects(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("p1", "p2"));

		tested.execute(null);

		verify(storage, times(0)).removeBasicArtifacts(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anySet(), anyBoolean());
		verify(storage, times(6)).removeReports(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
		verify(storage, times(0)).removePatterns(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
	}

	@Test
	public void execute_whenProjectAndCompanyAreWildcardsAndPatternsArtifactType_expectRemovedAllPatternsAndNoReportsAndNoBasicArtifactsRemoved() throws Exception {
		tested.setCompanyName("*");
		tested.setProjectName("*");
		tested.setArtifactTypes("PATTERNS");
		when(storage.getCompanies(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("company1", "company2", "company3"));
		when(storage.getProjects(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("p1", "p2"));

		tested.execute(null);

		verify(storage, times(0)).removeBasicArtifacts(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anySet(), anyBoolean());
		verify(storage, times(0)).removeReports(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
		verify(storage, times(6)).removePatterns(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
	}

	@Test
	public void execute_whenProjectAndCompanyAreWildcardsAndNotPatternTypes_expectRemovedBasicArtifactsAndNoReportsAndNoPatternsRemoved() throws Exception {
		tested.setCompanyName("*");
		tested.setProjectName("*");
		tested.setArtifactTypes("DATA");
		when(storage.getCompanies(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("company1", "company2", "company3"));
		when(storage.getProjects(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("p1", "p2"));

		tested.execute(null);

		verify(storage, times(6)).removeBasicArtifacts(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anySet(), anyBoolean());
		verify(storage, times(0)).removeReports(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
		verify(storage, times(0)).removePatterns(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
	}

	@Test
	public void execute_whenProjectAndCompanyAreWildcardsAndAllPatternTypes_expectRemovedBasicArtifactsAndReportsAndPatternsRemoved() throws Exception {
		tested.setCompanyName("*");
		tested.setProjectName("*");
		tested.setArtifactTypes("DATA,RESULTS,REPORTS,PATTERNS");
		when(storage.getCompanies(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("company1", "company2", "company3"));
		when(storage.getProjects(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("p1", "p2"));

		tested.execute(null);

		verify(storage, times(6)).removeBasicArtifacts(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anySet(), anyBoolean());
		verify(storage, times(6)).removeReports(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
		verify(storage, times(6)).removePatterns(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
	}

	@Test
	public void execute_whenCompanySetAndProjectIsWildcard_expectOnlyCompanyArtifactsRemoved() throws Exception {
		tested.setCompanyName("company1");
		tested.setProjectName("*");
		tested.setArtifactTypes("DATA,RESULTS,REPORTS,PATTERNS");
		when(storage.getProjects(Matchers.<NodeMetadata>any())).thenReturn(Lists.newArrayList("p1", "p2"));

		tested.execute(null);

		verify(storage, times(0)).getCompanies(Matchers.<NodeMetadata>any());
		verify(storage, times(2)).removeBasicArtifacts(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anySet(), anyBoolean());
		verify(storage, times(2)).removeReports(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
		verify(storage, times(2)).removePatterns(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
	}

	@Test
	public void execute_whenCompanyAndProjectAreSet_expectOnlyProjectArtifactsRemoved() throws Exception {
		tested.setCompanyName("company1");
		tested.setProjectName("p1");
		tested.setArtifactTypes("DATA,RESULTS,REPORTS,PATTERNS");

		tested.execute(null);

		verify(storage, times(0)).getCompanies(Matchers.<NodeMetadata>any());
		verify(storage, times(0)).getProjects(Matchers.<NodeMetadata>any());
		verify(storage, times(1)).removeBasicArtifacts(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anySet(), anyBoolean());
		verify(storage, times(1)).removeReports(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
		verify(storage, times(1)).removePatterns(Matchers.<NodeMetadata>any(), Matchers.<Date>any(), anyInt(), anyBoolean());
	}
}
