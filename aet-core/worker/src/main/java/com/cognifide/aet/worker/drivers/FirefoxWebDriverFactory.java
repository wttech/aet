/*
 * Cognifide AET :: Worker
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.worker.drivers;

import static com.cognifide.aet.worker.drivers.WebDriverConstants.NAME;
import static com.cognifide.aet.worker.drivers.WebDriverConstants.NAME_LABEL;
import static com.cognifide.aet.worker.drivers.WebDriverConstants.PATH;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.osgi.framework.Constants;

import com.cognifide.aet.job.api.collector.HttpRequestBuilderFactory;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.worker.api.WebDriverFactory;
import com.cognifide.aet.worker.exceptions.WorkerException;

@Service
@Component(immediate = false, description = "AET Firefox WebDriver Factory", label = "AET Firefox WebDriver Factory", metatype = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd") })
public class FirefoxWebDriverFactory implements WebDriverFactory {

	private static final String DEFAULT_FIREFOX_ERROR_LOG_FILE_PATH = "C:\\content\\firefox\\stderr.log";

	private static final String LOG_FILE_PATH = "logFilePath";

	private static final String DEFAULT_FF_NAME = "ff";

	@Reference
	private HttpRequestBuilderFactory builderFactory;

	@Property(name = NAME, label = NAME_LABEL, value = DEFAULT_FF_NAME)
	private String name;

	@Property(name = PATH, label = "Custom path to Firefox binary")
	private String path;

	@Property(name = LOG_FILE_PATH, label = "Path to firefox error log", value = "C:\\content\\firefox\\stderr.log")
	private String logFilePath;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public WebCommunicationWrapper createWebDriver(ProxyServerWrapper proxyServer) throws WorkerException {
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
			capabilities.setCapability(FirefoxDriver.PROFILE, fp);

			return new WebCommunicationWrapperImpl(getFirefoxDriver(fp, capabilities), proxyServer, builderFactory.createInstance());
		} catch (Exception e) {
			throw new WorkerException(e.getMessage(), e);
		}
	}

	@Override
	public WebCommunicationWrapper createWebDriver() throws WorkerException {
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();

			FirefoxProfile fp = getFirefoxProfile();
			capabilities.setCapability(FirefoxDriver.PROFILE, fp);

			return new WebCommunicationWrapperImpl(getFirefoxDriver(fp, capabilities), null, builderFactory.createInstance());
		} catch (Exception e) {
			throw new WorkerException(e.getMessage(), e);
		}
	}

	private FirefoxProfile getFirefoxProfile() throws IOException {
		final FirefoxProfile firefoxProfile = FirefoxProfileBuilder.newInstance()
				.withUnstableAndFastLoadStrategy()
				.withLogfilePath(logFilePath)
				.withFlashSwitchedOff()
				.withForcedAliasing()
				.withJavaScriptErrorCollectorPlugin()
				.withDevtoolsStorageEnabled()
				.withAllCookiesAccepted()
				.withClearingCacheAfterBrowserShutdown()
				.withUpdateDisabled()
				.build();
		System.setProperty("webdriver.firefox.logfile", logFilePath);
		System.setProperty("webdriver.load.strategy", "unstable");
		return firefoxProfile;
	}

	private FirefoxDriver getFirefoxDriver(FirefoxProfile fp, DesiredCapabilities capabilities) throws IOException {
		FirefoxDriver driver;
		if (StringUtils.isBlank(path)) {
			driver = new FirefoxDriver(capabilities);
		} else {
			FirefoxBinary binary = new FirefoxBinary(new File(path));
			driver = new FirefoxDriver(binary, fp, capabilities);
		}
		driver.manage().timeouts().pageLoadTimeout(5L, TimeUnit.MINUTES);
		return driver;
	}

	@Activate
	public void activate(Map<String, String> properties) {
		this.name = PropertiesUtil.toString(properties.get(NAME), DEFAULT_FF_NAME);
		this.path = PropertiesUtil.toString(properties.get(PATH), null);
		this.logFilePath = PropertiesUtil.toString(properties.get(LOG_FILE_PATH), DEFAULT_FIREFOX_ERROR_LOG_FILE_PATH);

	}

}
