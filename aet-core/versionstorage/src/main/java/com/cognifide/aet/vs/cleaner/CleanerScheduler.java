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

import java.util.Map;
import java.util.UUID;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.ValidationResultBuilderFactory;
import com.cognifide.aet.validation.Validator;
import com.cognifide.aet.vs.VersionStorage;
import com.google.common.base.Objects;

@Service(CleanerScheduler.class)
@Component(immediate = true, label = "AET Cleaning Scheduler Service", policy = ConfigurationPolicy.REQUIRE, metatype = true, configurationFactory = true)
public class CleanerScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CleanerScheduler.class);

	public static final String WILDCARD_CHAR = "*";

	private static final String STORAGE_KEY = "storage";

	private static final String ARTIFACT_TYPES = "artifactTypes";

	private static final String COMPANY_NAME = "companyName";

	private static final String PROJECT_NAME = "projectName";

	private static final String REMOVE_OLDER_THAN = "removeOlderThan";

	private static final String KEEP_N_VERSIONS = "keepNVersions";

	private static final String SCHEDULE = "schedule";

	private static final String DRY_RUN = "dryRun";

	private static final String DEFAULT_ARTIFACT_TYPES_PARAM = "DATA,RESULTS,REPORTS,PATTERNS";

	private static final String DEFAULT_COMPANY_NAME_PARAM = WILDCARD_CHAR;

	private static final String DEFAULT_PROJECT_NAME_PARAM = WILDCARD_CHAR;

	private static final int DEFAULT_REMOVE_OLDER_THAN_PARAM = 10;

	private static final int DEFAULT_KEEP_N_VERSIONS_PARAM = 1;

	private Scheduler scheduler;

	@Property(name = SCHEDULE, label = "Schedule", description = "CRON notation of when the job is to be fired. [example: '0 0 21 ? * *' will trigger job daily at 21:00].")
	private String schedule;

	@Property(name = KEEP_N_VERSIONS, label = "Last versions to keep", description = "Defines number of artifacts versions that will be left after clean operation [integer]. If left empty, only one version will be kept after cleaning operation.", intValue = DEFAULT_KEEP_N_VERSIONS_PARAM)
	private Integer keepNVersions;

	@Property(name = REMOVE_OLDER_THAN, label = "Remove artifacts older than", description = "Defines how old files should be removed [integer days]. Works as conjunction with last versions to keep.", intValue = DEFAULT_REMOVE_OLDER_THAN_PARAM)
	private Integer removeOlderThan;

	@Property(name = ARTIFACT_TYPES, label = "Artifact Types", description = "Comma-separated list of artifact types that are to be removed in scheduled event. [DATA,RESULTS,REPORTS,PATTERNS]", value = DEFAULT_ARTIFACT_TYPES_PARAM)
	private String artifactTypes;

	@Property(name = COMPANY_NAME, label = "Company Name", description = "Name of the company for which we wish cleaning to be performed. Use '*' if you wish to trigger this job for each company on database.", value = DEFAULT_COMPANY_NAME_PARAM)
	private String companyName;

	@Property(name = PROJECT_NAME, label = "Project Name", description = "Name of the project for which we wish cleaning to be performed. Use '*' if you wish to trigger this job for each project on database.", value = DEFAULT_PROJECT_NAME_PARAM)
	private String projectName;

	@Property(name = DRY_RUN, label = "Dry run", description = "Flag that says if operation should be run in 'dry run' mode. When checked, no changes will be performed on database.", boolValue = true)
	private Boolean dryRun;

	@Reference
	private VersionStorage storage;

	@Reference
	private ValidationResultBuilderFactory validationResultBuilderFactory;

	/**
	 * Name of scheduled job in this CleanerScheduler session.
	 */
	private String scheduledJob;

	public CleanerScheduler() {
		try {
			this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			LOGGER.error("Failed to init scheduler", e);
		}
	}

	@Activate
	public void activate(Map<String, ?> properties) {
		LOGGER.info("Activating CleanerScheduler.");
		try {
			schedule = PropertiesUtil.toString(properties.get(SCHEDULE), null);
			removeOlderThan = PropertiesUtil.toInteger(properties.get(REMOVE_OLDER_THAN),
					DEFAULT_REMOVE_OLDER_THAN_PARAM);
			keepNVersions = PropertiesUtil.toInteger(properties.get(KEEP_N_VERSIONS),
					DEFAULT_KEEP_N_VERSIONS_PARAM);

			artifactTypes = PropertiesUtil.toString(properties.get(ARTIFACT_TYPES),
					DEFAULT_ARTIFACT_TYPES_PARAM);
			companyName = PropertiesUtil.toString(properties.get(COMPANY_NAME), DEFAULT_COMPANY_NAME_PARAM);
			projectName = PropertiesUtil.toString(properties.get(PROJECT_NAME), DEFAULT_PROJECT_NAME_PARAM);
			dryRun = PropertiesUtil.toBoolean(properties.get(DRY_RUN), true);

			ValidationResultBuilder validationResultBuilder = validationResultBuilderFactory.createInstance();

			Validator validator = new CleanerSchedulerValidator(storage, schedule, artifactTypes,
					keepNVersions, removeOlderThan, companyName, projectName);
			validator.validate(validationResultBuilder);

			if (validationResultBuilder.hasErrors()) {
				String message = validationResultBuilder.toString();
				LOGGER.error(message);
				throw new ComponentException(message);
			}

			this.scheduler.start();
			LOGGER.info("CleanerScheduler has been activated successfully with parameters: {}",
					this.toString());

			scheduledJob = registerCleaningJob(scheduler, buildJobDataMap(), schedule);
		} catch (SchedulerException e) {
			LOGGER.error("Failed to activate scheduler", e);
		}
	}

	@Deactivate
	public void deactivate() {
		try {
			LOGGER.info("Deactivating CleanerScheduler.");
			if (!scheduler.isShutdown()) {
				scheduler.deleteJob(JobKey.jobKey(scheduledJob));
				scheduler.shutdown();
				LOGGER.info("CleanerScheduler has been deactivated");
			}
		} catch (SchedulerException e) {
			LOGGER.error("Failed to deactivated scheduler", e);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("schedule", schedule).add("keepNVersions", keepNVersions)
				.add("removeOlderThan", removeOlderThan).add("artifactTypes", artifactTypes)
				.add("companyName", companyName).add("projectName", projectName).add("dryRun", dryRun)
				.toString();
	}

	private JobDataMap buildJobDataMap() {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(STORAGE_KEY, storage);
		jobDataMap.put(ARTIFACT_TYPES, artifactTypes);
		jobDataMap.put(REMOVE_OLDER_THAN, removeOlderThan);
		jobDataMap.put(KEEP_N_VERSIONS, keepNVersions);
		jobDataMap.put(COMPANY_NAME, companyName);
		jobDataMap.put(PROJECT_NAME, projectName);
		jobDataMap.put(DRY_RUN, dryRun);
		return jobDataMap;
	}

	/**
	 * Static synchronized method which adds job and trigger to scheduler.
	 */
	private static synchronized String registerCleaningJob(Scheduler scheduler, JobDataMap jobDataMap,
			String schedule) throws SchedulerException {
		UUID uuid = UUID.randomUUID();
		String cleanerJobName = "cleanMongoDbJob-" + uuid;
		String cleanerTriggerName = "cleanMongoDbTrigger-" + uuid;

		JobDetail job = JobBuilder.newJob(CleanerJob.class).withIdentity(cleanerJobName)
				.usingJobData(jobDataMap).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cleanerTriggerName)
				.withSchedule(CronScheduleBuilder.cronSchedule(schedule)).build();

		LOGGER.info("Scheduled job {} with trigger {}.", cleanerJobName, cleanerTriggerName);
		scheduler.scheduleJob(job, trigger);
		return cleanerJobName;
	}

}
