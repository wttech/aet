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

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.validation.ConstraintViolation;

public class ValidatorException extends Exception {

  private static final Function<ConstraintViolation, String> EXTRACT_CONSTRAINT_VIOLATION_MESSAGES =
      new Function<ConstraintViolation, String>() {
        @Override
        public String apply(ConstraintViolation violation) {
          return violation.getMessage();
        }
      };

  private transient final Set<ConstraintViolation> issues;

  @SuppressWarnings("unchecked")
  public ValidatorException(String message, Set issues) {
    super(message);
    this.issues = issues;
  }

  public Set<ConstraintViolation> getIssues() {
    return ImmutableSet.copyOf(issues);
  }

  public Set<String> getAllViolationMessages() {
    return FluentIterable.from(issues).transform(EXTRACT_CONSTRAINT_VIOLATION_MESSAGES).toSet();
  }
}
