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
package com.cognifide.aet.communication.api.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class Step extends Operation implements Commentable, Named {

  private static final long serialVersionUID = 3437740998451941360L;

  @NotNull
  private final Integer index;

  @NotBlank
  private String name;

  @Valid
  private Set<Pattern> patterns;

  @Valid
  private CollectorStepResult stepResult;

  private String comment;

  private Statistics statistics;

  @Valid
  private Set<Comparator> comparators = new HashSet<>();

  private Step(Builder builder) {
    super(builder.type);
    index = builder.index;
    name = builder.name;
    patterns = builder.patterns;
    stepResult = builder.stepResult;
    setComment(builder.comment);
    comparators = builder.comparators;
  }

  public static Builder newBuilder(String type, Integer index) {
    return new Builder(type, index);
  }

  @Override
  public String getName() {
    return name;
  }

  public Integer getIndex() {
    return index;
  }

  public Set<Pattern> getPatterns() {
    if (patterns == null) {
      patterns = new HashSet<>();
    }
    return patterns;
  }

  public void erasePatterns() {
    patterns = null;
  }

  public void setStepResult(CollectorStepResult stepResult) {
    this.stepResult = stepResult;
  }

  public CollectorStepResult getStepResult() {
    return stepResult;
  }

  public Set<Comparator> getComparators() {
    return comparators;
  }

  /**
   * Adds comparator to comparators set. If the same comparator already exists, it is removed and
   * replaced by passed comparator object.
   *
   * @param comparator to add
   */
  public void addComparator(Comparator comparator) {
    if (comparators.contains(comparator)) {
      comparators.remove(comparator);
    }
    comparators.add(comparator);
  }

  public void addPattern(String artifactId, String patternSuiteCorrelationId) {
    Pattern pattern = new Pattern(artifactId, patternSuiteCorrelationId);
    addPatterns(Collections.singleton(pattern));
  }

  public void addPatterns(Set<Pattern> patterns) {
    if (this.patterns == null) {
      this.patterns = new HashSet<>();
    }
    this.patterns.addAll(patterns);
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String getComment() {
    return comment;
  }

  public Statistics getStatistics() {
    return statistics;
  }

  public void setStatistics(Statistics statistics) {
    this.statistics = statistics;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("index", index)
        .add("type", type)
        .add("name", name)
        .add("comparators no", comparators.size())
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Step that = (Step) o;

    return Objects.equal(this.type, that.type) &&
        Objects.equal(this.parameters, that.parameters) &&
        Objects.equal(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, parameters, name);
  }

  public static final class Builder {

    private final String type;
    private String name;
    private Set<Pattern> patterns;
    private CollectorStepResult stepResult;
    private String comment;
    private Set<Comparator> comparators = new HashSet<>();
    private Integer index;

    private Builder(String type, Integer index) {
      this.type = type;
      this.index = index;
    }

    public Builder withName(String val) {
      name = val;
      return this;
    }

    public Builder withPatterns(Set<Pattern> val) {
      patterns = val;
      return this;
    }

    public Builder withResult(CollectorStepResult val) {
      stepResult = val;
      return this;
    }

    public Builder withComment(String val) {
      comment = val;
      return this;
    }

    public Builder withComparators(Set<Comparator> val) {
      comparators = val;
      return this;
    }

    public Step build() {
      return new Step(this);
    }
  }
}
