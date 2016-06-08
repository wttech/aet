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

import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.config.ComparatorStep;
import com.cognifide.aet.communication.api.config.DataModifierStep;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.job.ComparatorJobData;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierFactory;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.VersionStorage;
import com.cognifide.aet.worker.api.ComparatorDispatcher;
import com.cognifide.aet.worker.api.JobRegistry;
import com.google.common.collect.Lists;

@Service
@Component(label = "AET Comparator Dispatcher", description = "Comparator Dispatcher", immediate = true)
public class ComparatorDispatcherImpl implements ComparatorDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorDispatcherImpl.class);

	@Reference
	private VersionStorage versionStorage;

	@Reference
	private JobRegistry jobRegistry;

	@Override
	public NodeMetadata run(final ComparatorJobData comparatorJobData) throws AETException {
		ComparatorStep comparatorStep = comparatorJobData.getComparatorStep();

		CollectMetadata collectorNodeMetadata = comparatorJobData.getCollectorNodeMetadata();

		ComparatorFactory comparatorFactory = getComparatorFactory(comparatorStep, collectorNodeMetadata);

		String compareModuleId = comparatorFactory.getName();
		CompareMetadata comparatorNodeMetadata = CompareMetadata.fromCollectMetadata(collectorNodeMetadata,
				compareModuleId, comparatorStep.getName());

		ComparatorProperties comparatorProperties = new ComparatorProperties(comparatorNodeMetadata);

		List<DataModifierJob> dataModifierJobs = Lists.newArrayList();
		if (comparatorStep.getDataModifierSteps() != null) {
			for (DataModifierStep dms : comparatorStep.getDataModifierSteps()) {
				DataModifierFactory dataModifierFactory = jobRegistry.getDataModifierFactory(dms.getName());
				dataModifierJobs.add(dataModifierFactory.createInstance(dms.getParameters()));
			}
		}

		LOGGER.info(
				"Creating comparator with {} DataModifiers. ComparatorModule: {} ComparatorName: {} Company: {} TestSuite: {} TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {}",
				dataModifierJobs.size(), comparatorNodeMetadata.getComparatorModule(),
				comparatorNodeMetadata.getComparatorModuleName(), comparatorNodeMetadata.getCompany(),
				comparatorNodeMetadata.getTestSuiteName(), comparatorNodeMetadata.getTestName(),
				comparatorNodeMetadata.getUrlName(), comparatorNodeMetadata.getUrl(),
				comparatorNodeMetadata.getCollectorModule(), comparatorNodeMetadata.getCollectorModuleName());
		ComparatorJob comparator = comparatorFactory.createInstance(
				versionStorage.getDataNode(collectorNodeMetadata),
				versionStorage.getPatternNode(PatternMetadata.fromUrlNodeMetadata(collectorNodeMetadata)),
				versionStorage.getResultNode(comparatorNodeMetadata), comparatorStep.getParameters(),
				comparatorProperties, dataModifierJobs);

		ExecutionTimer timer = ExecutionTimer.createAndRun("comparator");
		try {
			if (BooleanUtils.isFalse(comparator.compare())) {
				versionStorage.setPatternCandidate(collectorNodeMetadata);
			}
		} catch (AETException e) {
			throw new AETException(
					String.format(
							"ComparatorJob: %s named %s with parameters: %s thrown exception. TestName: %s UrlName: %s Url: %s CollectorModule: %s CollectorModuleName: %s. Cause: %s",
							comparatorNodeMetadata.getComparatorModule(),
							comparatorNodeMetadata.getComparatorModuleName(), comparatorStep.getParameters(),
							comparatorNodeMetadata.getTestName(), comparatorNodeMetadata.getUrlName(),
							comparatorNodeMetadata.getUrl(), comparatorNodeMetadata.getCollectorModule(),
							comparatorNodeMetadata.getCollectorModuleName(), e.getMessage()), e);
		} finally {
			timer.finishAndLog(compareModuleId);
		}

		return comparatorNodeMetadata;
	}

	private ComparatorFactory getComparatorFactory(ComparatorStep comparatorStep,
			CollectMetadata collectorNodeMetadata) throws ParametersException {
		ComparatorFactory comparatorFactory;
		if (comparatorStep.isUseDefault()) {
			comparatorFactory = jobRegistry.getComparatorFactoryForType(collectorNodeMetadata
					.getCollectorModule());
		} else {
			comparatorFactory = jobRegistry.getComparatorFactory(comparatorStep.getModule());
		}
		if (comparatorFactory == null) {
			throw new ParametersException(String.format(
					"Can not find comparator for module '%s' while processing data type: '%s'.",
					comparatorStep.getModule(), comparatorStep.getType()));
		}
		return comparatorFactory;
	}

}
