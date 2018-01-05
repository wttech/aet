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
package com.cognifide.aet.job.common.collectors.accessibility;

import com.cognifide.aet.job.common.Excludable;
import java.io.Serializable;

public class AccessibilityIssue implements Serializable, Excludable {

  private static final long serialVersionUID = -53665467524179701L;

  private final IssueType type;

  private final String message;

  private final String code;

  private final String elementString;

  private final String elementStringAbbreviated;

  private int lineNumber;

  private int columnNumber;

  /**
   * If issue is excluded from results, i.e. is listed but not taken into account when computed
   * results.
   */
  private boolean excluded;

  public AccessibilityIssue(IssueType type, String message, String code, String elementString,
      String elementStringAbbreviated) {
    this.message = message;
    this.code = code;
    this.type = type;
    this.elementString = elementString;
    this.elementStringAbbreviated = elementStringAbbreviated;
  }

  public String getMessage() {
    return message;
  }

  public String getCode() {
    return code;
  }

  public IssueType getType() {
    return type;
  }

  public String getElementString() {
    return elementString;
  }

  public String getElementStringAbbreviated() {
    return elementStringAbbreviated;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public int getColumnNumber() {
    return columnNumber;
  }

  public void setColumnNumber(int columnNumber) {
    this.columnNumber = columnNumber;
  }

  @Override
  public boolean isExcluded() {
    return excluded;
  }

  @Override
  public void exclude() {
    this.excluded = true;
  }

  public enum IssueType {
    ERROR("1"),
    WARN("2"),
    NOTICE("3"),
    UNKNOWN("");

    private String value;

    IssueType(String value) {
      this.value = value;
    }

    public static IssueType byValue(String value) {
      for (IssueType issueType : IssueType.values()) {
        if (issueType.value.equalsIgnoreCase(value)) {
          return issueType;
        }
      }
      return UNKNOWN;
    }
  }
}
