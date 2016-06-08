/*
 * Cognifide AET :: Data Storage API
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
package com.cognifide.aet.vs;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.communication.api.RebaseOperationStatus;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;

/**
 * @author lukasz.wieczorek
 */
public interface VersionStorage {

	Node getDataNode(NodeMetadata nodeMetadata);

	Collection getPatternModuleVersions(NodeMetadata nodeMetadata);

	Node getPatternNode(NodeMetadata nodeMetadata);

	Node getResultNode(NodeMetadata nodeMetadata);

	Node getReportNode(NodeMetadata nodeMetadata);

	Collection getResultsComparatorsTypes(NodeMetadata nodeMetadata);

	Collection getResultsComparatorsNames(NodeMetadata nodeMetadata);

	/**
	 * Removes all basic (DATA, RESULTS) artifact nodes from database which fulfill condition:
	 * <ul>
	 * <li>version is less or equal to difference between current max version and keepNVersions,</li>
	 * <li>artifact was created before keepAllAfter date.</li>
	 * </ul>
	 * 
	 * @param nodeMetadata - contains basic metadata - company and project name
	 * @param keepAllAfter - date after which all artifacts will remain
	 * @param keepNVersions - number of last versions that will remain after cleanup
	 * @param artifactTypes - set of ArtifactType - can be only used with DATA and RESULTS
	 * @param dryRun - flag which says if remove operation should be performed. When set to true, operation
	 * will be logged only.
	 * @return OperationStatus of operation
	 */
	OperationStatus removeBasicArtifacts(NodeMetadata nodeMetadata, Date keepAllAfter,
			Integer keepNVersions, Set<ArtifactType> artifactTypes, Boolean dryRun);


	/**
	 * Removes all reports nodes from database which fulfill condition:
	 * <ul>
	 * <li>version is less or equal to difference between current max version and keepNVersions,</li>
	 * <li>artifact was created before keepAllAfter date.</li>
	 * </ul>
	 *
	 * @param nodeMetadata - contains basic metadata - company and project name
	 * @param keepAllAfter - date after which all artifacts will remain
	 * @param keepNVersions - number of last versions that will remain after cleanup
	 * @param dryRun - flag which says if remove operation should be performed. When set to true, operation
	 * will be logged only.
	 * @return OperationStatus of operation
	 */
	OperationStatus removeReports(NodeMetadata nodeMetadata, Date keepAllAfter, Integer keepNVersions,
			boolean dryRun);
	/**
	 * Sets flag patternCandidate for all matched DATA Artifacts.
	 * 
	 * @param nodeMetadata - identifies DATA Artifacts.
	 */
	void setPatternCandidate(NodeMetadata nodeMetadata);

	RebaseOperationStatus rebasePatterns(NodeMetadata nodeMetadata) throws VersionStorageException;

	Map<String, String> getRebasePatternParameters(PatternMetadata nodeMetadata);

	Collection<String> getCompanies(NodeMetadata nodeMetadata);

	Collection<String> getProjects(NodeMetadata nodeMetadata);

	Collection getTestSuites(NodeMetadata nodeMetadata);

	Collection getEnvironments(NodeMetadata build);

	Collection getTestCases(NodeMetadata nodeMetadata);

	Collection getTestCaseUrls(NodeMetadata nodeMetadata);

	Collection getTypes();

	Collection getDataExecutions(NodeMetadata nodeMetadata);

	Collection getResultsExecutions(NodeMetadata nodeMetadata);

	Collection getPatternsCollectorsTypes(NodeMetadata nodeMetadata);

	Collection getPatternsCollectorsNames(NodeMetadata nodeMetadata);

	Collection getDataCollectorTypes(NodeMetadata nodeMetadata);

	Collection getDataCollectorNames(NodeMetadata nodeMetadata);

	Collection getResultsCollectorsTypes(NodeMetadata nodeMetadata);

	Collection getResultsCollectorsNames(NodeMetadata nodeMetadata);

	Collection getPatternsArtifacts(NodeMetadata nodeMetadata);

	Collection getDataArtifacts(NodeMetadata nodeMetadata);

	InputStream getDataArtifactData(NodeMetadata nodeMetadata, String artifactName);

	InputStream getResultsArtifactData(NodeMetadata nodeMetadata, String artifactName);

	InputStream getPatternsArtifactData(NodeMetadata nodeMetadata, String artifactName);

	Collection getResultsArtifacts(NodeMetadata nodeMetadata);

	InputStream getReportsArtifactData(NodeMetadata nodeMetadata, String artifactName);

	Collection<String> getReportsExecutionArtifacts(NodeMetadata nodeMetadata);

	Collection<String> getReportsExecutions(NodeMetadata nodeMetadata);

	Collection getReportsModules(NodeMetadata nodeMetadata);

	/**
	 * Removes all pattern nodes from database which fulfill condition:
	 * <ul>
	 * <li>version is less or equal to difference between current max version and keepNVersions,</li>
	 * <li>artifact was created before keepAllAfter date.</li>
	 * </ul>
	 * 
	 * @param nodeMetadata - contains basic metadata - company and project name
	 * @param keepAllAfter - date after which all artifacts will remain
	 * @param keepNVersions - number of last versions that will remain after cleanup
	 * @param dryRun - flag which says if remove operation should be performed. When set to true, operation
	 * will be logged only.
	 * @return OperationStatus of operation
	 */
	OperationStatus removePatterns(NodeMetadata nodeMetadata, Date keepAllAfter, Integer keepNVersions,
			boolean dryRun);

	OperationStatus restorePatterns(PatternMetadata nodeMetadata, Long version);

	/**
	 * @param nodeMetadata contains basic metadata info
	 * @return last (max) test suite run version or 0 if no runs of this suite configuration before. When
	 * searching for version following criteria are taken into account: company, project, testSuiteName,
	 * environment, domain (even if not set).
	 */
	Long getLastTestSuiteVersion(NodeMetadata nodeMetadata) throws AETException;

	/**
	 * @param nodeMetadata contains basic metadata info
	 * @return test suite run correlationId or null if no runs of this suite configuration before. When
	 * searching for correlationId following criteria are taken into account: company, project, testSuiteName,
	 * environment, domain (even if not set), version.
	 */
	String getCorrelationId(NodeMetadata nodeMetadata) throws AETException;

	/**
	 * @param nodeMetadata contains basic metadata info
	 * @return last report (with max version) correlationId or null if no reports of this suite configuration before. When
	 * searching for correlationId following criteria are taken into account: company, project, testSuiteName,
	 * environment, domain (even if not set).
	 */
	String getLastReportCorrelationId(NodeMetadata nodeMetadata) throws AETException;
}
