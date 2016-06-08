/*
 * Cognifide AET :: Worker
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.worker.impl;

import java.util.List;

import com.cognifide.aet.communication.api.node.PatternMetadata;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.config.CollectorStep;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.job.CollectorJobData;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.VersionStorage;
import com.cognifide.aet.worker.api.CollectorDispatcher;
import com.cognifide.aet.worker.api.JobRegistry;
import com.google.common.collect.Lists;

@Service
@Component(immediate = true, label = "AET Collector Dispatcher", description = "Collector Dispatcher")
public class CollectorDispatcherImpl implements CollectorDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectorDispatcherImpl.class);

	@Reference
	private JobRegistry jobRegistry;

	@Reference
	private VersionStorage versionStorage;

	@Override
	public List<CollectMetadata> run(CollectMetadata nodeMetadata, CollectorJobData collectorJobData,
			WebCommunicationWrapper webCommunicationWrapper) throws AETException {
		LOGGER.info(
				"Start collection from {}, with {} steps to perform. Company: {} Project: {} TestSuite: {} Test: {}",
				nodeMetadata.getUrl(), collectorJobData.getCollectorSteps().size(),
				nodeMetadata.getCompany(), nodeMetadata.getProject(), nodeMetadata.getTestSuiteName(),
				nodeMetadata.getTestName());

		List<CollectMetadata> results = Lists.newArrayList();

		int stepNumber = 0;
		int totalSteps = collectorJobData.getCollectorSteps().size();
		for (final CollectorStep step : collectorJobData.getCollectorSteps()) {
			stepNumber++;
			LOGGER.debug(
					"Performing collection step {}/{}: {} named {} with parameters: {} from {}. Company: {} Project: {} TestSuite: {} Test: {}",
					stepNumber, totalSteps, step.getModule(), step.getName(), step.getParameters(),
					nodeMetadata.getUrl(), nodeMetadata.getCompany(), nodeMetadata.getProject(),
					nodeMetadata.getTestSuiteName(), nodeMetadata.getTestName());

			CollectMetadata collectNodeMetadata = CollectMetadata.fromUrlNodeMetadata(nodeMetadata,
					step.getModule(), step.getName());

			CollectorJob collectorJob = getConfiguredCollector(step, webCommunicationWrapper,
					collectNodeMetadata);
			ExecutionTimer timer = ExecutionTimer.createAndRun("collector");
			boolean collector;
			try {
				collector = collectorJob.collect();
			} catch (AETException e) {
				throw new AETException(
						String.format(
								"CollectionStep: %s named %s with parameters: %s thrown exception. TestName: %s UrlName: %s Url: %s. Cause: %s",
								step.getModule(), step.getName(), step.getParameters(),
								nodeMetadata.getTestName(), nodeMetadata.getUrlName(), nodeMetadata.getUrl(), e.getMessage()),
						e);
			} finally {
				timer.finishAndLog(step.getModule());
			}
			if (collector) {
				results.add(collectNodeMetadata);
			}
		}

		LOGGER.info(
				"All collection steps from {} have been performed! Company: {} Project: {} TestSuite: {} Test: {}",
				nodeMetadata.getUrl(), nodeMetadata.getCompany(), nodeMetadata.getProject(),
				nodeMetadata.getTestSuiteName(), nodeMetadata.getTestName());
		return results;
	}

	private CollectorJob getConfiguredCollector(final CollectorStep step,
			WebCommunicationWrapper webCommunicationWrapper, CollectMetadata collectNodeMetadata)
			throws ParametersException {
		if (jobRegistry.hasJob(step.getModule())) {
			CollectorProperties collectorProperties = new CollectorProperties(collectNodeMetadata.getUrl(),
					collectNodeMetadata.getUrlName());
			return jobRegistry.getCollectorFactory(step.getModule()).createInstance(
					versionStorage.getDataNode(collectNodeMetadata),
					versionStorage.getPatternNode(PatternMetadata.fromUrlNodeMetadata(collectNodeMetadata)), step.getParameters(),
					webCommunicationWrapper, collectorProperties);
		} else {
			throw new ParametersException(String.format(
					"Can not find collector with name '%s' while processing url: '%s'", step.getModule(),
					collectNodeMetadata.getUrl()));
		}
	}

}
