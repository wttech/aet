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
package com.cognifide.aet.vs.metadata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Pattern;
import com.cognifide.aet.communication.api.metadata.Suite;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DocumentConverterTest {

  private static final String PATTERN = "56f3b0a978139e1e6dabcd0e";

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {"conversion/oldStructureSuite.json"}, {"conversion/currentStructureSuite.json"}
    });
  }

  private String suiteReference;

  public DocumentConverterTest(String suiteReference) {
    this.suiteReference = suiteReference;
  }

  @Test
  public void whenConvertingOldAndNewStructureSuite_shouldBeBackwardsCompatible()
      throws IOException {
    // given
    String json = readSuite(suiteReference);
    Document oldSuite = Document.parse(json);

    // when
    Suite suite = new DocumentConverter(oldSuite).toSuite();

    Set<Pattern> patterns =
        suite.getTests().get(0).getUrls().iterator().next().getSteps().get(0).getPatterns();
    List<ComparatorStepResult> stepResults = suite.getTests().get(0).getUrls().iterator().next()
        .getSteps().get(0).getComparators().iterator().next().getStepResults();

    // then
    assertThat(patterns, hasSize(1));
    assertThat(patterns.iterator().next().getPattern(), equalTo(PATTERN));

    assertThat(stepResults, hasSize(1));
    assertThat(stepResults.get(0).getStatus(), equalTo(Status.PASSED));
  }

  private String readSuite(String resource) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return IOUtils.toString(classLoader.getResourceAsStream(resource));
  }
}
