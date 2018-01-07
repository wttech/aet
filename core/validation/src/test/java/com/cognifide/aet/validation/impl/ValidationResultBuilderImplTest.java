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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.cognifide.aet.validation.ErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class ValidationResultBuilderImplTest {

  private ValidationResultBuilderImpl tested;

  private static final String MSG_1 = "msg1";

  private static final String MSG_2 = "msg2";

  private static final String MSG_3 = "msg1";

  @Before
  public void setUp() {
    tested = new ValidationResultBuilderImpl();
  }

  @Test
  public void addErrorMessage() {
    tested.addErrorMessage(MSG_1);
    assertThat(tested.getErrorMessages(), hasSize(1));

    ErrorMessage errorMessage = tested.getErrorMessages().get(0);
    assertThat(errorMessage.getMessage(), is(MSG_1));
    assertThat(tested.isValid(), is(false));
    assertThat(tested.hasErrors(), is(true));
  }

  @Test
  public void addErrorMessage_multiple() {
    tested.addErrorMessage(MSG_1).addErrorMessage(MSG_2).addErrorMessage(MSG_3);
    assertThat(tested.getErrorMessages(), hasSize(3));

    ErrorMessage errorMessage = tested.getErrorMessages().get(0);
    assertThat(errorMessage.getMessage(), is(MSG_1));

    errorMessage = tested.getErrorMessages().get(1);
    assertThat(errorMessage.getMessage(), is(MSG_2));

    errorMessage = tested.getErrorMessages().get(2);
    assertThat(errorMessage.getMessage(), is(MSG_3));
    assertThat(tested.isValid(), is(false));
    assertThat(tested.hasErrors(), is(true));
  }

  @Test
  public void testToString() throws Exception {
    //empty builder
    ValidationResultBuilderImpl validationResultBuilder = new ValidationResultBuilderImpl();
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        is(StringUtils.EMPTY));

    //single error message
    validationResultBuilder.addErrorMessage("MSG_1");
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("1 errors were found"));
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("Message: MSG_1"));

    //two error messages
    validationResultBuilder.addErrorMessage("MSG_2");
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("2 errors were found"));
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("Message: MSG_1"));
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("Message: MSG_2"));

    //two error messages
    validationResultBuilder.addErrorMessage("MSG_3", new NoSuchFieldException());
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("3 errors were found"));
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("Message: MSG_1"));
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("Message: MSG_2"));
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("Message: MSG_3"));
    assertThat(ValidationUtils.validationResultToString(validationResultBuilder),
        containsString("Caused by: java.lang.NoSuchFieldException"));
  }
}
