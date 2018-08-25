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
import com.cognifide.aet.cleaner.processors.exchange.ReferencedArtifactsMessageBody;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.common.collect.Sets;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = RemoveArtifactsProcessor.class)
public class RemoveArtifactsProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveArtifactsProcessor.class);

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  @SuppressWarnings("unchecked")
  public void process(Exchange exchange) throws Exception {
    final CleanerContext cleanerContext = exchange.getIn()
        .getHeader(CleanerContext.KEY_NAME, CleanerContext.class);
    final ReferencedArtifactsMessageBody messageBody = exchange.getIn()
        .getBody(ReferencedArtifactsMessageBody.class);

    final Sets.SetView<String> artifactsToRemove =
        Sets.difference(messageBody.getArtifactsToRemove(), messageBody.getArtifactsToKeep());

    LOGGER.debug("Artifacts that will be removed: {}", artifactsToRemove);
    if (!cleanerContext.isDryRun()) {
      LOGGER.info("{} unreferenced artifacts will be removed from {} after cleaning suite `{}`",
          artifactsToRemove.size(), messageBody.getDbKey(), messageBody.getData());
      artifactsDAO.removeArtifacts(messageBody.getDbKey(), artifactsToRemove);
      LOGGER.info("{} artifacts removed successfully!", artifactsToRemove.size());
    } else {
      LOGGER.info(
          "Dry run completed! {} unreferenced artifacts should be removed from {} after cleaning suite `{}`",
          artifactsToRemove.size(), messageBody.getDbKey(), messageBody.getData());
    }
  }
}
