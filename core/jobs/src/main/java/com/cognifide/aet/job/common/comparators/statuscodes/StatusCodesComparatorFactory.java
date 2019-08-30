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
package com.cognifide.aet.job.common.comparators.statuscodes;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class StatusCodesComparatorFactory implements ComparatorFactory {

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  public String getType() {
    return StatusCodesComparator.COMPARATOR_TYPE;
  }

  @Override
  public String getName() {
    return StatusCodesComparator.COMPARATOR_NAME;
  }

  @Override
  public int getRanking() {
    return DEFAULT_COMPARATOR_RANKING;
  }


  @Override
  public ComparatorJob createInstance(Comparator comparator,
      ComparatorProperties comparatorProperties, List<DataFilterJob> dataFilterJobs)
      throws ParametersException {
    final StatusCodesComparator statusCodesComparator = new StatusCodesComparator(artifactsDAO,
        comparatorProperties, dataFilterJobs);

    statusCodesComparator.setParameters(comparator.getParameters());
    return statusCodesComparator;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("StatusCodes")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Filter Range")
                            .tag("filterRange")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Defines range of status codes that should be processed, example values: [400, 500]. Mandatory if filterCodes is not set")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Filter Codes")
                            .tag("filterCodes")
                            .withoutValues()
                            .isMandatory(false)
                            .description("List of status codes that should be processed, example values: [400, 401, 404]. Mandatory if filterRange is not set")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Show Excluded")
                            .tag("showExcluded")
                            .withValues()
                            .addValue("true")
                            .addValue("false")
                            .and().defaultValue("true")
                            .isMandatory(false)
                            .description("Flag that says if excluded codes ")
                            .build()
            )
            .and()
            .withDeps("statuscodes-collectors").depType("Error")
            .dropTo("Comparators")
            .group("Comparators")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/StatusCodesComparator")
            .build();
  }
}
