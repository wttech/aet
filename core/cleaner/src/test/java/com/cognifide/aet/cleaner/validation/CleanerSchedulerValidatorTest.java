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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;
import com.cognifide.aet.validation.impl.ValidationResultBuilderImpl;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class CleanerSchedulerValidatorTest {

  private ValidationResultBuilder builder;

  private ValidatorBuilder validatorBuilder;

  @Before
  public void setUp() {
    validatorBuilder = new ValidatorBuilder();
    builder = new ValidationResultBuilderImpl();
  }

  @Test
  public void validateTest_keepNVersionsNull_expectFalse() {
    Validator tested = validatorBuilder.setKeepNVersions(null).build();

    tested.validate(builder);

    assertThat(builder.isValid(), is(false));
    assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
  }

  @Test
  public void validateTest_keepNVersions0() {
    Validator tested = validatorBuilder.setKeepNVersions(0L).build();

    tested.validate(builder);

    assertThat(builder.isValid(), is(false));
    assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
    assertThat(builder.getErrorMessages().get(0).getMessage(),
        is("Leave N Patterns has to be greater than 0"));
  }

  @Test
  public void validateTest_keepNVersions1() {
    Validator tested = validatorBuilder.setKeepNVersions(1L).build();

    tested.validate(builder);

    assertThat(builder.isValid(), is(true));
  }

  @Test
  public void validateTest_removeOlderThanMinus1() {
    Validator tested = validatorBuilder.setRemoveOlderThan(-1L).build();

    tested.validate(builder);

    assertThat(builder.isValid(), is(false));
  }

  @Test
  public void validateTest_removeOlderThan() {
    //case 0
    Validator tested = validatorBuilder.setRemoveOlderThan(0L).build();

    tested.validate(builder);

    assertThat(builder.isValid(), is(true));

    //case > 0
    tested = validatorBuilder.setRemoveOlderThan(1L).build();

    tested.validate(builder);

    assertThat(builder.isValid(), is(true));
  }

  @Test
  public void validateTest_emptyKeepNVersions_expectFalse() {
    Validator tested = validatorBuilder.setKeepNVersions(null).build();

    tested.validate(builder);
    assertThat(builder.isValid(), is(false));
  }

  @Test
  public void validateTest_emptySchedule() {
    Validator tested = validatorBuilder.setSchedule(null).build();

    tested.validate(builder);

    assertThat(builder.isValid(), is(false));
    assertThat(builder.getErrorMessages(), Matchers.hasSize(1));
    assertThat(builder.getErrorMessages().get(0).getMessage(),
        is("CRON expression may not be empty"));
  }

  private static class ValidatorBuilder {

    private static final String DEFAULT_SCHEDULE = "0 0 21 ? * *";

    private static final Long DEFAULT_KEEP_N_VERSIONS = 3L;

    private static final Long DEFAULT_REMOVE_OLDER_THAN = 5L;

    private String schedule = DEFAULT_SCHEDULE;

    private Long keepNVersions = DEFAULT_KEEP_N_VERSIONS;

    private Long removeOlderThan = DEFAULT_REMOVE_OLDER_THAN;

    private ValidatorBuilder setSchedule(String schedule) {
      this.schedule = schedule;
      return this;
    }

    private ValidatorBuilder setKeepNVersions(Long keeNVersions) {
      this.keepNVersions = keeNVersions;
      return this;
    }

    private ValidatorBuilder setRemoveOlderThan(Long removeOlderThan) {
      this.removeOlderThan = removeOlderThan;
      return this;
    }

    Validator build() {
      return new CleanerSchedulerValidator(schedule, keepNVersions, removeOlderThan);
    }
  }
}
