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

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.job.CollectorJobData;
import com.cognifide.aet.communication.api.job.CollectorResultData;
import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.worker.api.CollectorDispatcher;
import com.cognifide.aet.worker.drivers.WebDriverProvider;
import com.cognifide.aet.worker.exceptions.WorkerException;
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
    service = CollectorMessageListenerImpl.class,
    immediate = true)
@Designate(ocd = CollectorMessageListenerImplConfig.class, factory = true)
public class CollectorMessageListenerImpl extends AbstractTaskMessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectorMessageListenerImpl.class);

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private CollectorDispatcher dispatcher;

  @Reference
  private WebDriverProvider webDriverProvider;

  private CollectorMessageListenerImplConfig config;

  @Activate
  void activate(CollectorMessageListenerImplConfig config) {
    this.config = config;
    super.doActivate(config.consumerQueueName(), config.producerQueueName(), config.pf());
  }

  @Deactivate
  void deactivate() {
    super.doDeactivate();
  }

  @Override
  public void onMessage(final Message message) {
    CollectorJobData collectorJobData = null;
    try {
      collectorJobData = JmsUtils.getFromMessage(message, CollectorJobData.class);
    } catch (JMSException e) {
      LOGGER.error("Invalid message obtained!", e);
    }
    String correlationId = JmsUtils.getJMSCorrelationID(message);
    String requestMessageId = JmsUtils.getJMSMessageID(message);
    if (collectorJobData != null && StringUtils.isNotBlank(correlationId)
        && requestMessageId != null) {
      LOGGER.info(
          "CollectorJobData [{}] message arrived with {} urls. CorrelationId: {} RequestMessageId: {}",
          config.name(), collectorJobData.getUrls().size(), correlationId,
          requestMessageId);
      WebCommunicationWrapper webCommunicationWrapper = null;
      int collected = 0;
      String preferredWebDriver = collectorJobData.getPreferredBrowserId();
      try {
        if (isProxyUsed(collectorJobData.getProxy())) {
          webCommunicationWrapper = this.webDriverProvider
              .createWebDriverWithProxy(preferredWebDriver, collectorJobData.getProxy());
        } else {
          webCommunicationWrapper = this.webDriverProvider.createWebDriver(preferredWebDriver);
        }
        collected = runUrls(collectorJobData, requestMessageId, webCommunicationWrapper,
            correlationId);

      } catch (WorkerException e) {
        for (Url url : collectorJobData.getUrls()) {
          String errorMessage = String.format(
              "Couldn't process following url `%s` because of error: %s", url.getUrl(),
              e.getMessage());
          LOGGER.error(errorMessage, e);
          // updates all steps with worker exception
          for (final Step step : url.getSteps()) {
            CollectorStepResult result = CollectorStepResult.newProcessingErrorResult(errorMessage);
            step.setStepResult(result);
          }
          // updates feedback queue
          CollectorResultData collectorResultData = CollectorResultData.createErrorResult(
              url, ProcessingError.collectingError(errorMessage), requestMessageId,
              collectorJobData.getTestName());
          feedbackQueue.sendObjectMessageWithCorrelationID(collectorResultData, correlationId);
        }
      } finally {
        quitWebDriver(webCommunicationWrapper);
      }
      LOGGER.info("Successfully collected from {}/{} urls.", collected,
          collectorJobData.getUrls().size());
    }

  }

  private int runUrls(CollectorJobData collectorJobData, String requestMessageId,
      WebCommunicationWrapper webCommunicationWrapper, String correlationId) {
    int result = 0;
    for (Url url : collectorJobData.getUrls()) {
      try {
        ExecutionTimer timer = ExecutionTimer.createAndRun("url");
        final Url processedUrl = dispatcher.run(url, collectorJobData, webCommunicationWrapper);

        final CollectorResultData collectorResultData = CollectorResultData
            .createSuccessResult(processedUrl, requestMessageId,
                collectorJobData.getTestName());
        result++;
        timer.finishAndLog(processedUrl.getUrl());
        processedUrl.setCollectionStats(timer.toStatistics());
        feedbackQueue.sendObjectMessageWithCorrelationID(collectorResultData, correlationId);
      } catch (Exception e) {
        LOGGER.error("Unrecognized collector error", e);
        final String message = "Unrecognized collector error: " + e.getMessage();

        CollectorStepResult collectorStepProcessingError =
            CollectorStepResult.newProcessingErrorResult(message);
        for (Step step : url.getSteps()) {
          step.setStepResult(collectorStepProcessingError);
        }

        CollectorResultData collectorResultData = CollectorResultData.createErrorResult(
            url, ProcessingError.collectingError(message), requestMessageId,
            collectorJobData.getTestName());
        feedbackQueue.sendObjectMessageWithCorrelationID(collectorResultData, correlationId);
      }
    }
    return result;
  }

  private boolean isProxyUsed(String useProxy) {
    return StringUtils.isNotBlank(useProxy);
  }

  private void quitWebDriver(WebCommunicationWrapper webCommunicationWrapper) {
    if (webCommunicationWrapper != null) {
      webCommunicationWrapper.getWebDriver().quit();
      if (webCommunicationWrapper.isUseProxy()) {
        try {
          webCommunicationWrapper.getProxyServer().stop();
        } catch (Exception e) {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }
  }

  @Override
  protected JmsConnection getJmsConnection() {
    return jmsConnection;
  }

}
