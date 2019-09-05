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
package com.cognifide.aet.job.common.comparators.w3chtml5;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.job.api.info.ParameterMetadata;
import com.cognifide.aet.job.common.comparators.w3chtml5.parser.W3cHtml5ValidationResultParser;
import com.cognifide.aet.job.common.comparators.w3chtml5.wrapper.NuValidatorWrapper;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class W3cHtml5ComparatorFactory implements ComparatorFactory {

  private final W3cHtml5ValidationResultParser resultParser = new W3cHtml5ValidationResultParser();

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  public String getType() {
    return W3cHtml5Comparator.COMPARATOR_TYPE;
  }

  @Override
  public String getName() {
    return W3cHtml5Comparator.COMPARATOR_NAME;
  }

  @Override
  public int getRanking() {
    return 70;
  }

  @Override
  public ComparatorJob createInstance(Comparator comparator,
      ComparatorProperties comparatorProperties,
      List<DataFilterJob> dataFilterJobs) throws ParametersException {
    W3cHtml5Comparator w3cHtml5Comparator = new W3cHtml5Comparator(artifactsDAO,
        comparatorProperties,
        new NuValidatorWrapper(), resultParser, dataFilterJobs);
    w3cHtml5Comparator.setParameters(comparator.getParameters());
    return w3cHtml5Comparator;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Source W3CHTML5")
            .tag("source")
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Comparator")
                            .tag("comparator")
                            .withValues()
                            .addValue("w3c-html5")
                            .and().defaultValue("w3c-html5")
                            .isMandatory(true)
                            .description("See wiki for specific information")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Ignore Warnings")
                            .tag("ignore-warnings")
                            .withValues()
                            .addValue("true")
                            .addValue("false")
                            .and().defaultValue(null)
                            .isMandatory(false)
                            .description("If set to true the test status does not depend on the warnings amount.")
                            .build()
            )
            .and()
            .withDeps("source-collectors").depType("Error")
            .dropTo("Comparators")
            .group("Comparators")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/W3CHTML5Comparator")
            .build();
  }
}
