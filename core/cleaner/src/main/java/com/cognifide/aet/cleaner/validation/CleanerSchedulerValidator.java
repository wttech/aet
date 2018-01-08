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
package com.cognifide.aet.cleaner.validation;

import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;
import org.apache.commons.lang3.StringUtils;

public class CleanerSchedulerValidator implements Validator {

  private final String schedule;

  private final Long keepNVersions;

  private final Long removeOlderThan;

  public CleanerSchedulerValidator(String schedule, Long keepNVersions, Long removeOlderThan) {
    this.schedule = schedule;
    this.keepNVersions = keepNVersions;
    this.removeOlderThan = removeOlderThan;
  }

  private void validateRestrictions(ValidationResultBuilder validationResultBuilder) {
    if (keepNVersions == null || keepNVersions <= 0) {
      validationResultBuilder.addErrorMessage("Leave N Patterns has to be greater than 0");
    }
    if (removeOlderThan == null || removeOlderThan < 0) {
      validationResultBuilder
          .addErrorMessage("Remove Older Than has to be greater or equal than 0");
    }
  }

  private void validateSchedule(ValidationResultBuilder validationResultBuilder) {
    if (StringUtils.isEmpty(schedule)) {
      validationResultBuilder.addErrorMessage("CRON expression may not be empty");
    }
  }

  @Override
  public void validate(ValidationResultBuilder validationResultBuilder) {
    validateSchedule(validationResultBuilder);
    validateRestrictions(validationResultBuilder);
  }
}
