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
package com.cognifide.aet.sanity.functional;

import com.google.inject.Inject;

import com.cognifide.aet.sanity.functional.po.ReportHomePage;
import com.cognifide.qa.bb.junit.Modules;
import com.cognifide.qa.bb.junit.TestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(TestRunner.class)
@Modules(GuiceModule.class)
public class UrlTilesTest {

  private static final int URLS = 97;

  private static final int EXPECTED_URLS_SUCCESS = 49;

  private static final int EXPECTED_URLS_WARN = 6;

  private static final int EXPECTED_URLS_FAIL = 42;

  @Inject
  private ReportHomePage page;

  @Before
  public void openReportPage() {
    page.openAndWaitForTiles();
  }

  @Test
  public void checkNumberOfUrlTiles() {
    int tilesAll = page.findTiles().size();
    assertEquals("should render all tiles for URLs", URLS, tilesAll);
  }

  @Test
  public void checkNumberOfSuccessUrlTiles() {
    String cssClassToSearch = TestStatus.SUCCESS.getTileCssClass();
    int tiles = page.findTiles(cssClassToSearch).size();
    assertEquals("number of URL tiles with SUCCESS status is incorrect", EXPECTED_URLS_SUCCESS, tiles);
  }

  @Test
  public void checkNumberOfWarningUrlTiles() {
    String cssClassToSearch = TestStatus.WARN.getTileCssClass();
    int tiles = page.findTiles(cssClassToSearch).size();
    assertEquals("number of URL tiles with WARNING status is incorrect", EXPECTED_URLS_WARN, tiles);
  }

  @Test
  public void checkNumberOfErrorUrlTiles() {
    String cssClassToSearch = TestStatus.FAIL.getTileCssClass();
    int tiles = page.findTiles(cssClassToSearch).size();
    assertEquals("number of URL tiles with ERROR status is incorrect", EXPECTED_URLS_FAIL, tiles);
  }
}
