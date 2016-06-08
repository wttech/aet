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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;

import java.util.Collection;
import java.util.List;

import com.cognifide.aet.communication.api.node.NodeMetadata;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;
import com.cognifide.aet.validation.impl.ValidationResultBuilderImpl;
import com.cognifide.aet.vs.VersionStorage;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class CleanerSchedulerValidatorTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CleanerSchedulerValidatorTest.class);

	@Mock
	private VersionStorage storage;

	private ValidationResultBuilder builder;

	private Collection companies;

	private Collection projects;

	private ValidatorBuilder validatorBuilder;

	@Before
	public void setUp() {
		builder = new ValidationResultBuilderImpl();
		validatorBuilder = new ValidatorBuilder(storage);
		companies = Lists.newArrayList("company_1", "company_2", "company_3");
		projects = Lists.newArrayList("project_1", "project_2", "project_3");
		Mockito.when(storage.getCompanies(any(NodeMetadata.class))).thenReturn(companies);
		Mockito.when(storage.getProjects(any(NodeMetadata.class))).thenReturn(projects);
	}

	@Test
	public void validateTest_nullStorage() {
		validatorBuilder = new ValidatorBuilder(null);

		validatorBuilder.build().validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), is("GridFS Storage is null"));
	}

	@Test
	public void validateArtifactTypesTest_invalidArtifactTypes() {
		List<String> invalidArtifactTypesList = Lists.newArrayList("invalid", "invalid1,invalid2",
				"invalid1, invalid2,invalid3");

		for (String invalidArtifactTypes : invalidArtifactTypesList) {
			builder = new ValidationResultBuilderImpl();
			LOGGER.info("Checking invalid '{}'", invalidArtifactTypes);

			Validator tested = validatorBuilder.setArtifactTypes(invalidArtifactTypes).build();
			tested.validate(builder);

			assertThat(builder.isValid(), is(false));
			assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
			assertThat(builder.getErrorMessages().get(0).getMessage(), containsString("Invalid artifact type provided: "));
		}
	}

	@Test
	public void validateArtifactTypesTest_notAllowedArtifactTypes() {
		String notAllowedArtifactType = "PATTERNS";
		Validator tested = validatorBuilder.setArtifactTypes(notAllowedArtifactType).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(0));
	}

	@Test
	public void validateTest_keepNVersionsNull() {
		Validator tested = validatorBuilder.setKeeNVersions(null).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(0));
	}

	@Test
	public void validateTest_keepNVersions0() {
		Validator tested = validatorBuilder.setKeeNVersions(0).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), is("Leave N Patterns has to be greater than 0"));
	}

	@Test
	public void validateTest_keepNVersions1() {
		Validator tested = validatorBuilder.setKeeNVersions(1).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateTest_removeOlderThanMinus1(){
		Validator tested = validatorBuilder.setRemoveOlderThan(-1).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
	}

	@Test
	public void validateTest_removeOlderThan(){
		//case 0
		Validator tested = validatorBuilder.setRemoveOlderThan(0).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));

		//case > 0
		tested = validatorBuilder.setRemoveOlderThan(1).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateCompanyName() {
		Validator tested = validatorBuilder.setCompanyName("company_2").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateCompanyName_nonExistingCompany() {
		Validator tested = validatorBuilder.setCompanyName("non-existing-company").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), is("Provided company does not exists on database"));
	}

	@Test
	public void validateCompanyName_emptyProject() {
		Validator tested = validatorBuilder.setCompanyName(null).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), containsString("Company name is empty. You have to provided valid company name or use wildcard char"));
	}

	@Test
	public void validateCompanyName_wildCard() {
		Validator tested = validatorBuilder.setCompanyName("*").setProjectName("*").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateProjectName() {
		Validator tested = validatorBuilder.setProjectName("project_2").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateProjectName_nonExistingProject() {
		Validator tested = validatorBuilder.setProjectName("non-existing-project").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), is("Provided project does not exists on database"));
	}

	@Test
	public void validateProjectName_emptyProject() {
		Validator tested = validatorBuilder.setProjectName(null).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), containsString("Project name is empty. You have to provided valid project name or use wildcard char"));
	}

	@Test
	public void validateProjectName_wildCard() {
		Validator tested = validatorBuilder.setProjectName("*").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateCompanyNameAndProjectName() {
		Validator tested = validatorBuilder.setCompanyName("*").setProjectName("project_1").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), containsString("If you provide project name then company name may not be wildcard"));
	}

	@Test
	public void validateTest_emptyArtifactTypes(){
		Validator tested = validatorBuilder.setArtifactTypes("").build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateTest_emptyKeepNVersions(){
		Validator tested = validatorBuilder.setKeeNVersions(null).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(true));
	}

	@Test
	public void validateTest_emptySchedule(){
		Validator tested = validatorBuilder.setSchedule(null).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), is("CRON expression may not be empty"));
	}

	@Test
	public void validateTest_emptyArtifacTypes_emptyKeepNPatterns(){
		Validator tested = validatorBuilder.setArtifactTypes(null).setKeeNVersions(null).build();

		tested.validate(builder);

		assertThat(builder.isValid(), is(false));
		assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
		assertThat(builder.getErrorMessages().get(0).getMessage(), is("At least one has to be defined - artifactTypes or keepNVersions"));
	}

	private static class ValidatorBuilder {

		private static final String DEFAULT_SCHEDULE = "0 0 21 ? * *";

		private static final String DEFAULT_ARTIFACT_TYPES = "DATA,RESULTS";

		private static final Integer DEFAULT_KEEP_N_VERSIONS = 3;

		private static final Integer DEFAULT_REMOVE_OLDER_THAN = 5;

		private static final String DEFAULT_COMPANY = "company_2";

		private static final String DEFAULT_PROJECT = "project_2";

		private final VersionStorage storage;

		private String schedule = DEFAULT_SCHEDULE;

		private String artifactTypes = DEFAULT_ARTIFACT_TYPES;

		private Integer keeNVersions = DEFAULT_KEEP_N_VERSIONS;

		private int removeOlderThan = DEFAULT_REMOVE_OLDER_THAN;

		private String companyName = DEFAULT_COMPANY;

		private String projectName = DEFAULT_PROJECT;

		public ValidatorBuilder(VersionStorage storage) {
			this.storage = storage;
		}

		private ValidatorBuilder setSchedule(String schedule) {
			this.schedule = schedule;
			return this;
		}

		private ValidatorBuilder setArtifactTypes(String artifactTypes) {
			this.artifactTypes = artifactTypes;
			return this;
		}

		private ValidatorBuilder setKeeNVersions(Integer keeNVersions) {
			this.keeNVersions = keeNVersions;
			return this;
		}

		private ValidatorBuilder setRemoveOlderThan(int removeOlderThan) {
			this.removeOlderThan = removeOlderThan;
			return this;
		}

		private ValidatorBuilder setCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		private ValidatorBuilder setProjectName(String projectName) {
			this.projectName = projectName;
			return this;
		}

		public Validator build() {
			return new CleanerSchedulerValidator(storage,schedule, artifactTypes, keeNVersions, removeOlderThan,
					companyName, projectName);
		}
	}
}
