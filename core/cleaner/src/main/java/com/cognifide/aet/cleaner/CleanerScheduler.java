/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.cleaner;

import com.cognifide.aet.cleaner.route.MetadataCleanerRouteBuilder;
import com.cognifide.aet.cleaner.validation.CleanerSchedulerValidator;
import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.ValidationResultBuilderFactory;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
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

@Service(CleanerScheduler.class)
@Component(immediate = true, metatype = true, label = "AET Cleaning Scheduler Service", policy = ConfigurationPolicy.REQUIRE, configurationFactory = true)
public class CleanerScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CleanerScheduler.class);

  private static final String COMPANY_NAME = "companyName";

  private static final String PROJECT_NAME = "projectName";

  private static final String REMOVE_OLDER_THAN = "removeOlderThan";

  private static final String KEEP_N_VERSIONS = "keepNVersions";

  private static final String SCHEDULE_CRON = "schedule";

  private static final String DRY_RUN = "dryRun";

  private static final long DEFAULT_REMOVE_OLDER_THAN_PARAM = 10L;

  private static final long DEFAULT_KEEP_N_VERSIONS_PARAM = 1L;

  private Scheduler scheduler;

  @Property(name = SCHEDULE_CRON, label = "Schedule", description = "CRON notation of when the job is to be fired. [example: '0 0 21 ? * *' will trigger job daily at 21:00].")
  private String schedule;

  @Property(name = KEEP_N_VERSIONS, label = "Last versions to keep", description = "Defines number of artifacts versions that will be left after clean operation [integer]. If left empty, only one version will be kept after cleaning operation.", longValue = DEFAULT_KEEP_N_VERSIONS_PARAM)
  private Long keepNVersions;

  @Property(name = REMOVE_OLDER_THAN, label = "Remove artifacts older than", description = "Defines how old files should be removed [integer days]. Works as conjunction with last versions to keep.", longValue = DEFAULT_REMOVE_OLDER_THAN_PARAM)
  private Long removeOlderThan;

  @Property(name = COMPANY_NAME, label = "Company Name", description = "Name of the company for which we wish cleaning to be performed. Leave blank if you wish to trigger this job for each company on database.")
  private String companyName;

  @Property(name = PROJECT_NAME, label = "Project Name", description = "Name of the project for which we wish cleaning to be performed. Leave blank if you wish to trigger this job for each project on database.")
  private String projectName;

  @Property(name = DRY_RUN, label = "Dry run", description = "Flag that says if operation should be run in 'dry run' mode. When checked, no changes will be performed on database.", boolValue = true)
  private Boolean dryRun;

  @Reference
  private MetadataCleanerRouteBuilder metadataCleanerRouteBuilder;

  @Reference
  private ValidationResultBuilderFactory validationResultBuilderFactory;

  /**
   * Name of scheduled job in this CleanerScheduler session.
   */
  private String scheduledJob;

  @Activate
  public void activate(Map<String, ?> properties) {
    LOGGER.info("Activating CleanerScheduler.");
    try {
      schedule = PropertiesUtil.toString(properties.get(SCHEDULE_CRON), null);
      removeOlderThan = PropertiesUtil
          .toLong(properties.get(REMOVE_OLDER_THAN), DEFAULT_REMOVE_OLDER_THAN_PARAM);
      keepNVersions = PropertiesUtil
          .toLong(properties.get(KEEP_N_VERSIONS), DEFAULT_KEEP_N_VERSIONS_PARAM);
      companyName = PropertiesUtil.toString(properties.get(COMPANY_NAME), "");
      projectName = PropertiesUtil.toString(properties.get(PROJECT_NAME), "");
      dryRun = PropertiesUtil.toBoolean(properties.get(DRY_RUN), true);

      ValidationResultBuilder validationResultBuilder = validationResultBuilderFactory
          .createInstance();
      new CleanerSchedulerValidator(schedule, keepNVersions, removeOlderThan)
          .validate(validationResultBuilder);
      if (!validationResultBuilder.hasErrors()) {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        scheduledJob = registerCleaningJob();
        LOGGER.info("CleanerScheduler has been activated successfully with parameters: {}",
            this.toString());
      } else {
        LOGGER
            .error("Cleaner has been not scheduled because of errors: {}", validationResultBuilder);
      }
    } catch (SchedulerException e) {
      LOGGER.error("Failed to activate scheduler", e);
    } catch (Exception e) {
      LOGGER.error("Fatal error while activating cleaner scheduler", e);
    }
  }

  @Deactivate
  public void deactivate() {
    try {
      LOGGER.info("Deactivating CleanerScheduler.");
      if (!scheduler.isShutdown()) {
        scheduler.deleteJob(JobKey.jobKey(scheduledJob));
        scheduler.shutdown();
        scheduler = null;
        LOGGER.info("CleanerScheduler has been deactivated");
      }
    } catch (SchedulerException e) {
      LOGGER.error("Failed to deactivated scheduler", e);
    } catch (Exception e) {
      LOGGER.error("Fatal error while deactivating scheduler", e);
    }
  }

  private String registerCleaningJob() throws SchedulerException {
    final UUID uuid = UUID.randomUUID();

    final String cleanerJobName = "cleanMongoDbJob-" + uuid;
    final String cleanerTriggerName = "cleanMongoDbTrigger-" + uuid;

    final ImmutableMap<String, Object> jobData = ImmutableMap.<String, Object>builder()
        .put(CleanerJob.KEY_ROUTE_BUILDER, metadataCleanerRouteBuilder)
        .put(CleanerJob.KEY_KEEP_N_VERSIONS, keepNVersions)
        .put(CleanerJob.KEY_REMOVE_OLDER_THAN, removeOlderThan)
        .put(CleanerJob.KEY_COMPANY_FILTER, companyName)
        .put(CleanerJob.KEY_PROJECT_FILTER, projectName)
        .put(CleanerJob.KEY_DRY_RUN, dryRun)
        .build();

    JobDetail jobDetail = JobBuilder.newJob(CleanerJob.class)
        .withIdentity(cleanerJobName)
        .usingJobData(new JobDataMap(jobData))
        .build();

    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(cleanerTriggerName)
        .withSchedule(CronScheduleBuilder.cronSchedule(schedule))
        .build();

    scheduler.scheduleJob(jobDetail, trigger);
    return cleanerJobName;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("schedule", schedule)
        .add("keepNVersions", keepNVersions)
        .add("removeOlderThan", removeOlderThan)
        .add("companyName", companyName)
        .add("projectName", projectName)
        .add("dryRun", dryRun)
        .toString();
  }
}
