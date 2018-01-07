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
package com.cognifide.aet.cleaner.processors.exchange;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.vs.DBKey;
import java.util.Set;

public class SuiteMessageBody extends MessageBody<Suite> {

  private static final long serialVersionUID = -162821828397507909L;

  private final boolean toRemove;

  private Set<String> suiteArtifacts;

  public SuiteMessageBody(Suite data, DBKey dbKey, boolean toRemove) {
    super(data, dbKey);
    this.toRemove = toRemove;
  }

  public boolean shouldBeRemoved() {
    return toRemove;
  }

  public Set<String> getSuiteArtifacts() {
    return suiteArtifacts;
  }

  public void setSuiteArtifacts(Set<String> suiteArtifacts) {
    this.suiteArtifacts = suiteArtifacts;
  }
}
