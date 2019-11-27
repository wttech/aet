/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.models;

import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5ComparatorResult;
import com.cognifide.aet.job.common.comparators.w3chtml5.W3cHtml5Issue;
import java.util.List;

public class W3cHtml5Error extends W3cHtml5ComparatorResult {

  private String urlName;
  private String type;

  public W3cHtml5Error(int errorCount, int warningCount,
      List<W3cHtml5Issue> issues,
      List<W3cHtml5Issue> excludedIssues) {
    super(errorCount, warningCount, issues, excludedIssues);
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
