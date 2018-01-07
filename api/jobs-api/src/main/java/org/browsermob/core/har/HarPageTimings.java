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

import org.codehaus.jackson.annotate.JsonWriteNullProperties;

@JsonWriteNullProperties(value = false)
public class HarPageTimings {

  private Long onContentLoad;
  private Long onLoad;

  public HarPageTimings() {
  }

  public HarPageTimings(Long onContentLoad, Long onLoad) {
    this.onContentLoad = onContentLoad;
    this.onLoad = onLoad;
  }

  public Long getOnContentLoad() {
    return onContentLoad;
  }

  public void setOnContentLoad(Long onContentLoad) {
    this.onContentLoad = onContentLoad;
  }

  public Long getOnLoad() {
    return onLoad;
  }

  public void setOnLoad(Long onLoad) {
    this.onLoad = onLoad;
  }
}
