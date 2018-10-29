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
package com.cognifide.aet.job.common.reporters.accessibility.format.bug;

import com.cognifide.aet.job.common.reporters.Bug;
import com.cognifide.aet.job.common.reporters.Label;
import com.cognifide.aet.job.common.reporters.accessibility.AccessibilityCode;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class AccessibilityBug implements Bug {

  private static final String NEW_LINE = "\n";

  private static final String DIVIDE_LINE = "\n\n";

  private final AccessibilityCode code;

  private final List<Case> cases;

  public AccessibilityBug(AccessibilityCode code, List<Case> cases) {
    this.code = code;
    this.cases = cases;
  }

  public List<Case> getCases() {
    return cases;
  }

  @Override
  public String getSummary() {
    return code.get();
  }

  @Override
  public Set<String> getLabels() {
    return Sets.newHashSet(Label.ACCESSIBILITY.name(), Label.AET.name());
  }

  @Override
  public String getDescription() {
    StringBuilder summary = new StringBuilder();

    cases.forEach(c -> {
      summary.append(c.getErrorMessage());
      summary.append(NEW_LINE);
      appendOccurrences(c.getOccurences(), summary);
      summary.append(DIVIDE_LINE);
    });

    return summary.toString();
  }

  private void appendOccurrences(List<Occurence> occurrences, StringBuilder summary) {
    occurrences.forEach(occurrence -> {
      summary.append(occurrence.getUrl());
      summary.append(NEW_LINE);
      appendLineNumbers(occurrence.getLineNumbers(), summary);
    });
  }

  private void appendLineNumbers(List<Integer> lineNumbers, StringBuilder summary) {
    StringJoiner joiner = new StringJoiner(", ");
    lineNumbers.forEach(line -> joiner.add(Integer.toString(line)));
    summary.append(joiner.toString());
    summary.append(DIVIDE_LINE);
  }
}
