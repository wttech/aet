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
package com.cognifide.aet.communication.api.wrappers;

import com.cognifide.aet.communication.api.metadata.Url;

public class UrlRunWrapper implements Run {

  private Url url;
  private String testName;
  private String proxy;
  private String preferredBrowserId;

  public UrlRunWrapper(Url url) {
    this.url = url;
  }

  @Override
  public String getType() {
    return "URL";
  }

  @Override
  public Url getObjectToRun() {
    return url;
  }


  public String getTestName() {
    return testName;
  }

  public String getProxy() {
    return proxy;
  }

  public String getPreferredBrowserId() {
    return preferredBrowserId;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public void setProxy(String proxy) {
    this.proxy = proxy;
  }

  public void setPreferredBrowserId(String preferredBrowserId) {
    this.preferredBrowserId = preferredBrowserId;
  }

}
