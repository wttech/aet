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

import static com.cognifide.aet.worker.drivers.WebDriverHelper.DEFAULT_SELENIUM_GRID_URL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME_DESC;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME_LABEL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.PATH;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.PATH_DESC;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.PATH_LABEL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.SELENIUM_GRID_URL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.SELENIUM_GRID_URL_LABEL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.getProp;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.setupProxy;

import com.cognifide.aet.job.api.collector.HttpRequestExecutorFactory;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.worker.api.WebDriverFactory;
import com.cognifide.aet.worker.exceptions.WorkerException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.osgi.framework.Constants;

@Service
@Component(
    description = "AET Firefox WebDriver Factory",
    label = "AET Firefox WebDriver Factory",
    metatype = true)
@Properties({@Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd")})
public class FirefoxWebDriverFactory implements WebDriverFactory {

  private static final String DEFAULT_FIREFOX_BINARY_PATH = "/usr/bin/firefox";

  private static final String DEFAULT_FIREFOX_ERROR_LOG_FILE_PATH = "/opt/aet/firefox/log/stderr.log";

  private static final String LOG_FILE_PATH = "logFilePath";

  private static final String DEFAULT_FF_NAME = "ff";

  @Reference
  private HttpRequestExecutorFactory requestExecutorFactory;

  @Property(name = NAME,
      label = NAME_LABEL,
      description = NAME_DESC,
      value = DEFAULT_FF_NAME)
  private String name;

  @Property(name = PATH,
      label = PATH_LABEL,
      description = PATH_DESC,
      value = DEFAULT_FIREFOX_BINARY_PATH)
  private String path;

  @Property(name = LOG_FILE_PATH,
      label = "Log file path",
      description = "Path to firefox error log",
      value = DEFAULT_FIREFOX_ERROR_LOG_FILE_PATH)
  private String logFilePath;

  @Property(name = SELENIUM_GRID_URL,
      label = SELENIUM_GRID_URL_LABEL,
      description = "Url to selenium grid hub. When null local Firefox driver will be used.",
      value = DEFAULT_SELENIUM_GRID_URL)
  private String seleniumGridUrl;

  @Activate
  public void activate(Map<String, String> properties) {
    this.name = getProp(properties, NAME, DEFAULT_FF_NAME);
    this.path = getProp(properties, PATH, DEFAULT_FIREFOX_BINARY_PATH);
    this.logFilePath = getProp(properties, LOG_FILE_PATH, DEFAULT_FIREFOX_ERROR_LOG_FILE_PATH);
    this.seleniumGridUrl = getProp(properties, SELENIUM_GRID_URL, DEFAULT_SELENIUM_GRID_URL);
  }

  @Override
  public WebCommunicationWrapper createWebDriver() throws WorkerException {
    final DesiredCapabilities capabilities = new DesiredCapabilities();
    final FirefoxProfile fp = getFirefoxProfile();

    return createWebDriver(capabilities, fp, null);
  }

  @Override
  public WebCommunicationWrapper createWebDriver(ProxyServerWrapper proxyServer)
      throws WorkerException {
    final Proxy proxy = setupProxy(proxyServer);
    final DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.PROXY, proxy);
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

    FirefoxProfile fp = getFirefoxProfile();
    fp.setAcceptUntrustedCertificates(true);
    fp.setAssumeUntrustedCertificateIssuer(false);

    return createWebDriver(capabilities, fp, proxyServer);
  }

  @Override
  public String getName() {
    return name;
  }

  private WebCommunicationWrapper createWebDriver(DesiredCapabilities capabilities,
      FirefoxProfile fp, ProxyServerWrapper proxyServer)
      throws WorkerException {
    try {
      setCommonCapabilities(capabilities, fp);
      return new FirefoxCommunicationWrapperImpl(getFirefoxDriver(capabilities), proxyServer,
          requestExecutorFactory
              .createInstance());
    } catch (MalformedURLException e) {
      throw new WorkerException(e.getMessage(), e);
    }
  }

  private FirefoxProfile getFirefoxProfile() {
    final FirefoxProfile firefoxProfile = FirefoxProfileBuilder.newInstance()
        .withUnstableAndFastLoadStrategy()
        .withLogfilePath(logFilePath)
        .withFlashSwitchedOff()
        .withForcedAliasing()
        .withJavaScriptErrorCollectorPlugin()
        .withDevtoolsStorageEnabled()
        .withAllCookiesAccepted()
        .withRandomPort()
        .withUpdateDisabled()
        .build();
    System.setProperty("webdriver.firefox.logfile", logFilePath);
    System.setProperty("webdriver.load.strategy", "unstable");
    return firefoxProfile;
  }

  private void setCommonCapabilities(DesiredCapabilities capabilities, FirefoxProfile fp) {
    capabilities.setCapability(FirefoxDriver.PROFILE, fp);
    capabilities.setCapability("marionette", false);
    capabilities.setCapability("firefox_binary", path);
  }

  private WebDriver getFirefoxDriver(DesiredCapabilities capabilities)
      throws MalformedURLException {
    WebDriver driver = StringUtils.isNotBlank(seleniumGridUrl) ? new RemoteWebDriver(
        new URL(seleniumGridUrl), capabilities) : new FirefoxDriver(capabilities);
    driver.manage().timeouts().pageLoadTimeout(5L, TimeUnit.MINUTES);
    return driver;
  }

}
