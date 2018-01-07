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
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HarPage {

  private String id;
  private Date startedDateTime;
  private String title = "";
  private HarPageTimings pageTimings = new HarPageTimings();

  public HarPage() {
  }

  public HarPage(String id) {
    this.id = id;
    startedDateTime = new Date();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonSerialize(using = ISO8601DateFormatter.class)
  public Date getStartedDateTime() {
    return startedDateTime;
  }

  public void setStartedDateTime(Date startedDateTime) {
    this.startedDateTime = startedDateTime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public HarPageTimings getPageTimings() {
    return pageTimings;
  }

  public void setPageTimings(HarPageTimings pageTimings) {
    this.pageTimings = pageTimings;
  }
}
