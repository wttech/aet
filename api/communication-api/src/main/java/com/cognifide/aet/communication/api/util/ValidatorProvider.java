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
package com.cognifide.aet.communication.api.util;

import java.util.Collections;
import java.util.List;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.spi.ValidationProvider;
import org.hibernate.validator.HibernateValidator;

public final class ValidatorProvider {

  private static volatile Validator validator;

  private ValidatorProvider() {
    //empty constructor
  }

  private static void initGenerator() {
    validator = Validation.byDefaultProvider()
        .providerResolver(new HibernateValidationProviderResolver())
        .configure().ignoreXmlConfiguration()
        .buildValidatorFactory()
        .getValidator();
  }

  public static Validator getValidator() {
    if (validator == null) {
      initGenerator();
    }
    return validator;
  }

  private static class HibernateValidationProviderResolver implements ValidationProviderResolver {

    @Override
    public List<ValidationProvider<?>> getValidationProviders() {
      return Collections.<ValidationProvider<?>>singletonList(new HibernateValidator());
    }
  }
}
