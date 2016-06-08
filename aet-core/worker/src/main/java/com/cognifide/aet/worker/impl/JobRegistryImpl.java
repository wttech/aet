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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.datamodifier.DataModifierFactory;
import com.cognifide.aet.worker.api.JobRegistry;

@Service
@Component(immediate = true, description = "AET JMS Registry", label = "AET Job Registry Implementation")
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd") })
public class JobRegistryImpl implements JobRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobRegistryImpl.class);

	@Reference(referenceInterface = CollectorFactory.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, bind = "bindCollectorFactory", unbind = "unbindCollectorFactory")
	private Map<String, CollectorFactory> collectorFactories = new ConcurrentHashMap<>();

	@Reference(referenceInterface = ComparatorFactory.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, bind = "bindComparatorFactory", unbind = "unbindComparatorFactory")
	private Map<String, ComparatorFactory> comparatorFactoryMap = new ConcurrentHashMap<>();

	@Reference(referenceInterface = DataModifierFactory.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, bind = "bindDataModifierFactory", unbind = "unbindDataModifierFactory")
	private Map<String, DataModifierFactory> dataModifierFactoryMap = new ConcurrentHashMap<>();

	private Map<String, ComparatorFactory> defaultComparatorMap = new ConcurrentHashMap<>();

	@Override
	public CollectorFactory getCollectorFactory(String name) {
		return collectorFactories.get(name);
	}

	@Override
	public ComparatorFactory getComparatorFactoryForType(String type) {
		return defaultComparatorMap.get(type);
	}

	@Override
	public ComparatorFactory getComparatorFactory(String name) {
		return comparatorFactoryMap.get(name);
	}

	@Override
	public DataModifierFactory getDataModifierFactory(String name) {
		return dataModifierFactoryMap.get(name);
	}

	@Override
	public boolean hasJob(String name) {
		return collectorFactories.containsKey(name);
	}

	// ######## Binding related methods

	protected void bindCollectorFactory(CollectorFactory collectorFactory) {
		LOGGER.info("Binding collector: {}", collectorFactory.getName());
		collectorFactories.put(collectorFactory.getName(), collectorFactory);
	}

	protected void unbindCollectorFactory(CollectorFactory collectorFactory) {
		LOGGER.info("Unbinding collector: {}", collectorFactory.getName());
		collectorFactories.remove(collectorFactory.getName());
	}

	protected void bindComparatorFactory(ComparatorFactory comparatorFactory) {
		LOGGER.info("Binding comparator: {}", comparatorFactory.getName());
		comparatorFactoryMap.put(comparatorFactory.getName(), comparatorFactory);
		if (comparatorForThisTypeExists(comparatorFactory)) {
			if (hasHighestRanking(comparatorFactory)) {
				ComparatorFactory overridden = defaultComparatorMap.put(comparatorFactory.getType(),
						comparatorFactory);
				if (overridden != null) {
					LOGGER.info("Comparator {} was overridden by {} comparator as default for type {}.",
							overridden.getName(), comparatorFactory.getName(), comparatorFactory.getType());
				}
			}
		} else {
			defaultComparatorMap.put(comparatorFactory.getType(), comparatorFactory);
		}
	}

	protected void unbindComparatorFactory(ComparatorFactory comparatorFactory) {
		LOGGER.info("Unbinding comparator: {}", comparatorFactory.getName());
		comparatorFactoryMap.remove(comparatorFactory.getName());
		if (defaultComparatorMap.get(comparatorFactory.getType()).equals(comparatorFactory)) {
			ComparatorFactory winner = getSuccessorFactory(comparatorFactory);
			if (winner == null) {
				defaultComparatorMap.remove(comparatorFactory.getType());
			} else {
				defaultComparatorMap.put(comparatorFactory.getType(), winner);
			}
		}
	}

	protected void bindDataModifierFactory(DataModifierFactory dataModifierFactory) {
		LOGGER.info("Binding data modifier: {}", dataModifierFactory.getName());
		dataModifierFactoryMap.put(dataModifierFactory.getName(), dataModifierFactory);
	}

	protected void unbindDataModifierFactory(DataModifierFactory dataModifierFactory) {
		LOGGER.info("Unbinding data modifier: {}", dataModifierFactory.getName());
		dataModifierFactoryMap.remove(dataModifierFactory.getName());
	}

	private ComparatorFactory getSuccessorFactory(ComparatorFactory comparatorFactory) {
		int currentRanking = 0;
		ComparatorFactory winner = null;
		for (ComparatorFactory factory : comparatorFactoryMap.values()) {
			if (factory.getType().equals(comparatorFactory.getType())
					&& factory.getRanking() > currentRanking) {
				currentRanking = factory.getRanking();
				winner = factory;
			}
		}
		return winner;
	}

	private boolean hasHighestRanking(ComparatorFactory comparatorFactory) {
		return defaultComparatorMap.get(comparatorFactory.getType()).getRanking() < comparatorFactory
				.getRanking();
	}

	private boolean comparatorForThisTypeExists(ComparatorFactory comparatorFactory) {
		return defaultComparatorMap.containsKey(comparatorFactory.getType());
	}

}
