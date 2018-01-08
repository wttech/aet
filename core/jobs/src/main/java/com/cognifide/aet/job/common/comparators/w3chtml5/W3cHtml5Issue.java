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

import java.io.Serializable;

public class W3cHtml5Issue implements Serializable {

  private static final long serialVersionUID = -738232028733701967L;

  protected int line;

  protected int column;

  protected String message;

  protected String code1;

  protected String code2;

  protected String errorPosition;

  private String additionalInfo;

  protected W3cHtml5IssueType issueType;

  public W3cHtml5Issue(int line, int column, String message, String code1, String code2,
      String errorPosition,
      String additionalInfo, W3cHtml5IssueType issueType) {
    this.line = line;
    this.column = column;
    this.message = message;
    this.code1 = code1;
    this.code2 = code2;
    this.errorPosition = errorPosition;
    this.additionalInfo = additionalInfo;
    this.issueType = issueType;
  }

  public W3cHtml5IssueType getIssueType() {
    return issueType;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public String getMessage() {
    return message;
  }

  public String getCode1() {
    return code1;
  }

  public String getCode2() {
    return code2;
  }

  public String getErrorPosition() {
    return errorPosition;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

}
