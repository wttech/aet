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

import com.cognifide.aet.cleaner.configuration.CleanerSchedulerConf;
import com.cognifide.aet.cleaner.route.MetadataCleanerRouteBuilder;
import com.cognifide.aet.cleaner.validation.CleanerSchedulerValidator;
import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.ValidationResultBuilderFactory;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.UUID;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
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

@Component(
    service = CleanerScheduler.class,
    immediate = true,
    configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = CleanerSchedulerConf.class, factory = true)
public class CleanerScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CleanerScheduler.class);

  private Scheduler scheduler;

  private CleanerSchedulerConf config;

  @Reference
  private MetadataCleanerRouteBuilder metadataCleanerRouteBuilder;

  @Reference
  private ValidationResultBuilderFactory validationResultBuilderFactory;

  /**
   * Name of scheduled job in this CleanerScheduler session.
   */
  private String scheduledJob;

  @Activate
  public void activate(CleanerSchedulerConf config) {
    LOGGER.info("Activating CleanerScheduler.");
    try {
      this.config = config;

      ValidationResultBuilder validationResultBuilder = validationResultBuilderFactory
          .createInstance();
      new CleanerSchedulerValidator(config.schedule(), config.keepNVersions(), config.removeOlderThan())
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
        .put(CleanerJob.KEY_KEEP_N_VERSIONS, config.keepNVersions())
        .put(CleanerJob.KEY_REMOVE_OLDER_THAN, config.removeOlderThan())
        .put(CleanerJob.KEY_COMPANY_FILTER, config.companyName())
        .put(CleanerJob.KEY_PROJECT_FILTER, config.projectName())
        .put(CleanerJob.KEY_DRY_RUN, config.dryRun())
        .build();

    JobDetail jobDetail = JobBuilder.newJob(CleanerJob.class)
        .withIdentity(cleanerJobName)
        .usingJobData(new JobDataMap(jobData))
        .build();

    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(cleanerTriggerName)
        .withSchedule(CronScheduleBuilder.cronSchedule(config.schedule()))
        .build();

    scheduler.scheduleJob(jobDetail, trigger);
    return cleanerJobName;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("schedule", config.schedule())
        .add("keepNVersions", config.keepNVersions())
        .add("removeOlderThan", config.removeOlderThan())
        .add("companyName", config.companyName())
        .add("projectName", config.projectName())
        .add("dryRun", config.dryRun())
        .toString();
  }
}
