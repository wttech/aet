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
package com.cognifide.aet.job.common.comparators.accessibility;

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
public class AccessibilityComparatorFactory implements ComparatorFactory {

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  public String getType() {
    return AccessibilityComparator.TYPE;
  }

  @Override
  public String getName() {
    return AccessibilityComparator.NAME;
  }

  @Override
  public int getRanking() {
    return 100;
  }

  @Override
  public ComparatorJob createInstance(Comparator comparator,
      ComparatorProperties comparatorProperties, List<DataFilterJob> dataFilterJobs)
      throws ParametersException {
    AccessibilityComparator accessibilityComparator = new AccessibilityComparator(artifactsDAO,
        comparatorProperties,
        dataFilterJobs);
    accessibilityComparator.setParameters(comparator.getParameters());
    return accessibilityComparator;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Accessibility")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Raport Level")
                            .tag("raport-level")
                            .withValues()
                            .addValue("ERROR")
                            .addValue("WARN")
                            .addValue("NOTICE")
                            .and().defaultValue("ERROR")
                            .isMandatory(false)
                            .description("Violation types to be show in the report, ERROR for errors, WARN for errors and warnings, NOTICE for everything")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Ignore Notice")
                            .tag("ignore-notice")
                            .withValues()
                            .addValue("true")
                            .addValue("false")
                            .and().defaultValue("true")
                            .isMandatory(false)
                            .description("If the ignore-notice=true test status does not depend on the number of notices. If ignore-notice=false notices are treated as warnings in calculating the test status.")
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
                            .description("The flag that indicates if excluded issues")
                            .build()
            )
            .and()
            .withDeps("accessibility-collectors").depType("Error")
            .dropTo("Comparators")
            .group("Comparators")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/AccessibilityComparator")
            .build();
  }
}
