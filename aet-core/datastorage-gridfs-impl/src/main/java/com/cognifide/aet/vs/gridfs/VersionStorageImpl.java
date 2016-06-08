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
package com.cognifide.aet.vs.gridfs;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.communication.api.RebaseOperationStatus;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorage;
import com.cognifide.aet.vs.VersionStorageException;

@Service
@Component(label = "AET GridFs Version Storage", immediate = true)
public class VersionStorageImpl implements VersionStorage {

	@Reference
	private GridFsStorage storage;

	@Override
	public Node getDataNode(NodeMetadata nodeMetadata) {
		return new DefaultNodeImpl(ArtifactType.DATA, storage, nodeMetadata);
	}

	@Override
	public Collection getPatternModuleVersions(NodeMetadata nodeMetadata) {
		return storage.getPatternModuleVersions(nodeMetadata);
	}

	@Override
	public Node getPatternNode(NodeMetadata nodeMetadata) {
		return new DefaultNodeImpl(ArtifactType.PATTERNS, storage, nodeMetadata);
	}

	@Override
	public Node getResultNode(NodeMetadata nodeMetadata) {
		return new DefaultNodeImpl(ArtifactType.RESULTS, storage, nodeMetadata);
	}

	@Override
	public Node getReportNode(NodeMetadata nodeMetadata) {
		return new DefaultNodeImpl(ArtifactType.REPORTS, storage, nodeMetadata);
	}

	@Override
	public OperationStatus removeBasicArtifacts(NodeMetadata nodeMetadata, Date keepAllAfter,
			Integer keepNVersions, Set<ArtifactType> artifactTypes, Boolean dryRun) {
		return storage.removeBasicArtifacts(nodeMetadata, keepAllAfter, keepNVersions, artifactTypes, dryRun);
	}

	@Override
	public OperationStatus removeReports(NodeMetadata nodeMetadata, Date keepAllAfter, Integer keepNVersions,
			boolean dryRun) {
		return storage.removeReports(nodeMetadata, keepAllAfter, keepNVersions, dryRun);
	}

	@Override
	public void setPatternCandidate(NodeMetadata nodeMetadata) {
		storage.setPatternCandidate(nodeMetadata);
	}

	@Override
	public RebaseOperationStatus rebasePatterns(NodeMetadata nodeMetadata) throws VersionStorageException {
		return storage.rebasePatterns(nodeMetadata);
	}

	@Override
	public Map<String, String> getRebasePatternParameters(PatternMetadata nodeMetadata) {
		return RebasePatternsHelper.getRebasePatternParameters(storage.getServerUrl(), nodeMetadata);
	}

	@Override
	public Collection getCompanies(NodeMetadata nodeMetadata) {
		return storage.getCompanies();
	}

	@Override
	public Collection getProjects(NodeMetadata nodeMetadata) {
		return storage.getProjects(nodeMetadata);
	}

