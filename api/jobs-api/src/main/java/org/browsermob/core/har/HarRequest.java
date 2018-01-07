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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HarRequest {

  private String method;
  private String url;
  private String httpVersion;
  private List<HarCookie> cookies = new CopyOnWriteArrayList<HarCookie>();
  private List<HarNameValuePair> headers = new CopyOnWriteArrayList<HarNameValuePair>();
  private List<HarNameValuePair> queryString = new CopyOnWriteArrayList<HarNameValuePair>();
  private HarPostData postData;
  private long headersSize; // Odd grammar in spec
  private long bodySize;

  public HarRequest() {
  }

  public HarRequest(String method, String url, String httpVersion) {
    this.method = method;
    this.url = url;
    this.httpVersion = httpVersion;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public void setHttpVersion(String httpVersion) {
    this.httpVersion = httpVersion;
  }

  public List<HarCookie> getCookies() {
    return cookies;
  }

  public void setCookies(List<HarCookie> cookies) {
    this.cookies = cookies;
  }

  public List<HarNameValuePair> getHeaders() {
    return headers;
  }

  public void setHeaders(List<HarNameValuePair> headers) {
    this.headers = headers;
  }

  public List<HarNameValuePair> getQueryString() {
    return queryString;
  }

  public void setQueryString(List<HarNameValuePair> queryString) {
    this.queryString = queryString;
  }

  public HarPostData getPostData() {
    return postData;
  }

  public void setPostData(HarPostData postData) {
    this.postData = postData;
  }

  public long getHeadersSize() {
    return headersSize;
  }

  public void setHeadersSize(long headersSize) {
    this.headersSize = headersSize;
  }

  public long getBodySize() {
    return bodySize;
  }

  public void setBodySize(long bodySize) {
    this.bodySize = bodySize;
  }
}
