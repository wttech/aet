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
package com.cognifide.aet.sanity.functional;

import static org.junit.Assert.assertEquals;

import com.cognifide.aet.sanity.functional.po.ReportHomePage;
import com.cognifide.qa.bb.junit.Modules;
import com.cognifide.qa.bb.junit.TestRunner;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.class)
@Modules(GuiceModule.class)
public class HomePageTilesTest {

  private static final int TESTS = 145;

  private static final int EXPECTED_TESTS_SUCCESS = 83;

  private static final int EXPECTED_TESTS_CONDITIONALLY_PASSED = 10;

  private static final int EXPECTED_TESTS_WARN = 5;

  private static final int EXPECTED_TESTS_FAIL = 57;
  
  private static final int EXPECTED_TESTS_FAIL = 55;

  @Inject
  private ReportHomePage page;

  @Before
  public void openReportPage() {
    page.openAndWaitForTiles();
  }

  @Test
  public void checkNumberOfTiles() {
    int tilesAll = page.findTiles().size();
    assertEquals("should render all tiles for tests", TESTS, tilesAll);
  }

  @Test
  public void checkNumberOfSuccessTiles() {
    String cssClassToSearch = TestStatus.SUCCESS.getCssClass();
    int tiles = page.findTiles(cssClassToSearch).size();
    assertEquals("number of tests tiles with SUCCESS status is incorrect",
        EXPECTED_TESTS_SUCCESS - EXPECTED_TESTS_CONDITIONALLY_PASSED,
        tiles);
  }

  @Test
  public void checkNumberOfConditionallyPassedTiles() {
    String cssClassToSearch = TestStatus.CONDITIONALLY_PASSED.getCssClass();
    int tiles = page.findTiles(cssClassToSearch).size();
    assertEquals("number of tests tiles with CONDITIONALLY PASSED status is incorrect",
        EXPECTED_TESTS_CONDITIONALLY_PASSED,
        tiles);
  }

  @Test
  public void checkNumberOfWarningTiles() {
    String cssClassToSearch = TestStatus.WARN.getCssClass();
    int tiles = page.findTiles(cssClassToSearch).size();
    assertEquals("number of tests tiles with WARNING status is incorrect", EXPECTED_TESTS_WARN,
        tiles);
  }

  @Test
  public void checkNumberOfErrorTiles() {
    String cssClassToSearch = TestStatus.FAIL.getCssClass();
    int tiles = page.findTiles(cssClassToSearch).size();
    assertEquals("number of tests tiles with ERROR status is incorrect", EXPECTED_TESTS_FAIL,
        tiles);
  }
}
