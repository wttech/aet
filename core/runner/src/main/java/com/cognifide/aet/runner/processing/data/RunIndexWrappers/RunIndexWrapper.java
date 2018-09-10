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
package com.cognifide.aet.runner.processing.data.RunIndexWrappers;

import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.wrappers.MetadataRunDecorator;
import com.cognifide.aet.communication.api.wrappers.Run;
import java.util.ArrayList;
import java.util.Optional;

public abstract class RunIndexWrapper {

  protected Run objectToRunWrapper = null;

  RunIndexWrapper(Run objectToRunWrapper){
    this.objectToRunWrapper = objectToRunWrapper;
  }

  public static void cleanUrlFromExecutionData(Url url) {
    url.setCollectionStats(null);
    url.getSteps()
        .forEach(step -> {
          step.setStepResult(null);
          if (step.getComparators() != null) {
            step.getComparators()
                .forEach(comparator -> {
                  comparator.setStepResult(null);
                  comparator.setFilters(new ArrayList<>());
                });
          }
        });
  }

  public Optional<Url> getTestUrl(String testName, final String urlName) {
    Test test = getTest(testName);
    Url url = test == null ? null : test.getUrl(urlName);
    return Optional.ofNullable(url);
  }

  public Test getTest(String testName) {
    return objectToRunWrapper.getRealSuite().getTest(testName);
  }

  public Run get(){
    return objectToRunWrapper;
  }

  public abstract ArrayList<MetadataRunDecorator> getUrls();
  public abstract int countUrls();
}
