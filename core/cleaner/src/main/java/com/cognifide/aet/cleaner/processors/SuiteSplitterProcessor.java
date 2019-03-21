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
import com.cognifide.aet.cleaner.context.SuiteAggregationCounter;
import com.cognifide.aet.cleaner.processors.exchange.ProjectMetadataMessageBody;
import com.cognifide.aet.cleaner.processors.exchange.SuiteMessageBody;
import com.cognifide.aet.vs.DBKey;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = SuiteSplitterProcessor.class)
public class SuiteSplitterProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuiteSplitterProcessor.class);

  @Override
  @SuppressWarnings("unchecked")
  public void process(Exchange exchange) throws Exception {
    final CleanerContext cleanerContext = exchange.getIn()
        .getHeader(CleanerContext.KEY_NAME, CleanerContext.class);
    final ProjectMetadataMessageBody allSuites = exchange.getIn().getBody(
        ProjectMetadataMessageBody.class);
    final DBKey dbKey = allSuites.getDbKey();

    final List<SuiteMessageBody> body =
        allSuites.getData().stream().map(suite -> new SuiteMessageBody(suite, dbKey, false))
            .collect(Collectors.toList());

    exchange.getOut().setBody(body);
    exchange.getOut().setHeader(SuiteAggregationCounter.NAME_KEY,
        new SuiteAggregationCounter(allSuites.getData().size()));
    exchange.getOut().setHeader(CleanerContext.KEY_NAME, cleanerContext);
    exchange.getOut().setHeader(DbAggregationCounter.NAME_KEY, exchange.getIn()
        .getHeader(DbAggregationCounter.NAME_KEY, DbAggregationCounter.class));
  }
}
