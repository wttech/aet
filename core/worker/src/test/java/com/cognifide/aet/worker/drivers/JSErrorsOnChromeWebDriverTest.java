/**
 * Automated Exploratory Tests
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
package com.cognifide.aet.worker.drivers;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.worker.drivers.chrome.LogEntryToJsError;
import com.google.common.collect.Ordering;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

@Ignore
public class JSErrorsOnChromeWebDriverTest {

  private static final LogEntryToJsError ENTRY_TO_JS_ERROR = new LogEntryToJsError();
  private ChromeDriver driver;

  @Before
  public void setUp() {
    System.setProperty("webdriver.chrome.driver", "C:/chromedriver_32/chromedriver.exe");

    DesiredCapabilities capabilities = DesiredCapabilities.chrome();

    LoggingPreferences loggingPreferences = new LoggingPreferences();
    loggingPreferences.enable(LogType.BROWSER, Level.ALL);
    capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--disable-plugins");

    capabilities.setCapability(ChromeOptions.CAPABILITY, options);

    driver = new ChromeDriver(capabilities);
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  public void ExtractJSLogs() {
    LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);

    final Set<JsErrorLog> jsErrorLogs = StreamSupport
        .stream(logEntries.spliterator(), false)
        .filter(input -> Level.SEVERE.equals(input.getLevel()))
        .map(ENTRY_TO_JS_ERROR)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Ordering.natural())));

    for (JsErrorLog entry : jsErrorLogs) {
      System.out.println(entry.toString());
    }

    for (LogEntry entry : logEntries) {
      System.out.println(entry.getTimestamp() + " " + entry.getLevel() + " " + entry.getMessage());
    }
  }

  @Test
  public void chrome() throws Exception {
    driver.get(
        "http://jas-staging-aet.cognifide.com/sample-site/sanity/comparators/jserrors/failed.jsp");
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    ExtractJSLogs();
  }
}
