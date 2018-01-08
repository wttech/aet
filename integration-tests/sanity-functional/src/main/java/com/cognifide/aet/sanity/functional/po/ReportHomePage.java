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
package com.cognifide.aet.sanity.functional.po;

import com.cognifide.aet.sanity.functional.po.aside.Aside;
import com.cognifide.qa.bb.constants.Timeouts;
import com.cognifide.qa.bb.provider.selenium.BobcatWait;
import com.cognifide.qa.bb.qualifier.PageObject;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PageObject
public class ReportHomePage {

  private static final Logger LOG = LoggerFactory.getLogger(ReportHomePage.class);

  private static final String TILE_SELECTOR = "div.reports-list-item";

  private static final String SELECTOR_CSS_CLASS_JOINER = ".";

  @Inject
  @Named("report.url")
  private String reportUrl;

  @FindBy(css = ".aside")
  private Aside aside;

  @Inject
  private WebDriver webDriver;

  @Inject
  private BobcatWait bobcatWait;

  public void openAndWaitForTiles() {
    webDriver.get(reportUrl);
    Integer tiles = bobcatWait.withTimeout(Timeouts.BIG)
        .until(new LoadedCondition(By.cssSelector(TILE_SELECTOR)));
    LOG.debug("tiles for URLs found: '{}'", tiles);
  }

  public List<WebElement> findTiles() {
    return webDriver.findElements(By.cssSelector(TILE_SELECTOR));
  }

  public List<WebElement> findTiles(String cssClass) {
    return webDriver
        .findElements(By.cssSelector(TILE_SELECTOR + SELECTOR_CSS_CLASS_JOINER + cssClass));
  }

  public Aside getAside() {
    return aside;
  }
}
