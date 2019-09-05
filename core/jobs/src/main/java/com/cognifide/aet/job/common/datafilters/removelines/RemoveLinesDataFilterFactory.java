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
package com.cognifide.aet.job.common.datafilters.removelines;

import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;

import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import org.osgi.service.component.annotations.Component;

@Component
public class RemoveLinesDataFilterFactory implements DataFilterFactory {

  @Override
  public String getName() {
    return RemoveLinesDataModifier.NAME;
  }

  @Override
  public DataFilterJob createInstance(Map<String, String> params) throws ParametersException {
    RemoveLinesDataModifier modifier = new RemoveLinesDataModifier();
    modifier.setParameters(params);
    return modifier;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Remove Lines")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Data Ranges")
                            .tag("dataRanges")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Ranges of lines to remove from data. At least one parameter is required.")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Pattern Ranges")
                            .tag("patternRanges")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Ranges of lines to remove from pattern. At least one parameter is required.")
                            .build()
            )
            .and()
            .withDeps("Source").depType(null)
            .dropTo("source-comparators")
            .group("DataFilters")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/RemoveLinesDataFilter")
            .build();
  }
}
