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
package com.cognifide.aet.job.common.reporters.accessibility;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AccessibilityCode {

  private static final int CODE_INDEX = 3;

  private static final String CODE_SPLIT = "_";

  private static final String ISSUE_SPLIT = "\\.";

  private static final int PRINCIPLE_INDEX = 0;

  private static final int GUIDELINE_INDEX = 1;

  private static final int CASE_INDEX = 2;

  private static final String SOLUTONS_SPLIT = ",";

  private static final Pattern CODE_PATTERN = Pattern.compile("[0-9]_[0-9]_[0-9]");

  private final int principle;

  private final int guideline;

  private final int subCase;

  private final List<String> solutions;

  public AccessibilityCode(String snifferErrorCode) {
    try {
      String code = snifferErrorCode.split(ISSUE_SPLIT)[CODE_INDEX];
      String[] splitCode = code.split(CODE_SPLIT);

      this.principle = Integer.parseInt(splitCode[PRINCIPLE_INDEX]);
      this.guideline = Integer.parseInt(splitCode[GUIDELINE_INDEX]);
      this.subCase = Integer.parseInt(splitCode[CASE_INDEX]);

      String solutions = snifferErrorCode.split(ISSUE_SPLIT)[4];
      this.solutions = Arrays.asList(solutions.split(SOLUTONS_SPLIT));

      validate(snifferErrorCode);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Provided snifferErrorCode has invalid format: " + snifferErrorCode);
    }
  }

  public String get() {
    return String.format("%d_%d_%d", principle, guideline, subCase);
  }

  public int getPrinciple() {
    return principle;
  }

  public int getGuideline() {
    return guideline;
  }

  public int getSubCase() {
    return subCase;
  }

  public List<String> getSolutions() {
    return solutions;
  }

  private void validate(String accessibilityIssueCode) {
    String s = get();
    if (!CODE_PATTERN.matcher(s).matches()) {
      throw new IllegalStateException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccessibilityCode that = (AccessibilityCode) o;
    return get().equals(that.get());
  }

  @Override
  public int hashCode() {
    int result = 31 + get().hashCode();
    return result;
  }
}
