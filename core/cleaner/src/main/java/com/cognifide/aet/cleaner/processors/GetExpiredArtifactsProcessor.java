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

import com.cognifide.aet.cleaner.processors.exchange.ArtifactsToRemoveMessageBody;
import com.cognifide.aet.cleaner.processors.exchange.ReferencedArtifactsMessageBody;
import com.google.common.collect.Sets;
import java.util.HashSet;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osgi.service.component.annotations.Component;

@Component(service = GetExpiredArtifactsProcessor.class)
public class GetExpiredArtifactsProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    final ReferencedArtifactsMessageBody messageBody = exchange.getIn()
        .getBody(ReferencedArtifactsMessageBody.class);

    final HashSet<String> artifactsToRemove = getArtifactsIdsToRemove(messageBody);

    ArtifactsToRemoveMessageBody body = new ArtifactsToRemoveMessageBody(artifactsToRemove,
        messageBody.getDbKey());

    exchange.getOut().setBody(body);
    exchange.getOut().setHeaders(exchange.getIn().getHeaders());
  }


  HashSet<String> getArtifactsIdsToRemove(ReferencedArtifactsMessageBody messageBody) {
    return new HashSet<>(
        Sets.difference(messageBody.getArtifactsToRemove(), messageBody.getArtifactsToKeep()));
  }
}
