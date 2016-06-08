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

import java.util.Map;

import com.cognifide.aet.job.api.collector.HttpRequestBuilderFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.openqa.selenium.chrome.ChromeDriver;
import org.osgi.framework.Constants;

import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.worker.api.WebDriverFactory;
import com.cognifide.aet.worker.exceptions.WorkerException;

@Service
@Component(immediate = true, description = "AET Chrome WebDriver Factory", label = "AET Chrome WebDriver Factory", metatype = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd") })
public class ChromeWebDriverFactory implements WebDriverFactory {

	@Reference
	private HttpRequestBuilderFactory builderFactory;

	@Property(name = NAME, label = NAME_LABEL, value = "chrome")
	private String name;

	@Property(name = PATH, label = "Path to Chrome")
	private String path;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public WebCommunicationWrapper createWebDriver(ProxyServerWrapper proxyServer) {
		System.setProperty("webdriver.chrome.driver", this.path);

		return new WebCommunicationWrapperImpl(new ChromeDriver(), null, builderFactory.createInstance());
	}

	@Override
	public WebCommunicationWrapper createWebDriver() throws WorkerException {
		return null;
	}

	@Activate
	void activate(Map<String, String> properties) {
		this.name = properties.get(NAME);
		this.path = properties.get(PATH);
	}

}
