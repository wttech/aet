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


import com.cognifide.aet.cleaner.context.CleanerContext;
import com.cognifide.aet.cleaner.route.MetadataCleanerRouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanerJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(CleanerJob.class);

  private static final String DIRECT_START = "direct:start";

  static final String KEY_ROUTE_BUILDER = "routeBuilder";

  static final String KEY_REMOVE_OLDER_THAN = "removeOlderThan";

  static final String KEY_KEEP_N_VERSIONS = "keepNVersions";

  static final String KEY_COMPANY_FILTER = "companyFilter";

  static final String KEY_PROJECT_FILTER = "projectFilter";

  static final String KEY_DRY_RUN = "dryRun";

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    CamelContext camelContext = null;
    try {
      final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
      final MetadataCleanerRouteBuilder cleanerRouteBuilder = (MetadataCleanerRouteBuilder) jobDataMap
          .get(KEY_ROUTE_BUILDER);

      final Long removeOlderThan = (Long) jobDataMap.get(KEY_REMOVE_OLDER_THAN);
      final Long keepNVersions = (Long) jobDataMap.get(KEY_KEEP_N_VERSIONS);
      final String companyFilter = (String) jobDataMap.get(KEY_COMPANY_FILTER);
      final String projectFilter = (String) jobDataMap.get(KEY_PROJECT_FILTER);
      final Boolean dryRun = (Boolean) jobDataMap.get(KEY_DRY_RUN);

      camelContext = new DefaultCamelContext();
      camelContext.setAllowUseOriginalMessage(false);
      camelContext.addRoutes(cleanerRouteBuilder);
      camelContext.start();

      ProducerTemplate template = camelContext.createProducerTemplate();
      template
          .sendBody(DIRECT_START, new CleanerContext(removeOlderThan, keepNVersions, companyFilter,
              projectFilter, dryRun));
    } catch (Exception e) {
      LOGGER.error("Fatal error during cleaner job setup", e);
    } finally {
      if (camelContext != null) {
        try {
          camelContext.stop();
        } catch (Exception e) {
          LOGGER.error("Can't stop camel context", e);
        }
      }
    }
  }
}
