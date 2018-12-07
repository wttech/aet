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
package com.cognifide.aet.runner.processing.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.cognifide.aet.communication.api.metadata.Pattern;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
@Configure(separator = ";")
public class SuiteMergeStrategyTest {

  private static final Gson GSON = new Gson();

  @TestWith({"conversion/pattern.json;conversion/current.json"})
  public void merge_checkVersionIncrement(String patternResource, String currentResource)
      throws Exception {
    final Suite pattern = readSuite(patternResource);
    final Suite current = readSuite(currentResource);
    final Suite merged = SuiteMergeStrategy.merge(current, pattern);
    assertThat(merged.getVersion(), is(2L));
  }

  @TestWith({"conversion/pattern.json;conversion/current.json"})
  public void merge_checkPatternsRewritten(String patternResource, String currentResource)
      throws Exception {
    final Suite pattern = readSuite(patternResource);
    final Suite current = readSuite(currentResource);
    final Suite merged = SuiteMergeStrategy.merge(current, pattern);

    final Test test = merged.getTests().get(0);
    final Url url = test.getUrls().iterator().next();
    final Step desktopScreenStep = url.getSteps().get(2);

    assertThat(desktopScreenStep.getPatterns(), hasSize(1));
    assertThat(desktopScreenStep.getPatterns().iterator().next().getPattern(),
        is("56ebbed87346a042e67ef462"));
  }

  @TestWith({"conversion/current.json"})
  public void merge_checkVersionSetAtFirstRun(String currentResource) throws Exception {
    final Suite current = readSuite(currentResource);
    final Suite merged = SuiteMergeStrategy.merge(current, null);

    assertThat(merged.getVersion(), is(1L));
  }

  @TestWith({"conversion/pattern.json;conversion/current.json"})
  public void merge_checkNewCollectStepAddedAndOldRemoved(String patternResource,
      String currentResource)
      throws Exception {
    final Suite pattern = readSuite(patternResource);
    final Suite current = readSuite(currentResource);
    final Suite merged = SuiteMergeStrategy.merge(current, pattern);

    final Test test = merged.getTests().get(0);
    final Url url = test.getUrls().iterator().next();

    final List<Step> steps = url.getSteps();
    assertThat(steps.size(), is(5));

    final Step desktopMobileNewStep = steps.get(4);
    assertThat(desktopMobileNewStep.getType(), is("screen"));
    assertThat(desktopMobileNewStep.getPatterns(), empty());
  }

  @TestWith({"conversion/pattern.json;conversion/current.json"})
  public void merge_addSinglePatternWhenMultipleSamePatternSuitesProvided(String patternResource,
      String currentResource) throws IOException {
    final Suite current = readSuite(currentResource);
    final Suite pattern1 = readSuite(patternResource);
    final Suite pattern2 = readSuite(patternResource);
    final List<Suite> patterns = Arrays.asList(pattern1, pattern2);

    final Suite merged = SuiteMergeStrategy.merge(current, pattern1, patterns);

    final Test test = merged.getTests().get(0);
    final Url url = test.getUrls().iterator().next();

    final List<Step> steps = url.getSteps();
    assertThat(steps.size(), is(5));

    final Step step = steps.get(2);
    assertThat(step.getPatterns(), hasSize(1));
  }

  @TestWith({"conversion/pattern.json;conversion/current.json"})
  public void merge_addAllPatternsWhenMultipleDifferentPatternSuitesProvided(String patternResource,
      String currentResource) throws IOException {
    final Suite current = readSuite(currentResource);
    final Suite pattern1 = readSuite(patternResource);
    final Suite pattern2 = readSuite(patternResource);

    pattern1.getTests().get(0).getUrls().iterator().next().getSteps().get(2)
        .addPattern("foo", null);
    pattern2.getTests().get(0).getUrls().iterator().next().getSteps().get(2)
        .addPattern("zoo", null);
    List<Suite> patterns = Arrays.asList(pattern1, pattern2);

    final Suite merged = SuiteMergeStrategy.merge(current, pattern1, patterns);

    final Test test = merged.getTests().get(0);
    final Url url = test.getUrls().iterator().next();

    final List<Step> steps = url.getSteps();
    assertThat(steps.size(), is(5));

    final Step step = steps.get(2);
    assertThat(step.getPatterns(), hasSize(3));
  }

  @TestWith({"conversion/pattern.json;conversion/current.json"})
  public void merge_shouldNotErasePatternInCurrent_whenNoStepsIntersectInSuites(
      String patternResource, String currentResource) throws IOException {
    final Suite current = readSuite(currentResource);
    final Suite pattern = readSuite(patternResource);
    List<Suite> patterns = Arrays.asList(pattern);

    current.getTests().get(0).getUrls().iterator().next().getSteps().get(4).addPattern("foo", null);

    final Suite merged = SuiteMergeStrategy.merge(current, pattern, patterns);

    final Test test = merged.getTests().get(0);
    final Url url = test.getUrls().iterator().next();

    final List<Step> steps = url.getSteps();
    assertThat(steps.size(), is(5));

    final Step nonIntersectingStep = steps.get(4);
    assertThat(nonIntersectingStep.getPatterns(), hasSize(1));
  }

  private Suite readSuite(String resource) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    String json = IOUtils.toString(classLoader.getResourceAsStream(resource));
    return GSON.fromJson(json, new TypeToken<Suite>() {
    }.getType());
  }
}