	@Override
	public Collection getTestSuites(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(null, nodeMetadata, NodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getEnvironments(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(null, nodeMetadata, NodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getTestCases(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(null, nodeMetadata, UrlNodeMetadata.TEST_NAME_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getTestCaseUrls(NodeMetadata nodeMetadata) {
		return storage.getTestCaseUrls(nodeMetadata);
	}

	@Override
	public Collection getTypes() {
		return storage.getArtifactTypes();
	}

	@Override
	public Collection getDataExecutions(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.DATA, nodeMetadata,
				CollectMetadata.CORRELATION_ID_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getResultsExecutions(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.RESULTS, nodeMetadata,
				CompareMetadata.CORRELATION_ID_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getPatternsCollectorsTypes(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.PATTERNS, nodeMetadata,
				PatternMetadata.COLLECTOR_ATTRIBUTE_TYPE);
	}

	@Override
	public Collection getPatternsCollectorsNames(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.PATTERNS, nodeMetadata,
				PatternMetadata.COLLECTOR_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getDataCollectorTypes(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.DATA, nodeMetadata,
				CollectMetadata.COLLECTOR_ATTRIBUTE_TYPE);
	}

	@Override
	public Collection getDataCollectorNames(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.DATA, nodeMetadata,
				CollectMetadata.COLLECTOR_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getResultsCollectorsTypes(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.RESULTS, nodeMetadata,
				CompareMetadata.COLLECTOR_ATTRIBUTE_TYPE);
	}

	@Override
	public Collection getResultsCollectorsNames(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.RESULTS, nodeMetadata,
				CompareMetadata.COLLECTOR_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getResultsComparatorsTypes(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.RESULTS, nodeMetadata,
				CompareMetadata.COMPARATOR_ATTRIBUTE_TYPE);
	}

	@Override
	public Collection getResultsComparatorsNames(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.RESULTS, nodeMetadata,
				CompareMetadata.COMPARATOR_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getPatternsArtifacts(NodeMetadata nodeMetadata) {
		return storage.getArtifacts(ArtifactType.PATTERNS, nodeMetadata);
	}

	@Override
	public Collection getDataArtifacts(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.DATA, nodeMetadata, GridFsHelper.FILENAME_ATTRIBUTE);
	}

	@Override
	public InputStream getDataArtifactData(NodeMetadata nodeMetadata, String artifactName) {
		return storage.getStream(nodeMetadata, artifactName, ArtifactType.DATA);
	}

	@Override
	public InputStream getResultsArtifactData(NodeMetadata nodeMetadata, String artifactName) {
		return storage.getStream(nodeMetadata, artifactName, ArtifactType.RESULTS);
	}

	@Override
	public InputStream getPatternsArtifactData(NodeMetadata nodeMetadata, String artifactName) {
		return storage.getStream(nodeMetadata, artifactName, ArtifactType.PATTERNS);
	}

	@Override
	public Collection getResultsArtifacts(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.RESULTS, nodeMetadata, GridFsHelper.FILENAME_ATTRIBUTE);
	}

	@Override
	public InputStream getReportsArtifactData(NodeMetadata nodeMetadata, String artifactName) {
		return storage.getStream(nodeMetadata, artifactName, ArtifactType.REPORTS);
	}

	@Override
	public Collection<String> getReportsExecutionArtifacts(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.REPORTS, nodeMetadata, GridFsHelper.FILENAME_ATTRIBUTE);
	}

	@Override
	public Collection<String> getReportsExecutions(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.REPORTS, nodeMetadata,
				ReportMetadata.CORRELATION_ID_ATTRIBUTE_NAME);
	}

	@Override
	public Collection getReportsModules(NodeMetadata nodeMetadata) {
		return storage.getSubNodesNames(ArtifactType.REPORTS, nodeMetadata,
				GridFsHelper.REPORTER_ATTRIBUTE_TYPE);
	}

	@Override
	public OperationStatus removePatterns(NodeMetadata nodeMetadata, Date keepAllAfter, Integer keepNVersions,
			boolean dryRun) {
		return storage.removePatterns(nodeMetadata, keepAllAfter, keepNVersions, dryRun);
	}

	@Override
	public OperationStatus restorePatterns(PatternMetadata nodeMetadata, Long version) {
		return storage.restorePatterns(nodeMetadata, version);
	}

	@Override
	public Long getLastTestSuiteVersion(NodeMetadata nodeMetadata) throws AETException {
		return storage.getMaxTestSuiteRunVersion(nodeMetadata);
	}

	@Override
	public String getLastReportCorrelationId(NodeMetadata nodeMetadata) throws AETException {
		return storage.getLastReportCorrelationId(nodeMetadata);
	}

	@Override
	public String getCorrelationId(NodeMetadata nodeMetadata) throws AETException {
		return storage.getCorrelationId(nodeMetadata);
	}

}
