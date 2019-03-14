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
package com.cognifide.aet.cleaner.route;


import com.cognifide.aet.cleaner.processors.ErrorHandlingProcessor;
import com.cognifide.aet.cleaner.processors.FetchAllProjectSuitesProcessor;
import com.cognifide.aet.cleaner.processors.RemoveOrphanArtifactsProcessor;
import com.cognifide.aet.cleaner.processors.StartMetadataCleanupProcessor;
import com.cognifide.aet.communication.api.exceptions.AETException;
import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = OrphanCleanerRouteBuilder.class)
public class OrphanCleanerRouteBuilder extends RouteBuilder {

  private static final String ERROR_ENDPOINT = "seda:Error";

  private static final String AGGREGATE_SUITES_STEP = "aggregateSuites";

  @Reference
  private StartMetadataCleanupProcessor startMetadataCleanupProcessor;

  @Reference
  private FetchAllProjectSuitesProcessor fetchAllProjectSuitesProcessor;

  @Reference
  private RemoveOrphanArtifactsProcessor removeOrphanArtifactsProcessor;

  @Override
  public void configure() throws Exception {
    setupErrorHandling();

    from(direct("start"))
        .process(startMetadataCleanupProcessor)
        .split(body())
        .to(direct("fetchProjectSuites"));

    from(direct("fetchProjectSuites"))
        .process(fetchAllProjectSuitesProcessor)
        .split(body())
        .to(direct("removeOrphanArtifacts"));

    from(direct("removeOrphanArtifacts"))
        .process(removeOrphanArtifactsProcessor);
  }

  private void setupErrorHandling() {
    onException(AETException.class).handled(true);
    errorHandler(deadLetterChannel(ERROR_ENDPOINT));
    from(ERROR_ENDPOINT).process(new ErrorHandlingProcessor());
  }

  private static String direct(String destination) {
    return "direct:" + destination;
  }
}
