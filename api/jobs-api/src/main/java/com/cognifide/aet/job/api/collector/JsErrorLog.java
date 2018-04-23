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
package com.cognifide.aet.job.api.collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Representation of JavaScript error that will be saved in database.
 * The class fields will be transformed to JSON attributes.
 */
public class JsErrorLog implements Serializable, Comparable<JsErrorLog> {

  private static final long serialVersionUID = -8929508257448156718L;

  private final String errorMessage;

  private final String sourceName;

  private final int lineNumber;

  private boolean ignored;

  private final List<FilterInfo> matchingFilters;

  public JsErrorLog(String errorMessage, String sourceName, int lineNumber) {
    this.errorMessage = errorMessage;
    this.sourceName = sourceName;
    this.lineNumber = lineNumber;
    this.matchingFilters = new ArrayList<>();
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getSourceName() {
    return sourceName;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public boolean isIgnored() {
    return this.ignored;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceName, lineNumber, errorMessage);
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj != null && obj.getClass() == this.getClass()) {
      JsErrorLog other = (JsErrorLog) obj;
      result = Objects.equals(errorMessage, other.errorMessage)
          && Objects.equals(sourceName, other.sourceName)
          && Objects.equals(lineNumber, other.lineNumber);
    }
    return result;
  }

  @Override
  public int compareTo(JsErrorLog o) {
    return new CompareToBuilder().append(sourceName, o.sourceName).append(lineNumber, o.lineNumber)
        .append(errorMessage, o.errorMessage).toComparison();
  }

  public void addMatchedFilter(FilterInfo matchedFilterInfo) {
    this.ignored = true;
    this.matchingFilters.add(matchedFilterInfo);
  }
}
