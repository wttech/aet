/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.communication.api.metadata;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import com.cognifide.aet.communication.api.util.ValidatorProvider;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Suite implements Serializable, Commentable, Named, Validatable {

  private static final long serialVersionUID = -4621145477713271743L;

  @NotBlank
  private final String correlationId;

  @NotBlank
  @Size(max = 30)
  @Pattern(regexp = "^[a-z\\d-]+$", message = "may include only alphanumeric characters and '-'")
  private final String company;

  @NotBlank
  @Size(max = 30)
  @Pattern(regexp = "^[a-z\\d-]+$", message = "may include only alphanumeric characters and '-'")
  private final String project;

  @NotBlank
  private final String name;

  @NotNull
  @Min(1)
  private Long version;

  @NotNull
  private Timestamp runTimestamp;

  @NotNull
  @Valid
  private final List<Test> tests = new ArrayList<>();

  private String comment;

  private Statistics collectStatistics;

  private Statistics compareStatistics;

  public Suite(String correlationId, String company, String project, String name) {
    this.correlationId = correlationId;
    this.company = company;
    this.project = project;
    this.name = name;
    runTimestamp = new Timestamp(System.currentTimeMillis());
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public String getCompany() {
    return company;
  }

  public String getSuiteIdentifier() {
    return company + "-" + project + "-" + name;
  }

  public String getProject() {
    return project;
  }

  @Override
  public String getName() {
    return name;
  }

  public Long getVersion() {
    return version;
  }

  public Long incrementVersion() {
    return ++version;
  }

  public List<Test> getTests() {
    return tests;
  }

  public boolean addTest(Test test) {
    return tests.add(test);
  }

  public Timestamp getRunTimestamp() {
    return runTimestamp;
  }

  public void setRunTimestamp(Timestamp runTimestamp) {
    this.runTimestamp = runTimestamp;
  }

  public Statistics getCollectStatistics() {
    return collectStatistics;
  }

  public void setCollectStatistics(
      Statistics collectStatistics) {
    this.collectStatistics = collectStatistics;
  }

  public Statistics getCompareStatistics() {
    return compareStatistics;
  }

  public void setCompareStatistics(
      Statistics compareStatistics) {
    this.compareStatistics = compareStatistics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Suite suite = (Suite) o;
    return java.util.Objects.equals(correlationId, suite.correlationId) &&
            java.util.Objects.equals(company, suite.company) &&
            java.util.Objects.equals(project, suite.project) &&
            java.util.Objects.equals(name, suite.name) &&
            java.util.Objects.equals(version, suite.version);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(correlationId, company, project, name, version);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
            .add("correlationId", correlationId)
            .add("company", company)
            .add("project", project)
            .add("name", name)
            .add("version", version)
            .toString();
  }

  public void setVersion(long version) {
    this.version = version;
  }

  @Override
  public void validate(final Set<String> ignoreFields) throws ValidatorException {
    Set<ConstraintViolation<Suite>> errors = FluentIterable.from(ValidatorProvider.getValidator().validate(this))
            .filter(new Predicate<ConstraintViolation<Suite>>() {
              @Override
              public boolean apply(ConstraintViolation<Suite> error) {
                return ignoreFields == null || !ignoreFields.contains(error.getPropertyPath().toString());
              }
            }).toSet();

    if (!errors.isEmpty()) {
      throw new ValidatorException("Invalid Suite object.", errors);
    }
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String getComment() {
    return comment;
  }


  public static class Timestamp implements Serializable {

    private static final long serialVersionUID = -1477961736334798357L;

    private final long value;

    public Timestamp(long value) {
      this.value = value;
    }

    public long get() {
      return value;
    }
  }
}