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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class Url implements Serializable, Commentable, Named {

  private static final long serialVersionUID = -8235442513988955778L;

  @NotBlank
  private final String name;

  @NotBlank
  private final String url;

  private final String domain;

  private String errorMessage;

  private String comment;

  private Statistics collectionStats;

  @Valid
  @NotNull
  private final List<Step> steps = new ArrayList<>();

  public Url(String name, String url, String domain) {
    this.name = name;
    this.url = url;
    this.domain = domain;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getDomain() {
    return domain;
  }

  public boolean addStep(Step step) {
    return steps.add(step);
  }

  public List<Step> getSteps() {
    return steps;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String getComment() {
    return comment;
  }

  public Statistics getCollectionStats() {
    return collectionStats;
  }

  public void setCollectionStats(Statistics collectionStats) {
    this.collectionStats = collectionStats;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("url", url)
        .add("domain", domain)
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
    Url that = (Url) o;
    return java.util.Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(name);
  }
}
