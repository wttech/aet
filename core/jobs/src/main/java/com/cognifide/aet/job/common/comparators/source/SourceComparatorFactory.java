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
package com.cognifide.aet.job.common.comparators.source;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.common.comparators.source.diff.DiffParser;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class SourceComparatorFactory implements ComparatorFactory {

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  public final String getType() {
    return SourceComparator.COMPARATOR_TYPE;
  }

  @Override
  public final String getName() {
    return SourceComparator.COMPARATOR_NAME;
  }

  @Override
  public final int getRanking() {
    return DEFAULT_COMPARATOR_RANKING;
  }

  @Override
  public ComparatorJob createInstance(Comparator comparator,
      ComparatorProperties comparatorProperties, List<DataFilterJob> dataFilterJobs)
      throws ParametersException {
    final SourceComparator sourceComparator = new SourceComparator(artifactsDAO,
        comparatorProperties, new DiffParser(), dataFilterJobs);
    sourceComparator.setParameters(comparator.getParameters());
    return sourceComparator;
  }
}
