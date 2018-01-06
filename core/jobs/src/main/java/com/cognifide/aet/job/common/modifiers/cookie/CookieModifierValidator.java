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


import com.cognifide.aet.validation.ValidationResultBuilder;
import com.cognifide.aet.validation.Validator;
import org.apache.commons.lang3.StringUtils;

public class CookieModifierValidator implements Validator {

  private final String name;

  private final String value;

  private final ModifyAction modifyAction;

  public CookieModifierValidator(String name, String value, ModifyAction modifyAction) {
    this.name = name;
    this.value = value;
    this.modifyAction = modifyAction;
  }

  @Override
  public void validate(ValidationResultBuilder builder) {
    if (modifyAction == null) {
      builder.addErrorMessage("Provided modify action is null");
    } else {
      switch (modifyAction) {
        case ADD:
          validateAddAction(builder);
          break;
        case REMOVE:
          validateRemoveAction(builder);
          break;
        default:
          break;
      }
    }
  }

  private void validateAddAction(ValidationResultBuilder builder) {
    if (StringUtils.isAnyEmpty(name, value)) {
      String message = String.format("Missing %s or %s on Cookie Modifier",
          CookieModifier.NAME_PARAMETER, CookieModifier.VALUE_PARAMETER);
      builder.addErrorMessage(message);
    }
  }

  private void validateRemoveAction(ValidationResultBuilder builder) {
    if (StringUtils.isEmpty(name)) {
      builder.addErrorMessage(String.format("Missing %s on Cookie Modifier",
          CookieModifier.NAME_PARAMETER));
    }
  }
}
