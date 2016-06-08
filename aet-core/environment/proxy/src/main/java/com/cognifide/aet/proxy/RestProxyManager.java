/*
 * Cognifide AET :: Proxy
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
package com.cognifide.aet.proxy;

import com.cognifide.aet.job.api.exceptions.ProxyException;
import com.github.detro.browsermobproxyclient.exceptions.BMPCUnableToConnectException;
import com.github.detro.browsermobproxyclient.manager.BMPCDefaultManager;
import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

@Service
@Component(immediate = true, metatype = true, description = "AET REST Proxy Manager", label = "AET REST Proxy Manager")
public class RestProxyManager implements ProxyManager {

	private static final String NAME = "rest";

	private static final String SERVER_PROPERTY_NAME = "server";

	private static final String PORT_PROPERTY_NAME = "port";

	private static final String DEFAULT_SERVER = "localhost";

	private static final int DEFAULT_PORT = 9272;

	@Property(name = SERVER_PROPERTY_NAME, label = "Server", description = "BrowserMob server address", value = DEFAULT_SERVER)
	private String server;

	@Property(name = PORT_PROPERTY_NAME, label = "Port", description = "BrowserMob API port", intValue = 9272)
	private int port;

	private BMPCDefaultManager manager;

	private final Set<RestProxyServer> proxies = Sets.newHashSet();

	private static final Logger LOG = LoggerFactory.getLogger(RestProxyManager.class);

	public String getName() {
		return NAME;
	}

	@Activate
	public void activate(Map<String, ?> properties) {
		port = PropertiesUtil.toInteger(properties.get(PORT_PROPERTY_NAME), DEFAULT_PORT);
		server = PropertiesUtil.toString(properties.get(SERVER_PROPERTY_NAME), DEFAULT_SERVER);

	}

	@Deactivate
	public void deactivate() {
		for (RestProxyServer proxy : proxies) {
			proxy.stop();
		}
	}

	public RestProxyServer createProxy(int port) throws ProxyException {
		return createProxy();
	}

	public RestProxyServer createProxy() throws ProxyException {
		RestProxyServer proxy;
		try {
			if (manager == null) {
				manager = new BMPCDefaultManager(server, port);
			}
			proxy = new RestProxyServer(manager.createProxy(), this);
			proxies.add(proxy);
			LOG.info("RestProxyServer created on server: " + getServer() + " ; port: " + proxy.getPort());
		} catch (BMPCUnableToConnectException e) {
			throw new ProxyException("Unable to connect!", e);
		}
		return proxy;
	}

	public void detach(RestProxyServer proxy) {
		proxies.remove(proxy);
	}

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

}
