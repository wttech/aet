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
package com.cognifide.aet.communication.api.metadata.exclude;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class LayoutExclude implements Serializable {

  private static final long serialVersionUID = -6496109702966509444L;

  private final List<ExcludedElement> excludedElements;
  private final Set<String> notFoundElements;

  public LayoutExclude(List<ExcludedElement> excludedElements, Set<String> notFoundElements) {
    this.excludedElements = excludedElements;
    this.notFoundElements = notFoundElements;
  }

  public List<ExcludedElement> getExcludedElements() {
    return excludedElements;
  }

  public Set<String> getNotFoundElements() {
    return notFoundElements;
  }
}
