/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.aet.rest;

import static com.cognifide.aet.communication.api.metadata.Step.SCREEN;

import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import java.util.List;
import java.util.Set;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

@Component(service = ResetService.class)
public class ResetService {

  @Reference
  private MetadataDAO metadataDAO;

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ResetSuiteServlet.class);

  public Suite resetPattern(Suite suite, String urlToReset) throws StorageException, ValidatorException {
    List<Test> tests = suite.getTests();

    if (!tests.isEmpty()) {
      Test test = getLatestTest(tests);
      Set<Url> urls = test.getUrls();

      Url curentUrl = urls.stream()
          .filter(url -> urlToReset.equals(url.getUrl()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException(String.format("The url: %s don't exit in test: %s", urlToReset, test)));

      Step selectedStep = curentUrl.getSteps().stream()
          .filter(step -> SCREEN.equals(step.getName()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException(String.format("The step name %s don't exit in test: %s", SCREEN, test)));

      selectedStep.replacePatternWithLatestPattern();
    } else {
      LOGGER.warn("Suite test has not contains any element: {}", suite);
      throw new IllegalStateException(String.format("Suite test has not element: %s", suite.toString()));
    }

    return metadataDAO.updateSuite(suite);
  }

  private Test getLatestTest(List<Test> tests) {
    return tests.get(tests.size() - 1);
  }
}
