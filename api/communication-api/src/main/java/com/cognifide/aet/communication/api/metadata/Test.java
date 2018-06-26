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
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class Test implements Serializable, Commentable, Named {

  private static final long serialVersionUID = -220660503633061510L;

  @NotBlank
  private final String name;

  private final String proxy;

  private final String preferredBrowserId;

  private String comment;

  @Valid
  @NotNull(message = "Test must have at least one url")
  private final Set<Url> urls = new HashSet<>();

  /**
   * @param name - name of a test
   * @param proxy - type of the proxy if it should be used
   * @param preferredBrowserId - id of preferred browser or null if the default one should be used
   */
  public Test(String name, String proxy, String preferredBrowserId) {
    this.name = name;
    this.proxy = proxy;
    this.preferredBrowserId = preferredBrowserId;
  }

  /**
   * Adds url to urls set. If the same url already exists, it is removed and replaced by passed url
   * object.
   *
   * @param url url to add.
   * @return {@code true} if the urls collection did not already contain the specified element
   */
  public boolean addUrl(Url url) {
    if (urls.contains(url)) {
      urls.remove(url);
    }
    return urls.add(url);
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String getComment() {
    return comment;
  }

  public String getProxy() {
    return proxy;
  }

  public String getPreferredBrowserId() {
    return preferredBrowserId;
  }

  @Override
  public String getName() {
    return name;
  }

  public Set<Url> getUrls() {
    return urls;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Test test = (Test) o;
    return java.util.Objects.equals(name, test.name);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(name);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .toString();
  }
}
