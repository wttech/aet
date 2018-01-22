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

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class Comparator extends Operation implements Commentable, Named {

  private static final long serialVersionUID = -2641455400847109851L;

  public static final String COMPARATOR_PARAMETER = "comparator";

  private ComparatorStepResult stepResult;

  private final List<Operation> filters = new ArrayList<>();

  private String comment;

  private Statistics statistics;

  public Comparator(String type) {
    super(type);
  }

  public ComparatorStepResult getStepResult() {
    return stepResult;
  }

  public void setStepResult(ComparatorStepResult stepResult) {
    this.stepResult = stepResult;
  }

  public List<Operation> getFilters() {
    return ImmutableList.copyOf(filters);
  }

  public void addFilter(Operation operation) {
    filters.add(operation);
  }

  public void addFilters(List<Operation> operations) {
    filters.addAll(operations);
  }

  public Statistics getStatistics() {
    return statistics;
  }

  public void setStatistics(Statistics statistics) {
    this.statistics = statistics;
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public String getName() {
    String name = getType();
    if (parameters != null) {
      name = name + "|" + Joiner.on("|").withKeyValueSeparator(":").join(parameters);
    }
    return name;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("type", type)
        .add("filters", filters)
        .add("parameters", parameters)
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

    Comparator that = (Comparator) o;

    return Objects.equal(this.type, that.type) &&
        Objects.equal(this.parameters, that.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, parameters);
  }

}
