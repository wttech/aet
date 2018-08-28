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
package com.cognifide.aet.worker.drivers.firefox.local;

import com.cognifide.aet.worker.drivers.firefox.FirefoxCommunicationWrapperImpl;
import com.cognifide.aet.worker.drivers.firefox.FirefoxProfileBuilder;
import com.cognifide.aet.job.api.collector.HttpRequestExecutorFactory;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.worker.api.WebDriverFactory;
import com.cognifide.aet.worker.drivers.firefox.local.configuration.FirefoxWebDriverFactoryConf;
import com.cognifide.aet.worker.exceptions.WorkerException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;


@Component(
    property = {Constants.SERVICE_VENDOR + "=Cognifide Ltd"}
)
@Designate(ocd = FirefoxWebDriverFactoryConf.class)
public class FirefoxWebDriverFactory implements WebDriverFactory {

  @Reference
  private HttpRequestExecutorFactory requestExecutorFactory;

  private FirefoxWebDriverFactoryConf config;

  @Override
  public String getName() {
    return config.name();
  }

  @Override
  public WebCommunicationWrapper createWebDriver(ProxyServerWrapper proxyServer)
      throws WorkerException {
    try {
      Proxy proxy = proxyServer.seleniumProxy();
      proxyServer.setCaptureContent(true);
      proxyServer.setCaptureHeaders(true);

      DesiredCapabilities capabilities = new DesiredCapabilities();
      capabilities.setCapability(CapabilityType.PROXY, proxy);
      capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

      FirefoxProfile fp = getFirefoxProfile();
      fp.setAcceptUntrustedCertificates(true);
      fp.setAssumeUntrustedCertificateIssuer(false);
      setCommonCapabilities(capabilities, fp);

      return new FirefoxCommunicationWrapperImpl(getFirefoxDriver(capabilities), proxyServer,
          requestExecutorFactory
              .createInstance());
    } catch (Exception e) {
      throw new WorkerException(e.getMessage(), e);
    }
  }

  @Override
  public WebCommunicationWrapper createWebDriver() throws WorkerException {
    try {
      DesiredCapabilities capabilities = new DesiredCapabilities();

      FirefoxProfile fp = getFirefoxProfile();
      setCommonCapabilities(capabilities, fp);

      return new FirefoxCommunicationWrapperImpl(getFirefoxDriver(capabilities), null,
          requestExecutorFactory
              .createInstance());
    } catch (Exception e) {
      throw new WorkerException(e.getMessage(), e);
    }
  }

  private FirefoxProfile getFirefoxProfile() {
    final FirefoxProfile firefoxProfile = FirefoxProfileBuilder.newInstance()
        .withUnstableAndFastLoadStrategy()
        .withLogfilePath(config.logFilePath())
        .withFlashSwitchedOff()
        .withForcedAliasing()
        .withJavaScriptErrorCollectorPlugin()
        .withDevtoolsStorageEnabled()
        .withAllCookiesAccepted()
        .withRandomPort()
        .withUpdateDisabled()
        .build();
    System.setProperty("webdriver.firefox.logfile", config.logFilePath());
    System.setProperty("webdriver.load.strategy", "unstable");
    return firefoxProfile;
  }

  private void setCommonCapabilities(DesiredCapabilities capabilities, FirefoxProfile fp) {
    capabilities.setCapability(FirefoxDriver.PROFILE, fp);
    capabilities.setCapability("marionette", false);
    capabilities.setCapability("firefox_binary", new File(config.path()).getAbsolutePath());
  }

  private WebDriver getFirefoxDriver(DesiredCapabilities capabilities) {
    WebDriver driver = new FirefoxDriver(capabilities);
    driver.manage().timeouts().pageLoadTimeout(5L, TimeUnit.MINUTES);
    return driver;
  }

  @Activate
  public void activate(FirefoxWebDriverFactoryConf config) {
    this.config = config;
  }

}
