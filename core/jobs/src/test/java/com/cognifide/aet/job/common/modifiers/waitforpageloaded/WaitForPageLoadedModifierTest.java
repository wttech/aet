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
package com.cognifide.aet.job.common.modifiers.waitforpageloaded;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RunWith(MockitoJUnitRunner.class)
public class WaitForPageLoadedModifierTest {

  private WaitForPageLoadedModifier tested;

  @Mock
  private WebDriver webDriver;

  @Mock
  List<WebElement> elements;

  @Before
  public void setUp() {
    tested = new WaitForPageLoadedModifier(webDriver);
  }

  @Test
  public void testAlreadyLoaded() throws Exception {
    when(webDriver.findElements(By.xpath("//*"))).thenReturn(elements);
    when(elements.size()).thenReturn(100);

    tested.collect();

    verify(webDriver, times(2)).findElements(Matchers.<By>any());
  }

  @Test
  public void testTimeout() throws Exception {
    when(webDriver.findElements(By.xpath("//*"))).thenReturn(elements);
    Integer[] sizes = new Integer[15];
    for (int i = 1; i <= 15; i++) {
      sizes[i - 1] = 100 + i;
    }
    when(elements.size()).thenReturn(100, sizes);

    tested.collect();

    verify(webDriver, times(10)).findElements(Matchers.<By>any());
  }

  @Test
  public void testElementsLoaded() throws Exception {
    when(webDriver.findElements(By.xpath("//*"))).thenReturn(elements);
    when(elements.size()).thenReturn(100, 101, 102, 102);

    tested.collect();

    verify(webDriver, times(4)).findElements(Matchers.<By>any());
  }

}
