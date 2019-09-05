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
package com.cognifide.aet.job.common.datafilters.jserrorsfilter;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;
import java.util.Set;

import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import org.osgi.service.component.annotations.Component;

@Component
public class JsErrorsFilterFactory implements DataFilterFactory {

  @Override
  public String getName() {
    return JsErrorsFilter.NAME;
  }

  @Override
  public DataFilterJob<Set<JsErrorLog>> createInstance(Map<String, String> params)
      throws ParametersException {
    JsErrorsFilter filter = new JsErrorsFilter();
    filter.setParameters(params);
    return filter;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("JSErrors")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Error")
                            .tag("error")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Exact error message. At least one parameter is required.")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Source")
                            .tag("source")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Source file name in which error occurred. At least one parameter is required.")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Error Pattern")
                            .tag("errorPattern")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Regular expression that matches message text of issue to be filter out. At least one parameter is required.")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Line")
                            .tag("line")
                            .withoutValues()
                            .isMandatory(false)
                            .description("Line number in file in which error occurred. At least one parameter is required.")
                            .build()
            )
            .and()
            .withDeps("JSError").depType(null)
            .dropTo("jserrors-comparators")
            .group("DataFilters")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/JSErrorsDataFilter")
            .build();
  }
}
