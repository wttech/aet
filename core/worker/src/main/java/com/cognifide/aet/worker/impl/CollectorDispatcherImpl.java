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
package com.cognifide.aet.worker.impl;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.job.CollectorJobData;
import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.worker.api.CollectorDispatcher;
import com.cognifide.aet.worker.api.JobRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class CollectorDispatcherImpl implements CollectorDispatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectorDispatcherImpl.class);

  @Reference
  private JobRegistry jobRegistry;

  @Override
  public Url run(Url url, CollectorJobData collectorJobData,
      WebCommunicationWrapper webCommunicationWrapper) throws AETException {
    LOGGER.info(
        "Start collection from {}, with {} steps to perform. Company: {} Project: {} TestSuite: {} Test: {}",
        url.getUrl(), collectorJobData.getUrls().size(),
        collectorJobData.getCompany(), collectorJobData.getProject(),
        collectorJobData.getSuiteName(),
        collectorJobData.getTestName());

    int stepNumber = 0;
    int totalSteps = url.getSteps().size();

    for (final Step step : url.getSteps()) {
      stepNumber++;
      LOGGER.debug(
          "Performing collection step {}/{}: {} named {} with parameters: {} from {}. Company: {} Project: {} TestSuite: {} Test: {}",
          stepNumber, totalSteps, step.getType(), step.getName(), step.getParameters(),
          url.getUrl(), collectorJobData.getCompany(), collectorJobData.getProject(),
          collectorJobData.getSuiteName(),
          collectorJobData.getTestName());

      final String urlWithDomain = StringUtils.trimToEmpty(url.getDomain()) + url.getUrl();
      CollectorJob collectorJob = getConfiguredCollector(step, urlWithDomain,
          webCommunicationWrapper, collectorJobData);
      ExecutionTimer timer = ExecutionTimer.createAndRun("collector");
      CollectorStepResult result = null;
      try {
        result = collectorJob.collect();
      } catch (AETException e) {
        final String message = String.format(
            "Step: `%s` (with parameters: %s) thrown an exception when collecting url: %s in: `%s` test. Cause: %s",
            step.getType(), step.getParameters(), url.getUrl(), collectorJobData.getTestName(),
            getCause(e));
        result = CollectorStepResult.newProcessingErrorResult(message);
        LOGGER.error("Failed to perform one of collect steps: {}.", step, e);
      } finally {
        timer.finishAndLog(step.getType());
        step.setStepResult(result);
        step.setStatistics(timer.toStatistics());
      }
    }

    LOGGER.info(
        "All collection steps from {} have been performed! Company: {} Project: {} TestSuite: {} Test: {}",
        url.getUrl(), collectorJobData.getCompany(), collectorJobData.getProject(),
        collectorJobData.getSuiteName(),
        collectorJobData.getTestName());
    return url;
  }

  private String getCause(AETException e) {
    String cause = e.getMessage();
    if (StringUtils.isBlank(cause)) {
      cause = ExceptionUtils.getRootCauseMessage(e);
    }
    if (StringUtils.isBlank(cause)) {
      cause = ExceptionUtils.getStackTrace(e);
    }
    return cause;
  }

  private CollectorJob getConfiguredCollector(Step currentStep, String urlWithDomain,
      WebCommunicationWrapper webCommunicationWrapper, CollectorJobData jobData)
      throws ParametersException {
    final String collectorType = currentStep.getType();
    if (jobRegistry.hasJob(collectorType)) {
      CollectorProperties collectorProperties = new CollectorProperties(urlWithDomain,
          jobData.getCompany(), jobData.getProject(), currentStep.getPattern());
      return jobRegistry.getCollectorFactory(collectorType)
          .createInstance(collectorProperties, currentStep.getParameters(),
              webCommunicationWrapper);
    } else {
      throw new ParametersException(String
          .format("Can not find collector with name '%s' while processing url: '%s'", collectorType,
              urlWithDomain));
    }
  }

}
