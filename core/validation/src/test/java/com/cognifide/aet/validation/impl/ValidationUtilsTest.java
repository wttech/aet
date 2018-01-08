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
package com.cognifide.aet.validation.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cognifide.aet.validation.ErrorMessage;
import com.cognifide.aet.validation.ValidationResultBuilder;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * ValidationUtilsTest
 *
 * @Author: Maciej Laskowski
 * @Date: 04.03.15
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidationUtilsTest {

  @Mock
  private ValidationResultBuilder builderWithErrors;

  @Mock
  private ValidationResultBuilder builderWithoutErrors;

  @Mock
  private ErrorMessage errorMessage;

  @Before
  public void setUp() throws Exception {
    when(builderWithErrors.hasErrors()).thenReturn(true);
    when(builderWithErrors.getErrorMessages()).thenReturn(Collections.singletonList(errorMessage));
    when(errorMessage.getMessage()).thenReturn("message");

    when(builderWithoutErrors.hasErrors()).thenReturn(false);
  }

  @Test
  public void validationResultToString_whenResultBuilderIsNull_expectEmptyString()
      throws Exception {
    String result = ValidationUtils.validationResultToString(null);
    assertTrue(StringUtils.isBlank(result));
  }

  @Test
  public void validationResultToString_whenResultBuilderHasNoErrors_expectEmptyString()
      throws Exception {
    String result = ValidationUtils.validationResultToString(builderWithoutErrors);
    assertTrue(StringUtils.isBlank(result));
  }

  @Test
  public void validationResultToString_whenResultBuilderHasErrorsWithoutThrowable_expectMessageWithoutCause()
      throws Exception {
    String result = ValidationUtils.validationResultToString(builderWithErrors);
    assertThat(result,
        is("Validation failed. 1 errors were found:\nError 1\n\tMessage: message\n"));
  }

  @Test
  public void validationResultToString_whenResultBuilderHasErrorsAndThrowable_expectMessageWithCause()
      throws Exception {
    Throwable throwable = Mockito.mock(Throwable.class);
    when(throwable.toString()).thenReturn("EXCEPTION");
    when(errorMessage.getThrowable()).thenReturn(throwable);
    String result = ValidationUtils.validationResultToString(builderWithErrors);
    assertThat(result,
        is("Validation failed. 1 errors were found:\nError 1\n\tMessage: message\n\tCaused by: EXCEPTION\n"));
  }
}
