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
package com.cognifide.aet.vs;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Validatable;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.util.ValidatorProvider;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

public class SimpleDBKey implements DBKey, Validatable {

  private static final long serialVersionUID = -5804482755568210487L;

  @NotBlank
  @Size(max = 30)
  @Pattern(regexp = "^[a-z\\d-]+$", message = "may include only alphanumeric characters and '-'")
  private final String company;

  @NotBlank
  @Size(max = 30)
  @Pattern(regexp = "^[a-z\\d-]+$", message = "may include only alphanumeric characters and '-'")
  private final String project;

  public SimpleDBKey(String company, String project) {
    this.company = company;
    this.project = project;
  }

  public SimpleDBKey(Suite suite) {
    this.company = suite.getCompany();
    this.project = suite.getProject();
  }

  @Override
  public String getProject() {
    return project;
  }

  @Override
  public String getCompany() {
    return company;
  }

  @Override
  public String toString() {
    return "{company='" + company + '\'' +
        ", project='" + project + '\'' +
        '}';
  }

  @Override
  public void validate(Set<String> ignoreFields) throws ValidatorException {
    Set<ConstraintViolation<SimpleDBKey>> issues = ValidatorProvider.getValidator().validate(this);
    if (!issues.isEmpty()) {
      throw new ValidatorException("Invalid SimpleDBKey", issues);
    }
  }

}
