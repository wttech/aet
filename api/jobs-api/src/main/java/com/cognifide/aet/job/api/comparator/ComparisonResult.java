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
package com.cognifide.aet.job.api.comparator;

public class ComparisonResult {

  private final Type type;

  private String resultId;

  private String error;

  private Boolean match;

  private ComparisonResult(Builder builder) {
    type = builder.type;
    resultId = builder.resultId;
    error = builder.error;
    match = builder.match;
  }

  public static Builder withPattern() {
    return new Builder(Type.WITH_PATTERN);
  }

  public static Builder noPattern() {
    return new Builder(Type.NO_PATTERN);
  }

  public Type getType() {
    return type;
  }

  public String getResultId() {
    return resultId;
  }

  public String getError() {
    return error;
  }

  public Boolean getMatch() {
    return match;
  }

  public enum Type {
    WITH_PATTERN,
    NO_PATTERN
  }


  public static final class Builder {

    private final Type type;
    private String resultId;
    private String error;
    private Boolean match;

    private Builder(Type type) {
      this.type = type;
    }

    public Builder withResultId(String val) {
      resultId = val;
      return this;
    }

    public Builder withError(String val) {
      error = val;
      return this;
    }

    public Builder withMatch(Boolean val) {
      match = val;
      return this;
    }

    public ComparisonResult build() {
      return new ComparisonResult(this);
    }
  }
}
