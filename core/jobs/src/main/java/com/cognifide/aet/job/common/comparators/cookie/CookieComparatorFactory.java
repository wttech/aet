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
package com.cognifide.aet.job.common.comparators.cookie;

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
public class CookieComparatorFactory implements ComparatorFactory {

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  public String getType() {
    return CookieComparator.COMPARATOR_TYPE;
  }

  @Override
  public String getName() {
    return CookieComparator.COMPARATOR_NAME;
  }

  @Override
  public int getRanking() {
    return DEFAULT_COMPARATOR_RANKING;
  }

  @Override
  public ComparatorJob createInstance(Comparator comparator,
      ComparatorProperties comparatorProperties, List<DataFilterJob> dataFilterJobs)
      throws ParametersException {
    final CookieComparator cookieComparator = new CookieComparator(comparatorProperties,
        artifactsDAO);
    cookieComparator.setParameters(comparator.getParameters());
    return cookieComparator;
  }

  @Override
  public FeatureMetadata getInformation() {
    return FeatureMetadata.builder()
            .type("Cookie")
            .tag(getName())
            .withParameters()
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Action")
                            .tag("action")
                            .withValues()
                            .addValue("LIST")
                            .addValue("TEST")
                            .addValue("COMPARE")
                            .and().defaultValue("LIST")
                            .isMandatory(false)
                            .description("Violation types to be show in the report, ERROR for errors, WARN for errors and warnings, NOTICE for everything")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Cookie Name")
                            .tag("cookie-name")
                            .withoutValues()
                            .isMandatory(false)
                            .description("The name of the cookie to test, applicable only for the test action")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Cookie Value")
                            .tag("cookie-value")
                            .withoutValues()
                            .isMandatory(false)
                            .description("The value of the cookie to test, applicable only for the test action")
                            .build()
            )
            .addParameter(
                    ParameterMetadata.builder()
                            .name("Show Matched")
                            .tag("showMatched")
                            .withValues()
                            .addValue("true")
                            .addValue("false")
                            .and().defaultValue("true")
                            .isMandatory(false)
                            .description("Works only in the compare mode. The flag that says if matched cookies should be displayed in the report or not. By default set to true.")
                            .build()
            )
            .and()
            .withDeps("cookie-collectors").depType("Error")
            .dropTo("Comparators")
            .group("Comparators")
            .proxy(false)
            .wiki("https://github.com/Cognifide/aet/wiki/CookieComparator")
            .build();
  }
}
