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
package com.cognifide.aet.job.api.collector;

import java.io.IOException;

/**
 * A Http request logic wrapper, that enables executing request with defined headers and cookies.
 */
public interface HttpRequestExecutor {

  HttpRequestExecutor addHeader(String key, String value);

  HttpRequestExecutor addCookie(String key, String value);

  HttpRequestExecutor removeCookie(String key);

  /**
   * Executes a GET request to the given <b>url</b> and returns {@link ResponseObject}
   *
   * @param url - an url the request should be done to
   * @param timeoutInMs - number of milliseconds after which connection will be timed out
   */
  ResponseObject executeGetRequest(String url, int timeoutInMs) throws IOException;
}
