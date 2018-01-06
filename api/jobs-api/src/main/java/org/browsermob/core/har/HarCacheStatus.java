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
/*
 * Copyright [2016] [http://bmp.lightbody.net/]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.browsermob.core.har;

import java.util.Date;
import org.browsermob.core.json.ISO8601DateFormatter;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonWriteNullProperties(value = false)
public class HarCacheStatus {

  private Date expires;
  private Date lastAccess;
  private String eTag;
  private int hitCount;

  @JsonSerialize(using = ISO8601DateFormatter.class)
  public Date getExpires() {
    return expires;
  }

  public void setExpires(Date expires) {
    this.expires = expires;
  }

  @JsonSerialize(using = ISO8601DateFormatter.class)
  public Date getLastAccess() {
    return lastAccess;
  }

  public void setLastAccess(Date lastAccess) {
    this.lastAccess = lastAccess;
  }

  public String geteTag() {
    return eTag;
  }

  public void seteTag(String eTag) {
    this.eTag = eTag;
  }

  public int getHitCount() {
    return hitCount;
  }

  public void setHitCount(int hitCount) {
    this.hitCount = hitCount;
  }
}
