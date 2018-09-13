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
package com.cognifide.aet.cleaner.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET Cleaning Scheduler Service", description = "AET Cleaning Scheduler Service")
public @interface CleanerSchedulerConf {

  String COMPANY_NAME = "Company Name";

  String PROJECT_NAME = "Project Name";

  String REMOVE_OLDER_THAN = "Remove artifacts older than";

  String KEEP_N_VERSIONS = "Last versions to keep";

  String SCHEDULE_CRON = "Schedule";

  String DRY_RUN = "Dry run";

  long DEFAULT_REMOVE_OLDER_THAN_PARAM = 10L;

  long DEFAULT_KEEP_N_VERSIONS_PARAM = 1L;

  @AttributeDefinition(
      name = SCHEDULE_CRON,
      description = "CRON notation of when the job is to be fired. [example: '0 0 21 ? * *' will trigger job daily at 21:00].")
  String schedule();

  @AttributeDefinition(
      name = KEEP_N_VERSIONS,
      description = "Defines number of artifacts versions that will be left after clean operation [integer]. If left empty, only one version will be kept after cleaning operation.",
      type = AttributeType.LONG)
  long keepNVersions() default DEFAULT_KEEP_N_VERSIONS_PARAM;

  @AttributeDefinition(
      name = REMOVE_OLDER_THAN,
      description = "Defines how old files should be removed [integer days]. Works as conjunction with last versions to keep.",
      type = AttributeType.LONG)
  long removeOlderThan() default DEFAULT_REMOVE_OLDER_THAN_PARAM;

  @AttributeDefinition(
      name = COMPANY_NAME,
      description = "Name of the company for which we wish cleaning to be performed. Leave blank if you wish to trigger this job for each company on database.",
      type = AttributeType.STRING)
  String companyName() default  "";

  @AttributeDefinition(
      name = PROJECT_NAME,
      description = "Name of the project for which we wish cleaning to be performed. Leave blank if you wish to trigger this job for each project on database.",
      type = AttributeType.STRING)
  String projectName() default  "";

  @AttributeDefinition(
      name = DRY_RUN,
      description = "Flag that says if operation should be run in 'dry run' mode. When checked, no changes will be performed on database.",
      type = AttributeType.BOOLEAN)
  boolean dryRun() default true;


}
