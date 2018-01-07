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
package com.cognifide.aet.job.common.modifiers.cookie;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.noMoreInteractions;

import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CookieModifierValidatorTest {

  private static final String VALUE = "value";

  private static final String NAME = "name";

  private static final String VALIDATION_ERROR_ADD = "Missing cookie-name or cookie-value on Cookie Modifier";

  private static final String VALIDATION_ERROR_REMOVE = "Missing cookie-name on Cookie Modifier";
  public static final String VALIDATION_ERROR_NULL_MODIFY_ACTION = "Provided modify action is null";

  @Mock
  private ValidationResultBuilder validationResultBuilder;

  @Test
  public void validate_addAction() {
    Validator tested = getTested(NAME, VALUE, ModifyAction.ADD);

    tested.validate(validationResultBuilder);

    verify(validationResultBuilder, noMoreInteractions()).addErrorMessage(anyString());
  }

  @Test
  public void validate_addActionNoName() {
    Validator tested = getTested(null, VALUE, ModifyAction.ADD);

    tested.validate(validationResultBuilder);

    verify(validationResultBuilder, times(1)).addErrorMessage(contains(VALIDATION_ERROR_ADD));
  }

  @Test
  public void validate_addActionNoValue() {
    Validator tested = getTested(null, VALUE, ModifyAction.ADD);

    tested.validate(validationResultBuilder);

    verify(validationResultBuilder, times(1)).addErrorMessage(contains(VALIDATION_ERROR_ADD));
  }

  @Test
  public void validate_addActionNoNameAndValue() {
    Validator tested = getTested(null, VALUE, ModifyAction.ADD);

    tested.validate(validationResultBuilder);

    verify(validationResultBuilder, times(1)).addErrorMessage(contains(VALIDATION_ERROR_ADD));
  }

  @Test
  public void validate_removeAction() {
    Validator tested = getTested(NAME, VALUE, ModifyAction.REMOVE);

    tested.validate(validationResultBuilder);

    verify(validationResultBuilder, noMoreInteractions()).addErrorMessage(anyString());
  }

  @Test
  public void validate_removeActionNoName() {
    Validator tested = getTested(null, VALUE, ModifyAction.REMOVE);

    tested.validate(validationResultBuilder);

    verify(validationResultBuilder, times(1)).addErrorMessage(contains(VALIDATION_ERROR_REMOVE));
  }

  @Test
  public void validate_noAction() {
    Validator tested = getTested(NAME, VALUE, null);

    tested.validate(validationResultBuilder);

    verify(validationResultBuilder, times(1))
        .addErrorMessage(contains(VALIDATION_ERROR_NULL_MODIFY_ACTION));
  }

  private Validator getTested(String name, String value, ModifyAction modifyAction) {
    return new CookieModifierValidator(name, value, modifyAction);
  }
}
