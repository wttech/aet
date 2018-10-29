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
package com.cognifide.aet.job.common.reporters;

public class ReportIssue {

  private final String id;

  private final String key;

  private final String self;

  public ReportIssue(String id, String key, String self) {
    this.id = id;
    this.key = key;
    this.self = self;
  }

  public String getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public String getSelf() {
    return self;
  }
}
