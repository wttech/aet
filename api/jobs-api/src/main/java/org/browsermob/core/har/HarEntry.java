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
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonAutoDetect
public class HarEntry {

  private String pageref;
  private Date startedDateTime;
  private long time;
  private HarRequest request;
  private HarResponse response;
  private HarCache cache = new HarCache();
  private HarTimings timings;
  private String serverIPAddress;

  public HarEntry() {
  }

  public HarEntry(String pageref) {
    this.pageref = pageref;
    this.startedDateTime = new Date();
  }

  public String getPageref() {
    return pageref;
  }

  public void setPageref(String pageref) {
    this.pageref = pageref;
  }

  @JsonSerialize(using = ISO8601DateFormatter.class)
  public Date getStartedDateTime() {
    return startedDateTime;
  }

  public void setStartedDateTime(Date startedDateTime) {
    this.startedDateTime = startedDateTime;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public HarRequest getRequest() {
    return request;
  }

  public void setRequest(HarRequest request) {
    this.request = request;
  }

  public HarResponse getResponse() {
    return response;
  }

  public void setResponse(HarResponse response) {
    this.response = response;
  }

  public HarCache getCache() {
    return cache;
  }

  public void setCache(HarCache cache) {
    this.cache = cache;
  }

  public HarTimings getTimings() {
    return timings;
  }

  public void setTimings(HarTimings timings) {
    this.timings = timings;
  }

  public String getServerIPAddress() {
    return serverIPAddress;
  }

  public void setServerIPAddress(String serverIPAddress) {
    this.serverIPAddress = serverIPAddress;
  }
}
