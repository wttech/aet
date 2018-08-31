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
package com.cognifide.aet.worker.listeners;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.job.ComparatorJobData;
import com.cognifide.aet.communication.api.job.ComparatorResultData;
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.worker.api.ComparatorDispatcher;
import javax.jms.JMSException;
import javax.jms.Message;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ComparatorMessageListenerImpl.class,
    immediate = true)
@Designate(ocd = ComparatorMessageListenerImplConfig.class, factory = true)
public class ComparatorMessageListenerImpl extends AbstractTaskMessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorMessageListenerImpl.class);

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private ComparatorDispatcher dispatcher;

  private ComparatorMessageListenerImplConfig config;

  @Activate
  void activate(ComparatorMessageListenerImplConfig config) {
    this.config = config;
    super.doActivate(config.consumerQueueName(), config.producerQueueName(), config.pf());
  }

  @Deactivate
  void deactivate() {
    super.doDeactivate();
  }

  @Override
  public void onMessage(final Message message) {
    ComparatorJobData comparatorJobData = null;
    try {
      comparatorJobData = JmsUtils.getFromMessage(message, ComparatorJobData.class);
    } catch (JMSException e) {
      LOGGER.error("Invalid message obtained!", e);
    }
    String jmsCorrelationId = JmsUtils.getJMSCorrelationID(message);

    if (comparatorJobData != null && StringUtils.isNotBlank(jmsCorrelationId)) {
      LOGGER.info(
          "ComparatorJobData [{}] message arrived. CorrelationId: {} TestName: {} UrlName: {}",
          comparatorJobData,
          jmsCorrelationId,
          comparatorJobData.getTestName(),
          comparatorJobData.getUrlName());
      final Step step = comparatorJobData.getStep();
      final ComparatorProperties properties = new ComparatorProperties(
          comparatorJobData.getCompany(),
          comparatorJobData.getProject(), step.getPattern(), step.getStepResult().getArtifactId(), step.getStepResult().getPayload());

      for (Comparator comparator : step.getComparators()) {
        LOGGER.info("Start comparison for comparator {} in step {}", comparator, step);
        ComparatorResultData.Builder resultBuilder = ComparatorResultData
            .newBuilder(comparatorJobData.getTestName(), comparatorJobData.getUrlName(),
                step.getIndex());
        try {
          Comparator processedComparator = dispatcher.run(comparator, properties);
          LOGGER.info(
              "Comparison successfully ended. CorrelationId: {} TestName: {} Url: {} Comparator: {}",
              jmsCorrelationId,
              comparatorJobData.getTestName(),
              comparatorJobData.getUrlName(),
              comparator);
          resultBuilder.withComparisonResult(processedComparator)
              .withStatus(JobStatus.SUCCESS);
        } catch (Exception e) {
          LOGGER.error("Exception during compare. CorrelationId: {}", jmsCorrelationId, e);
          final ComparatorStepResult errorResult =
              new ComparatorStepResult(null, ComparatorStepResult.Status.PROCESSING_ERROR);
          errorResult.addError(e.getMessage());
          comparator.setStepResult(errorResult);
          resultBuilder.withStatus(JobStatus.ERROR)
              .withComparisonResult(comparator)
              .withProcessingError(ProcessingError.comparingError(e.getMessage()));
        }
        feedbackQueue.sendObjectMessageWithCorrelationID(resultBuilder.build(), jmsCorrelationId);
      }

    }
  }

  @Override
  protected JmsConnection getJmsConnection() {
    return jmsConnection;
  }

}
