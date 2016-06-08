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

import java.util.Set;

import net.lightbody.bmp.proxy.ProxyServer;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.job.api.exceptions.ProxyException;
import com.google.common.collect.Sets;

@Service
@Component(immediate = true, description = "AET Embedded Proxy Manager", label = "AET Embedded Proxy Manager")
public class EmbeddedProxyManager implements ProxyManager {

	private static final String NAME = "embedded";

	private static final String SERVER = "localhost";

	private static final Logger LOG = LoggerFactory.getLogger(EmbeddedProxyManager.class);

	private final Set<EmbeddedProxyServer> proxies = Sets.newHashSet();

	public String getName() {
		return NAME;
	}

	public EmbeddedProxyServer createProxy(int port) throws ProxyException {
		EmbeddedProxyServer proxy = new EmbeddedProxyServer(new ProxyServer(port), this);
		proxies.add(proxy);
		LOG.info("EmbeddedProxyServer created on port: " + proxy.getPort());
		return proxy;
	}

	@Deactivate
	public void deactivate() {
		try {
			for (EmbeddedProxyServer proxy : proxies) {
				proxy.stop();
			}
		} catch (ProxyException e) {
			LOG.error("Unable to stop EmbeddedProxyServe", e);
		}
	}

	public void detach(EmbeddedProxyServer embeddedProxyServer) {
		proxies.remove(embeddedProxyServer);
	}

	public String getServer() {
		return SERVER;
	}

}
