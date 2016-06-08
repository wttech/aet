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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.builders.NodeMetadataBuilder;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.vs.VersionStorage;
import com.google.common.collect.Lists;

@DisallowConcurrentExecution
public class CleanerJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(CleanerJob.class);

	private static final char SEPARATOR = ',';

	private VersionStorage storage;

	private Set<ArtifactType> artifactTypes;

	private Integer removeOlderThan;

	private String companyName;

	private String projectName;

	private Integer keepNVersions;

	private Boolean dryRun;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info(
				"{}Cleaning operation start. Configuration: Remove data older than: {} days and keep last {} versions",
				dryRun ? "[DRY RUN] " : "", removeOlderThan, keepNVersions);

		ExecutionTimer executionTimer = ExecutionTimer.createAndRun("CleanerJob");
		List<NodeMetadata> nodeMetadatas = prepareCompanies();

		int progressCounter = 1;
		int progressTotalSize = nodeMetadatas.size();

		for (NodeMetadata nodeMetadata : nodeMetadatas) {
			String company = nodeMetadata.getCompany();
			String project = nodeMetadata.getProject();
			removeNodes(nodeMetadata);
			removeReports(nodeMetadata);
			removePatterns(nodeMetadata);
			LOGGER.info("Removing nodes Progress: {}/{} Company: {} Project: {}", progressCounter,
					progressTotalSize, company, project);
			progressCounter++;
		}
		executionTimer.finish();
		LOGGER.info("{}Cleaning job finished. Total time: {} ms ({})", dryRun ? "[DRY RUN] " : "",
				executionTimer.getExecutionTimeInMillis(), executionTimer.getExecutionTimeInMMSS());
	}

	private void removeReports(NodeMetadata nodeMetadata) {
		if (canRemoveReports()) {
			Date keepAllAfter = calculateRemoveDate();
			LOGGER.info("Reports with version older than {} and created before {} will be removed.",
					keepNVersions, keepAllAfter);
			OperationStatus operationStatus = storage.removeReports(nodeMetadata, keepAllAfter,
					keepNVersions, dryRun);
			LOGGER.info("Removing reports completed. isSuccess: {}, message: {}",
					operationStatus.isSuccess(), operationStatus.getMessage());
		} else {
			LOGGER.info("No reports will be removed during this clean job.");
		}
	}
	private void removePatterns(NodeMetadata nodeMetadata) {
		if (canRemovePatterns()) {
			Date keepAllAfter = calculateRemoveDate();
			LOGGER.info("Patterns with version older than {} and created before {} will be removed.",
					keepNVersions, keepAllAfter);
			OperationStatus operationStatus = storage.removePatterns(nodeMetadata, keepAllAfter,
					keepNVersions, dryRun);
			LOGGER.info("Removing patterns completed. isSuccess: {}, message: {}",
					operationStatus.isSuccess(), operationStatus.getMessage());
		} else {
			LOGGER.info("No patterns will be removed during this clean job.");
		}
	}

	private void removeNodes(NodeMetadata nodeMetadata) {
		if (canRemoveNodes()) {
			Set<ArtifactType> basicArtifactTypes = new HashSet<>(artifactTypes);
			basicArtifactTypes.remove(ArtifactType.PATTERNS);
			basicArtifactTypes.remove(ArtifactType.REPORTS);
			Date keepAllAfter = calculateRemoveDate();
			LOGGER.info("Artifacts {} with version older than {} and created before {} will be removed.",
					basicArtifactTypes, keepNVersions, keepAllAfter);
			OperationStatus operationStatus = storage.removeBasicArtifacts(nodeMetadata, keepAllAfter,
					keepNVersions, basicArtifactTypes, dryRun);
			LOGGER.info("Removing nodes completed. isSuccess: {}, message: {}", operationStatus.isSuccess(),
					operationStatus.getMessage());
		} else {
			LOGGER.info("No data artifacts will be removed during this clean job.");
		}
	}

	private List<NodeMetadata> prepareCompanies() {
		List<NodeMetadata> nodeMetadatas = Lists.newArrayList();
		if (isWildCard(companyName)) {
			Collection<String> companies = storage.getCompanies(NodeMetadataBuilder.getNodeMetadata()
					.withCompany(companyName).build());
			for (String company : companies) {
				prepareProjects(nodeMetadatas, company);
			}
		} else {
			prepareProjects(nodeMetadatas, companyName);
		}
		return nodeMetadatas;
	}

	private void prepareProjects(List<NodeMetadata> nodeMetadatas, String company) {
		if (isWildCard(projectName)) {
			NodeMetadata companyNodeMetadata = NodeMetadataBuilder.getNodeMetadata().withCompany(company)
					.build();
			Collection<String> projects = storage.getProjects(companyNodeMetadata);
			for (String project : projects) {
				NodeMetadataBuilder builder = NodeMetadataBuilder.getNodeMetadata().withCompany(company)
						.withProject(project);
				nodeMetadatas.add(builder.build());
			}
		} else {
			NodeMetadataBuilder builder = NodeMetadataBuilder.getNodeMetadata().withCompany(company)
					.withProject(projectName);
			nodeMetadatas.add(builder.build());
		}
	}

	private Date calculateRemoveDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -removeOlderThan);
		return calendar.getTime();
	}

	private boolean isWildCard(String value) {
		return StringUtils.equals(value, CleanerScheduler.WILDCARD_CHAR);
	}

	private boolean canRemoveReports() {
		return artifactTypes.contains(ArtifactType.REPORTS);
	}

	private boolean canRemovePatterns() {
		return artifactTypes.contains(ArtifactType.PATTERNS);
	}

	private boolean canRemoveNodes() {
		Set<ArtifactType> basicArtifactTypes = new HashSet<>(artifactTypes);
		basicArtifactTypes.remove(ArtifactType.REPORTS);
		basicArtifactTypes.remove(ArtifactType.PATTERNS);
		return !basicArtifactTypes.isEmpty();
	}

	public void setStorage(VersionStorage storage) {
		this.storage = storage;
	}

	public void setArtifactTypes(String artifactTypes) {
		this.artifactTypes = ArtifactType.valueOf(StringUtils.split(artifactTypes, SEPARATOR));
	}

	public void setRemoveOlderThan(Integer removeOlderThan) {
		this.removeOlderThan = removeOlderThan;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setKeepNVersions(Integer keepNVersions) {
		this.keepNVersions = keepNVersions;
	}

	public void setDryRun(Boolean dryRun) {
		this.dryRun = dryRun;
	}
}
