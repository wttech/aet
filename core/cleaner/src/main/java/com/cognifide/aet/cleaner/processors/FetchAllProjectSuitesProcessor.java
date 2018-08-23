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
import com.cognifide.aet.cleaner.processors.exchange.AllSuiteVersionsMessageBody;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = FetchAllProjectSuitesProcessor.class)
public class FetchAllProjectSuitesProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FetchAllProjectSuitesProcessor.class);

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

    final ImmutableListMultimap<String, Suite> groupedSuites =
        FluentIterable.from(allProjectSuites).index(new Function<Suite, String>() {
          @Override
          public String apply(Suite suite) {
            return suite.getName();
          }
        });

    LOGGER.info("Found {} distinct suites in {}", groupedSuites.keySet().size(), dbKey);

    final ImmutableList<AllSuiteVersionsMessageBody> body =
        FluentIterable.from(groupedSuites.asMap().entrySet()).transform(
            new Function<Map.Entry<String, Collection<Suite>>, AllSuiteVersionsMessageBody>() {
              @Override
              public AllSuiteVersionsMessageBody apply(Map.Entry<String, Collection<Suite>> input) {
                return new AllSuiteVersionsMessageBody(input.getKey(), dbKey, input.getValue());
              }
            }).toList();

    exchange.getOut().setBody(body);
    exchange.getOut().setHeader(CleanerContext.KEY_NAME, cleanerContext);
  }


}
