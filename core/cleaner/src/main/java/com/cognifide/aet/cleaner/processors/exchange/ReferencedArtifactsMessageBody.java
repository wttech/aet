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

import com.cognifide.aet.vs.DBKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReferencedArtifactsMessageBody extends MessageBody<String> {

  private static final long serialVersionUID = 3811933888080393100L;

  private Set<String> artifactsToRemove;

  private Set<String> artifactsToKeep;

  public ReferencedArtifactsMessageBody(String data, DBKey dbKey) {
    super(data, dbKey);
  }

  public String getId() {
    return getDbKey().getCompany() + "_" + getDbKey().getProject() + "|" + getData();
  }

  public Set<String> getArtifactsToRemove() {
    return artifactsToRemove != null ? Collections.unmodifiableSet(artifactsToRemove)
        : Collections.<String>emptySet();
  }

  public void setArtifactsToRemove(Set<String> artifactsToRemove) {
    this.artifactsToRemove = artifactsToRemove;
  }

  public Set<String> getArtifactsToKeep() {
    return artifactsToKeep != null ? Collections.unmodifiableSet(artifactsToKeep)
        : Collections.<String>emptySet();
  }

  public void setArtifactsToKeep(Set<String> artifactsToKeep) {
    this.artifactsToKeep = artifactsToKeep;
  }

  public void update(ReferencedArtifactsMessageBody body) {
    if (artifactsToRemove == null) {
      artifactsToRemove = new HashSet<>();
    }
    artifactsToRemove.addAll(body.getArtifactsToRemove());

    if (artifactsToKeep == null) {
      artifactsToKeep = new HashSet<>();
    }
    artifactsToKeep.addAll(body.getArtifactsToKeep());
  }
}
