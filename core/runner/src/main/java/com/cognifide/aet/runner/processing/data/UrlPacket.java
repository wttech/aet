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
package com.cognifide.aet.runner.processing.data;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import java.util.ArrayList;
import java.util.List;

public class UrlPacket {

  private final Suite suite;
  private final Test test;
  private final List<Url> urls = new ArrayList<>();

  public UrlPacket(Suite suite, Test test) {
    this.suite = suite;
    this.test = test;
  }

  public void addUrl(Url url) {
    urls.add(url);
  }

  public String getCompany() {
    return suite.getCompany();
  }

  public String getProject() {
    return suite.getProject();
  }

  public String getName() {
    return suite.getName();
  }

  public String getTestName() {
    return test.getName();
  }

  public String getProxy() {
    return test.getProxy();
  }

  public String getPreferredBrowserId() {
    return test.getPreferredBrowserId();
  }

  public List<Url> getUrls() {
    return urls;
  }
}
