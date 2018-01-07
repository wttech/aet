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
package com.cognifide.aet.worker.drivers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.internal.Lock;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AetFirefoxDriver extends FirefoxDriver {

  public AetFirefoxDriver(Capabilities desiredCapabilities) {
    super(desiredCapabilities);
  }

  public AetFirefoxDriver(FirefoxBinary binary, FirefoxProfile profile) {
    super(binary, profile, DesiredCapabilities.firefox());
  }

  public AetFirefoxDriver(FirefoxBinary binary, FirefoxProfile profile, Capabilities capabilities) {
    super(binary, profile, capabilities, null);
  }

  @Override
  protected Lock obtainLock(FirefoxProfile profile) {
    return new AetSocketLock();
  }
}
