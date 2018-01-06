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
public class HarLog {

  private String version = "1.2";
  private HarNameVersion creator;
  private HarNameVersion browser;
  private List<HarPage> pages = new CopyOnWriteArrayList<HarPage>();
  private List<HarEntry> entries = new CopyOnWriteArrayList<HarEntry>();

  public HarLog() {
  }

  public HarLog(HarNameVersion creator) {
    this.creator = creator;
  }

  public void addPage(HarPage page) {
    if (pages == null) {
      pages = new CopyOnWriteArrayList<HarPage>();
    }

    pages.add(page);
  }

  public void addEntry(HarEntry entry) {
    if (entries == null) {
      entries = new CopyOnWriteArrayList<HarEntry>();
    }

    entries.add(entry);
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public HarNameVersion getCreator() {
    return creator;
  }

  public void setCreator(HarNameVersion creator) {
    this.creator = creator;
  }

  public HarNameVersion getBrowser() {
    return browser;
  }

  public void setBrowser(HarNameVersion browser) {
    this.browser = browser;
  }

  public List<HarPage> getPages() {
    return pages;
  }

  public void setPages(List<HarPage> pages) {
    this.pages = pages;
  }

  public List<HarEntry> getEntries() {
    return entries;
  }

  public void setEntries(List<HarEntry> entries) {
    this.entries = entries;
  }
}
