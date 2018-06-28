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
package com.cognifide.aet.worker.drivers.firefox;

import com.cognifide.aet.worker.helpers.JavaScriptError;
import java.io.IOException;
import java.util.Random;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxProfileBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxProfileBuilder.class);

  final FirefoxProfile firefoxProfile;

  private FirefoxProfileBuilder() {
    firefoxProfile = new FirefoxProfile();
  }

  public static FirefoxProfileBuilder newInstance() {
    return new FirefoxProfileBuilder();
  }

  public FirefoxProfileBuilder withJavaScriptErrorCollectorPlugin() {
    try {
      JavaScriptError.addExtension(firefoxProfile);
    } catch (IOException e) {
      LOGGER.error("Can't add JSErrorCollector extension!", e);
    }
    return this;
  }

  public FirefoxProfileBuilder withUnstableAndFastLoadStrategy() {
    firefoxProfile.setPreference("webdriver.load.strategy", "unstable");
    return this;
  }


  public FirefoxProfileBuilder withLogfilePath(String logfilePath) {
    firefoxProfile.setPreference("webdriver.firefox.logfile", logfilePath);
    return this;
  }

  public FirefoxProfileBuilder withFlashSwitchedOff() {
    firefoxProfile.setPreference("plugin.state.flash", 0);
    return this;
  }

  public FirefoxProfileBuilder withDevtoolsStorageEnabled() {
    firefoxProfile.setPreference("devtools.storage.enabled", true);
    return this;
  }

  public FirefoxProfileBuilder withAllCookiesAccepted() {
    firefoxProfile.setPreference("network.cookie.cookieBehavior", 0);
    firefoxProfile.setPreference("network.cookie.alwaysAcceptSessionCookies", true);
    return this;
  }

  public FirefoxProfileBuilder withRandomPort() {
    firefoxProfile
        .setPreference(FirefoxProfile.PORT_PREFERENCE, new Random().nextInt(64510) + 1024);
    return this;
  }

  FirefoxProfileBuilder withClearingCacheAfterBrowserShutdown() {
    firefoxProfile.setPreference("privacy.clearOnShutdown.offlineApps", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.passwords", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.siteSettings", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.cache", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.cookies", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.downloads", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.formdata", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.history", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.openWindows", true);
    firefoxProfile.setPreference("privacy.clearOnShutdown.sessions", true);
    return this;
  }

  /**
   * The possible settings are: 0 = default, 1 = aliased, 2 = GDI Classic, 3 = GDI Natural, 4 =
   * Natural, 5 = Natural Symmetric
   */
  public FirefoxProfileBuilder withForcedAliasing() {
    firefoxProfile.setPreference("gfx.font_rendering.cleartype_params.rendering_mode", 0);
    return this;
  }

  public FirefoxProfileBuilder withUpdateDisabled() {
    firefoxProfile.setPreference("app.update.enabled", false);
    return this;
  }

  public FirefoxProfile build() {
    return firefoxProfile;
  }
}
