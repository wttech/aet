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
import com.cognifide.aet.cleaner.processors.filters.DBKeyProjectCompanyPredicate;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query for all databases and passes them to split.
 */
@Component(service = StartMetadataCleanupProcessor.class)
public class StartMetadataCleanupProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartMetadataCleanupProcessor.class);

  private static final Function<String, DBKey> DB_NAMES_TO_DB_KEYS = new Function<String, DBKey>() {
    @Override
    public DBKey apply(String dbName) {
      return new SimpleDBKey(MongoDBClient.getCompanyNameFromDbName(dbName),
          MongoDBClient.getProjectNameFromDbName(dbName));
    }
  };

  @Reference
  private transient MongoDBClient client;

  @Override
  public void process(Exchange exchange) throws Exception {
    LOGGER.info("Start processing cleaning workflow.");

    final CleanerContext cleanerContext = exchange.getIn().getBody(CleanerContext.class);
    final String companyFilter = cleanerContext.getCompanyFilter();
    final String projectFilter = cleanerContext.getProjectFilter();

    final Collection<String> dbNames = client.getAetsDBNames();
    final DBKeyProjectCompanyPredicate predicate = new DBKeyProjectCompanyPredicate(companyFilter,
        projectFilter);

    final ImmutableList<DBKey> dbKeys = FluentIterable.from(dbNames)
        .transform(DB_NAMES_TO_DB_KEYS)
        .filter(predicate)
        .toList();

    LOGGER.info("Found {} databases matching criteria {}.", dbKeys.size(), predicate);
    exchange.getOut().setHeader(CleanerContext.KEY_NAME, cleanerContext);
    exchange.getOut().setBody(dbKeys);
  }

}
