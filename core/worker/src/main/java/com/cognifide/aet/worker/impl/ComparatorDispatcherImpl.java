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
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.Operation;
import com.cognifide.aet.communication.api.util.ExecutionTimer;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.worker.api.ComparatorDispatcher;
import com.cognifide.aet.worker.api.JobRegistry;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class ComparatorDispatcherImpl implements ComparatorDispatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorDispatcherImpl.class);

  private static final String COMPARATOR_PARAMETER = "comparator";

  @Reference
  private JobRegistry jobRegistry;

  @Override
  public Comparator run(Comparator comparator, ComparatorProperties comparatorProperties)
      throws AETException {
    ComparatorFactory comparatorFactory = getComparatorFactory(comparator);

    final List<DataFilterJob> dataFilterJobs = getComparatorDataFilters(comparator);
    final ComparatorJob comparatorJob = comparatorFactory
        .createInstance(comparator, comparatorProperties, dataFilterJobs);
    LOGGER.info("Starting comparison of {}", comparator);

    ExecutionTimer timer = ExecutionTimer.createAndRun(COMPARATOR_PARAMETER);
    ComparatorStepResult comparisonResult = null;
    try {
      comparisonResult = comparatorJob.compare();
    } catch (AETException e) {
      LOGGER.error(comparator + " throw error:", comparator, e);
      comparisonResult = new ComparatorStepResult(null,
          ComparatorStepResult.Status.PROCESSING_ERROR);
      comparisonResult.addError(getCause(e));
    } finally {
      timer.finishAndLog(comparator.getType());
      comparator.setStepResult(comparisonResult);
      comparator.setStatistics(timer.toStatistics());
    }
    return comparator;
  }

  private List<DataFilterJob> getComparatorDataFilters(Comparator comparator) {
    return FluentIterable.from(comparator.getFilters())
        .transform(new Function<Operation, DataFilterJob>() {
          @Nullable
          @Override
          public DataFilterJob apply(Operation dataFilter) {
            DataFilterJob dataFilterJob = null;
            DataFilterFactory dataFilterFactory = jobRegistry
                .getDataModifierFactory(dataFilter.getType());
            if (dataFilterFactory != null) {
              try {
                dataFilterJob = dataFilterFactory.createInstance(dataFilter.getParameters());
              } catch (ParametersException e) {
                LOGGER.error("Unexpected parameter in {}", dataFilter, e);
              }
            }
            return dataFilterJob;
          }
        }).filter(Predicates.notNull()).toList();
  }

  private ComparatorFactory getComparatorFactory(Comparator comparator) throws ParametersException {
    ComparatorFactory comparatorFactory;
    final String comparatorName = comparator.getParameters().get(COMPARATOR_PARAMETER);
    if (comparatorName != null) {
      comparatorFactory = jobRegistry.getComparatorFactory(comparatorName);
    } else {
      comparatorFactory = jobRegistry.getComparatorFactoryForType(comparator.getType());
    }
    if (comparatorFactory == null) {
      throw new ParametersException(String.format(
          "Can not find comparator for name '%s' and type '%s' while processing: '%s'.",
          comparatorName, comparator.getType(), comparator));
    }
    return comparatorFactory;
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
}
