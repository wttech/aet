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

import java.util.List;
import javax.annotation.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

class LoadedCondition implements ExpectedCondition<Integer> {

  private final By selector;

  private int alreadyLoaded = 0;

  public LoadedCondition(By selector) {
    this.selector = selector;
  }

  @Nullable
  @Override
  public Integer apply(@Nullable WebDriver webDriver) {
    List<WebElement> elements = webDriver.findElements(selector);
    boolean tilesLoaded = !isStillLoading(elements) && alreadyLoaded > 0;
    return tilesLoaded ? elements.size() : null;
  }

  private boolean isStillLoading(List<WebElement> currentlyFound) {
    boolean stillLoading = false;
    if (currentlyFound.size() > alreadyLoaded) {
      alreadyLoaded = currentlyFound.size();
      stillLoading = true;
    }
    return stillLoading;
  }
}
