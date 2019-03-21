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
package com.cognifide.aet.cleaner.processors;

import com.cognifide.aet.cleaner.context.CleanerContext;
import com.cognifide.aet.cleaner.context.DbAggregationCounter;
import com.cognifide.aet.cleaner.processors.exchange.ProjectMetadataMessageBody;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = FetchProjectMetadataProcessor.class)
public class FetchProjectMetadataProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FetchProjectMetadataProcessor.class);

  @Reference
  private MetadataDAO metadataDAO;

  @Override
  @SuppressWarnings("unchecked")
  public void process(Exchange exchange) throws Exception {
    final CleanerContext cleanerContext = exchange.getIn()
        .getHeader(CleanerContext.KEY_NAME, CleanerContext.class);
    final DBKey dbKey = exchange.getIn().getBody(DBKey.class);
    LOGGER.info("Querying for unused data in {}", dbKey);

    final List<Suite> allProjectSuites = metadataDAO.listSuites(dbKey);
    final ProjectMetadataMessageBody messageBody = new ProjectMetadataMessageBody(
        allProjectSuites, dbKey);

    exchange.getOut().setBody(messageBody);
    exchange.getOut().setHeader(CleanerContext.KEY_NAME, cleanerContext);
    exchange.getOut().setHeader(DbAggregationCounter.NAME_KEY, exchange.getIn()
        .getHeader(DbAggregationCounter.NAME_KEY, DbAggregationCounter.class));
  }


}
