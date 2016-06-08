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

import java.net.UnknownHostException;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.Proxy;

import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.exceptions.ProxyException;

public class EmbeddedProxyServer implements ProxyServerWrapper {

	private ProxyServer server;

	private EmbeddedProxyManager proxyManager;

	public EmbeddedProxyServer(ProxyServer server, EmbeddedProxyManager embeddedProxyManager)
			throws ProxyException {
		this.server = server;
		this.proxyManager = embeddedProxyManager;
		try {
			server.start();
		} catch (Exception e) {
			throw new ProxyException("Unable to start EmbeddedProxyServer ", e);
		}
	}

	@Override
	public Har getHar() {
		return server.getHar();
	}

	@Override
	public Har newHar(String initialPageRef) {
		return server.newHar(initialPageRef);
	}

	@Override
	public Proxy seleniumProxy() throws UnknownHostException {
		return server.seleniumProxy()
				.setHttpProxy(String.format("%s:%d", proxyManager.getServer(), getPort()))
				.setSslProxy(String.format("%s:%d", proxyManager.getServer(), getPort()));
	}

	@Override
	public int getPort() {
		return server.getPort();
	}

	@Override
	public void setCaptureHeaders(boolean captureHeaders) {
		server.setCaptureHeaders(captureHeaders);
	}

	@Override
	public void setCaptureContent(boolean captureContent) {
		server.setCaptureContent(captureContent);
	}

	@Override
	public void addHeader(String name, String value) {
		server.addHeader(name, value);
	}

	@Override
	public void stop() throws ProxyException {
		try {
			server.stop();
			proxyManager.detach(this);
		} catch (Exception e) {
			throw new ProxyException("Unable to stop EmbeddedProxyServer ", e);
		}
	}

}
