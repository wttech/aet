/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.worker.drivers;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import com.cognifide.aet.job.api.collector.HttpRequestBuilder;
import com.cognifide.aet.job.api.collector.ResponseObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestBuilderImpl implements HttpRequestBuilder {

  private final Map<String, String> headers = new LinkedHashMap<String, String>();

  private final Map<String, String> cookies = new HashMap<String, String>();

  private static final String COOKIE = "Cookie";

  private byte[] content;

  private Request request;

  @Override
  public HttpRequestBuilderImpl createRequest(String url, String methodName) {
    request = createRawRequest(url, methodName);
    // add headers
    if (!headers.isEmpty()) {
      for (Entry<String, String> entry : headers.entrySet()) {
        request.addHeader(entry.getKey(), entry.getValue());
      }
      headers.clear();
    }
    // add header cookie
    if (!cookies.isEmpty()) {
      Collection<String> cookieList = Collections2.transform(cookies.entrySet(),
              new Function<Entry<String, String>, String>() {
                @Override
                public String apply(Entry<String, String> input) {
                  return String.format("%s=%s", input.getKey(), input.getValue());
                }
              });
      request.addHeader(COOKIE, StringUtils.join(cookieList, "; "));
      cookies.clear();
    }
    // add body content
    if (content != null) {
      request.bodyByteArray(content);
      content = null;
    }
    return this;
  }

  @Override
  public ResponseObject executeRequest() throws IOException {
    HttpResponse response = request.execute().returnResponse();
    Header[] headersArray = response.getAllHeaders();
    Map<String, List<String>> tmpHeaders = new HashMap<String, List<String>>();
    for (Header header : headersArray) {
      tmpHeaders.put(header.getName(), Arrays.asList(header.getValue()));
    }
    return new ResponseObject(tmpHeaders, IOUtils.toByteArray(response.getEntity().getContent()));
  }

  @Override
  public HttpRequestBuilderImpl addHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  @Override
  public HttpRequestBuilderImpl addCookie(String key, String value) {
    cookies.put(key, value);
    return this;
  }

  @Override
  public HttpRequestBuilderImpl removeCookie(String key) {
    cookies.remove(key);
    return this;
  }

  @Override
  public HttpRequestBuilderImpl setContent(byte[] content) {
    this.content = Arrays.copyOf(content, content.length);
    return this;
  }

  private Request createRawRequest(String url, String methodName) {
    Request rawRequest;
    switch (HttpMethod.valueOf(methodName)) {
      case POST:
        rawRequest = Request.Post(url);
        break;
      case GET:
      default:
        rawRequest = Request.Get(url);
        break;
    }
    return rawRequest;
  }

  private enum HttpMethod {
    GET, POST
  }

}
