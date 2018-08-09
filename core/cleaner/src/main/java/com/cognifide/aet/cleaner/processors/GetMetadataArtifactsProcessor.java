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
import com.cognifide.aet.cleaner.context.SuiteAggregationCounter;
import com.cognifide.aet.cleaner.processors.exchange.ReferencedArtifactsMessageBody;
import com.cognifide.aet.cleaner.processors.exchange.SuiteMessageBody;
import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = GetMetadataArtifactsProcessor.class)
public class GetMetadataArtifactsProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetMetadataArtifactsProcessor.class);

  private static final Predicate<Comparator> STEP_RESULTS_WITH_ARTIFACT_ID = new Predicate<Comparator>() {
    @Override
    public boolean apply(Comparator comparator) {
      return comparator.getStepResult() != null && StringUtils
          .isNotBlank(comparator.getStepResult().getArtifactId());
    }
  };

  private static final Function<Comparator, String> COMPARATOR_TO_ARTIFACT_ID = new Function<Comparator, String>() {
    @Nullable
    @Override
    public String apply(Comparator comparator) {
      return comparator.getStepResult().getArtifactId();
    }
  };

  @Override
  @SuppressWarnings("unchecked")
  public void process(Exchange exchange) throws Exception {
    final SuiteMessageBody messageBody = exchange.getIn().getBody(SuiteMessageBody.class);
    final CleanerContext cleanerContext = exchange.getIn()
        .getHeader(CleanerContext.KEY_NAME, CleanerContext.class);

    final Set<String> metatadaArtifacts = new HashSet<>();

    LOGGER.info("Processing suite {}", messageBody.getData());

    for (Test test : messageBody.getData().getTests()) {
      metatadaArtifacts.addAll(traverseTest(test));
    }

    ReferencedArtifactsMessageBody body = new ReferencedArtifactsMessageBody(
        messageBody.getData().getName(),
        messageBody.getDbKey());
    if (messageBody.shouldBeRemoved()) {
      body.setArtifactsToRemove(metatadaArtifacts);
    } else {
      body.setArtifactsToKeep(metatadaArtifacts);
    }

    exchange.getOut().setBody(body);
    exchange.getOut().setHeader(CleanerContext.KEY_NAME, cleanerContext);
    exchange.getOut().setHeader(SuiteAggregationCounter.NAME_KEY, exchange.getIn()
        .getHeader(SuiteAggregationCounter.NAME_KEY, SuiteAggregationCounter.class));
  }

  private Set<String> traverseTest(Test test) {
    Set<String> testArtifacts = new HashSet<>();
    for (Url url : test.getUrls()) {
      for (Step step : url.getSteps()) {
        testArtifacts.addAll(traverseStep(step));
      }
    }
    return testArtifacts;
  }

  private Set<String> traverseStep(Step step) {
    final Set<String> stepArtifacts = new HashSet<>();
    final CollectorStepResult stepResult = step.getStepResult();
    if (stepResult != null && stepResult.getStatus().hasArtifacts()) {
      stepArtifacts.add(stepResult.getArtifactId());
      stepArtifacts.addAll(traverseComparators(step));
    }
    if (step.getPattern() != null) {
      stepArtifacts.add(step.getPattern());
    }
    return stepArtifacts;
  }

  private Set<String> traverseComparators(Step step) {
    Set<String> stepArtifacts = Collections.emptySet();
    if (step.getComparators() != null) {
      stepArtifacts = FluentIterable.from(step.getComparators())
          .filter(STEP_RESULTS_WITH_ARTIFACT_ID)
          .transform(COMPARATOR_TO_ARTIFACT_ID)
          .toSet();
    }
    return stepArtifacts;
  }
}
