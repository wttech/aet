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
import com.cognifide.aet.cleaner.processors.exchange.SuiteMessageBody;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.vs.MetadataDAO;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = RemoveMetadataProcessor.class)
public class RemoveMetadataProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveMetadataProcessor.class);

  @Reference
  private MetadataDAO metadataDAO;

  @Override
  @SuppressWarnings("unchecked")
  public void process(Exchange exchange) throws Exception {
    final CleanerContext cleanerContext = exchange.getIn()
        .getHeader(CleanerContext.KEY_NAME, CleanerContext.class);
    final SuiteMessageBody messageBody = exchange.getIn().getBody(SuiteMessageBody.class);
    final Suite suiteToRemove = messageBody.getData();
    if (!cleanerContext.isDryRun()) {
      LOGGER.info("Removing suite {}.", suiteToRemove);
      metadataDAO.removeSuite(messageBody.getDbKey(), suiteToRemove.getCorrelationId(),
          suiteToRemove.getVersion());
    } else {
      LOGGER.info("Dry run completed! Suites to remove: {}", suiteToRemove);
    }
  }
}
