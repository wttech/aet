/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.models;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import java.util.Set;

public class JsErrorWrapper {

  private Set<JsErrorLog> jsErrors;
  private String urlName;

  public JsErrorWrapper(Set<JsErrorLog> jsErrors, String urlName) {
    this.jsErrors = jsErrors;
    this.urlName = urlName;
  }

  public void setJsErrors(Set<JsErrorLog> jsErrors) {
    this.jsErrors = jsErrors;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
}
