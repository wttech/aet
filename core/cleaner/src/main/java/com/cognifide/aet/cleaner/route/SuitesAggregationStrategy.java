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

import com.cognifide.aet.cleaner.context.CleanerContext;
import com.cognifide.aet.cleaner.processors.exchange.ReferencedArtifactsMessageBody;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SuitesAggregationStrategy implements CompletionAwareAggregationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuitesAggregationStrategy.class);

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    final Exchange aggregatedExchange;
    final ReferencedArtifactsMessageBody newBody = newExchange.getIn()
        .getBody(ReferencedArtifactsMessageBody.class);
    if (isFirstAggregation(oldExchange)) {
      aggregatedExchange = newExchange;
    } else {
      final ReferencedArtifactsMessageBody oldBody = oldExchange.getIn()
          .getBody(ReferencedArtifactsMessageBody.class);

      newBody.update(oldBody);

      oldExchange.getIn().setBody(newBody);
      oldExchange.getIn().setHeader(CleanerContext.KEY_NAME,
          newExchange.getIn().getHeader(CleanerContext.KEY_NAME, CleanerContext.class));

      aggregatedExchange = oldExchange;
    }
    LOGGER.debug("Aggregated suite version for `{}` in {}.", newBody.getData(), newBody.getDbKey());
    return aggregatedExchange;
  }

  private boolean isFirstAggregation(Exchange oldExchange) {
    return oldExchange == null;
  }

  @Override
  public void onCompletion(Exchange exchange) {
    ReferencedArtifactsMessageBody body = exchange.getIn()
        .getBody(ReferencedArtifactsMessageBody.class);
    LOGGER.debug("Finished aggregating {}", body.getId());
  }
}
