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
package com.cognifide.aet.communication.api.metadata;

import java.util.Objects;

public class Pattern {

  @javax.validation.constraints.Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "Invalid objectID")
  private final String pattern;

  public Pattern(String pattern) {
    this.pattern = pattern;
  }

  public String getPattern() {
    return pattern;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pattern pattern1 = (Pattern) o;
    return Objects.equals(pattern, pattern1.pattern);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pattern);
  }
}
