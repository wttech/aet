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

import com.cognifide.aet.communication.api.metadata.gson.CollectionSerializer;
import com.cognifide.aet.communication.api.metadata.gson.MapSerializer;
import com.cognifide.aet.communication.api.metadata.gson.OptionalSerializer;
import com.cognifide.aet.communication.api.metadata.gson.TimestampSerializer;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import com.cognifide.aet.communication.api.util.ValidatorProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
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

  private static final long serialVersionUID = 3602287822306302730L;
  private static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(Collection.class, new CollectionSerializer())
      .registerTypeHierarchyAdapter(Map.class, new MapSerializer())
      .registerTypeHierarchyAdapter(Optional.class, new OptionalSerializer())
      .registerTypeAdapter(Suite.Timestamp.class, new TimestampSerializer())
      .create();
  private static final Type SUITE_TYPE = new TypeToken<Suite>() {
  }.getType();

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

  private Optional<String> patternSuite;

  private String comment;

  public Suite(String correlationId, String company, String project, String name) {
    this.correlationId = correlationId;
    this.company = company;
    this.project = project;
    this.name = name;
    runTimestamp = new Timestamp(System.currentTimeMillis());
  }

  public static Suite fromJson(String json) {
    return GSON.fromJson(json, SUITE_TYPE);
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

  public Optional<String> getPatternSuite() {
    return patternSuite;
  }

  public void setPatternSuite(Optional<String> patternSuite) {
    this.patternSuite = patternSuite;
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

  public String toJson() {
    return GSON.toJson(this, SUITE_TYPE);
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