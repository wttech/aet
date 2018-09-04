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
package com.cognifide.aet.common;

import com.google.gson.Gson;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.mime.MIME;
import org.apache.http.util.EntityUtils;

@Deprecated
class JsonResponseHandler<T> implements ResponseHandler<T> {

  private Class<T> resultClass;

  public JsonResponseHandler(Class<T> resultClass) {
    this.resultClass = resultClass;
  }

  @Override
  public T handleResponse(HttpResponse httpResponse) throws IOException {
    StatusLine statusLine = httpResponse.getStatusLine();
    if (statusLine.getStatusCode() == 200) {
      String result = EntityUtils.toString(httpResponse.getEntity(), MIME.UTF8_CHARSET);
      Gson gson = new Gson();
      return gson.fromJson(result, resultClass);
    } else {
      throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
    }
  }
}
