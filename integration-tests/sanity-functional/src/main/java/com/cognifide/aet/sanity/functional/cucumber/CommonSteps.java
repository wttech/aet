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
package com.cognifide.aet.sanity.functional.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.cognifide.aet.sanity.functional.po.ReportHomePage;
import com.google.inject.Inject;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.openqa.selenium.WebElement;

public class CommonSteps {

  @Inject
  ReportHomePage reportHomePage;

  @Given("^I have opened sample tests report page$")
  public void iHaveOpenedSampleTestsReportPage() throws Throwable {
    reportHomePage.openAndWaitForTiles();
  }

  @Then("^There are (\\d+) tiles visible$")
  public void thereAreTilesVisible(int expectedNumberOfVisibleTiles) throws Throwable {
    int actual = reportHomePage.findTiles().size();
    assertEquals("Unexpected number of tiles", expectedNumberOfVisibleTiles, actual);
  }

  @And("^Statistics text contains \"([^\"]*)\"$")
  public void statisticsTextContains(String expectedText) throws Throwable {
    WebElement statistics = reportHomePage.getAside().getStatistics();
    String actual = statistics.getText();

    assertTrue("statistics text should contain '" + expectedText + "'",
        actual.contains(expectedText));
  }
}
