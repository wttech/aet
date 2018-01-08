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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ResponseObject {

  private final Map<String, List<String>> headers;

  private final byte[] content;

  public ResponseObject(Map<String, List<String>> headers, byte[] content) {
    this.headers = headers;
    this.content = content != null ? Arrays.copyOf(content, content.length) : null;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public byte[] getContent() {
    return content != null ? Arrays.copyOf(content, content.length) : null;
  }

}
