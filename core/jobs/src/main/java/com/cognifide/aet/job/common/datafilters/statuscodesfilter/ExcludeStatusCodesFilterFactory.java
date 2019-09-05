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
package com.cognifide.aet.job.common.datafilters.statuscodesfilter;

import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCodesCollectorResult;
import java.util.Map;
import org.osgi.service.component.annotations.Component;

@Component
public class ExcludeStatusCodesFilterFactory implements DataFilterFactory {

  @Override
  public String getName() {
    return ExcludeStatusCodesFilter.NAME;
  }

  @Override
  public DataFilterJob<StatusCodesCollectorResult> createInstance(Map<String, String> params)
      throws ParametersException {
    ExcludeStatusCodesFilter filter = new ExcludeStatusCodesFilter();
    filter.setParameters(params);
    return filter;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Exclude")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("URL")
                            .tag("url")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Exact url to be removed from results. At least one parameter is required.")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Pattern")
                            .tag("pattern")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Regex pattern that urls should match to be removed from results. At least one parameter is required.")
                            .build()
            )
            .and()
            .withDeps("StatusCodes").depType(null)
            .dropTo("statuscodes-comparators")
            .group("DataFilters")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/StatusCodesDataFilters")
            .build();
  }
}
