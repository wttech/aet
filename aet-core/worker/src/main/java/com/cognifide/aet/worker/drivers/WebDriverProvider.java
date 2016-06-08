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

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.proxy.ProxyServerProvider;
import com.cognifide.aet.job.api.exceptions.ProxyException;
import com.cognifide.aet.worker.api.WebDriverFactory;
import com.cognifide.aet.worker.exceptions.WorkerException;
import com.google.common.collect.Maps;

/**
 * @author lukasz.wieczorek
 */
@Service(WebDriverProvider.class)
@Component(label = "AET WebDriver Provider", description = "AET WebDriver Provider", immediate = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd") })
public class WebDriverProvider {

	@Reference(referenceInterface = WebDriverFactory.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, bind = "bindWebDriverFactory", unbind = "unbindWebDriverFactory")
	private final Map<String, WebDriverFactory> collectorFactories = Maps.newConcurrentMap();

	@Reference
	private ProxyServerProvider proxyServerProvider;

	public WebCommunicationWrapper createWebDriverWithProxy(String webDriverName, String useProxy, int port)
			throws WorkerException {
		WebDriverFactory webDriverFactory = collectorFactories.get(webDriverName);
		if (webDriverFactory == null) {
			throw new WorkerException("Undefined WebDriver " + webDriverName);
		}
		try {
			return webDriverFactory.createWebDriver(proxyServerProvider.createProxy(useProxy, port));
		} catch (ProxyException e) {
			throw new WorkerException("WorkerException", e);
		}
	}

	public WebCommunicationWrapper createWebDriver(String webDriverName) throws WorkerException {
		WebDriverFactory webDriverFactory = collectorFactories.get(webDriverName);
		if (webDriverFactory == null) {
			throw new WorkerException("Undefined WebDriver " + webDriverName);
		}
		return webDriverFactory.createWebDriver();
	}

	protected void bindWebDriverFactory(WebDriverFactory webDriverFactory) {
		this.collectorFactories.put(webDriverFactory.getName(), webDriverFactory);
	}

	protected void unbindWebDriverFactory(WebDriverFactory webDriverFactory) {
		this.collectorFactories.remove(webDriverFactory.getName());
	}

}
