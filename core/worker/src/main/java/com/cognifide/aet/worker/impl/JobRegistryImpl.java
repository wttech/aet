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

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import com.cognifide.aet.worker.api.JobRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    property = {Constants.SERVICE_VENDOR + "=Cognifide Ltd"}
)
public class JobRegistryImpl implements JobRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRegistryImpl.class);

  private Map<String, CollectorFactory> collectorFactories = new ConcurrentHashMap<>();

  private Map<String, ComparatorFactory> comparatorFactoryMap = new ConcurrentHashMap<>();

  private Map<String, DataFilterFactory> dataModifierFactoryMap = new ConcurrentHashMap<>();

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
  public DataFilterFactory getDataModifierFactory(String name) {
    return dataModifierFactoryMap.get(name);
  }

  @Override
  public boolean hasJob(String name) {
    return collectorFactories.containsKey(name);
  }

  // ######## Binding related methods

  @Reference(
      service = CollectorFactory.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE)
  protected void bindCollectorFactory(CollectorFactory collectorFactory) {
    LOGGER.info("Binding collector: {}", collectorFactory.getName());
    collectorFactories.put(collectorFactory.getName(), collectorFactory);
  }

  protected void unbindCollectorFactory(CollectorFactory collectorFactory) {
    LOGGER.info("Unbinding collector: {}", collectorFactory.getName());
    collectorFactories.remove(collectorFactory.getName());
  }

  @Reference(
      service = ComparatorFactory.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE)
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

  @Reference(
      service = DataFilterFactory.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE)
  protected void bindDataModifierFactory(DataFilterFactory dataFilterFactory) {
    LOGGER.info("Binding data modifier: {}", dataFilterFactory.getName());
    dataModifierFactoryMap.put(dataFilterFactory.getName(), dataFilterFactory);
  }

  protected void unbindDataModifierFactory(DataFilterFactory dataFilterFactory) {
    LOGGER.info("Unbinding data modifier: {}", dataFilterFactory.getName());
    dataModifierFactoryMap.remove(dataFilterFactory.getName());
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
