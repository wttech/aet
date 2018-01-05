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

import com.cognifide.aet.sanity.functional.po.ReportHomePage;
import com.cognifide.aet.sanity.functional.po.aside.Aside;
import com.cognifide.qa.bb.constants.Timeouts;
import com.cognifide.qa.bb.provider.selenium.BobcatWait;
import com.google.inject.Inject;
import cucumber.api.java.en.When;
import javax.annotation.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class FilteringSteps {

  @Inject
  private ReportHomePage reportHomePage;

  @Inject
  private BobcatWait bobcatWait;

  @When("^I search for tests containing \"([^\"]*)\"$")
  public void iSearchForTestsContaining(final String searchedTerm) throws Throwable {
    Aside aside = reportHomePage.getAside();
    final WebElement searchInput = aside.getSearchInput();

    // retries typing as there is known bug in selenium:
    // https://github.com/seleniumhq/selenium-google-code-issue-archive/issues/4446
    bobcatWait.withTimeout(Timeouts.MEDIUM).until(
        new ExpectedCondition<Boolean>() {

          @Nullable
          @Override
          public Boolean apply(@Nullable WebDriver input) {
            searchInput.clear();
            searchInput.sendKeys(searchedTerm);
            return searchInput.getAttribute("value").equals(searchedTerm);
          }
        });
  }
}
