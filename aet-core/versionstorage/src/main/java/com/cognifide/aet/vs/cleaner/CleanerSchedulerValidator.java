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

import java.util.Collection;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.builders.NodeMetadataBuilder;
import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;
import com.cognifide.aet.vs.VersionStorage;

public class CleanerSchedulerValidator implements Validator {

	private final VersionStorage storage;

	private final String schedule;

	private final String artifactTypes;

	private final Integer keepNVersions;

	private final int removeOlderThan;

	private final String companyName;

	private final String projectName;

	public CleanerSchedulerValidator(VersionStorage storage, String schedule, String artifactTypes,
			Integer keepNVersions, int removeOlderThan, String companyName, String projectName) {
		this.storage = storage;
		this.schedule = schedule;
		this.artifactTypes = artifactTypes;
		this.keepNVersions = keepNVersions;
		this.removeOlderThan = removeOlderThan;
		this.companyName = companyName;
		this.projectName = projectName;
	}

	@Override
	public void validate(ValidationResultBuilder builder) {
		if (storage == null) {
			builder.addErrorMessage("GridFS Storage is null");
		} else {
			validateSchedule(schedule, builder);
			validateArtifactTypes(artifactTypes, builder);

			if (keepNVersions != null && keepNVersions <= 0) {
				builder.addErrorMessage("Leave N Patterns has to be greater than 0");
			}
			if (removeOlderThan < 0) {
				builder.addErrorMessage("Remove Older Than has to be greater or equal than 0");
			}
			if (StringUtils.isEmpty(artifactTypes) && keepNVersions == null) {
				builder.addErrorMessage("At least one has to be defined - artifactTypes or keepNVersions");
			}

			validateCompanyName(companyName, builder);
			validateProjectName(projectName, builder);
		}
	}

	private void validateSchedule(String schedule, ValidationResultBuilder builder) {
		if (StringUtils.isEmpty(schedule)) {
			builder.addErrorMessage("CRON expression may not be empty");
		}
	}

	private void validateArtifactTypes(String artifactTypesString, ValidationResultBuilder builder) {
		if (StringUtils.isNotBlank(artifactTypesString)) {
			String[] artifactTypesNames = artifactTypesString.split(",");
			for (String artifactType : artifactTypesNames) {
				if (!EnumUtils.isValidEnum(ArtifactType.class, artifactType.trim())) {
					builder.addErrorMessage("Invalid artifact type provided: " + artifactType);
					break;
				}
			}
		}
	}

	private void validateCompanyName(String companyName, ValidationResultBuilder builder) {
		if (StringUtils.isEmpty(companyName)) {
			builder.addErrorMessage("Company name is empty. You have to provided valid company name or use wildcard char ["
					+ CleanerScheduler.WILDCARD_CHAR + "]");
		} else if (!isWildCard(companyName)) {
			Collection companies = storage.getCompanies(NodeMetadataBuilder.getNodeMetadata()
					.withCompany(companyName).build());
			if (!companies.contains(companyName)) {
				builder.addErrorMessage("Provided company does not exists on database");
			}
		}
	}

	private void validateProjectName(String projectName, ValidationResultBuilder builder) {
		if (StringUtils.isEmpty(projectName)) {
			builder.addErrorMessage("Project name is empty. You have to provided valid project name or use wildcard char ["
					+ CleanerScheduler.WILDCARD_CHAR + "]");
		} else if (!isWildCard(projectName)) {
			if (isWildCard(companyName)) {
				builder.addErrorMessage("If you provide project name then company name may not be wildcard");
			} else {
				Collection projects = storage.getProjects(NodeMetadataBuilder.getNodeMetadata()
						.withCompany(companyName).build());
				if (!projects.contains(projectName)) {
					builder.addErrorMessage("Provided project does not exists on database");
				}
			}
		}
	}

	private boolean isWildCard(String value) {
		return StringUtils.equals(value, CleanerScheduler.WILDCARD_CHAR);
	}

}
