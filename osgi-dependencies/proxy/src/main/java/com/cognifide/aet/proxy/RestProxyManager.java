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
package com.cognifide.aet.proxy;

import com.cognifide.aet.job.api.exceptions.ProxyException;
import com.cognifide.aet.proxy.configuration.RestProxyManagerConf;
import com.github.detro.browsermobproxyclient.BMPCProxy;
import com.github.detro.browsermobproxyclient.exceptions.BMPCUnableToConnectException;
import com.github.detro.browsermobproxyclient.manager.BMPCDefaultManager;
import com.google.common.collect.Sets;
import java.util.Set;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
@Designate(ocd = RestProxyManagerConf.class)
public class RestProxyManager implements ProxyManager {

  public static final String CREATE_PROXY_EXCEPTION_FORMAT = "Unable to create proxy! Reached max attempts: %d";

  private static final String NAME = "rest";

  private static final Logger LOG = LoggerFactory.getLogger(RestProxyManager.class);

  private final Set<RestProxyServer> proxies = Sets.newHashSet();

  private RestProxyManagerConf config;

  private BMPCDefaultManager manager;

  @Override
  public String getName() {
    return NAME;
  }

  @Activate
  public void activate(RestProxyManagerConf config) {
    this.config = config;
  }

  @Deactivate
  public void deactivate() {
    for (RestProxyServer proxy : proxies) {
      proxy.stop();
    }
  }

  @Override
  public RestProxyServer createProxy() throws ProxyException {
    RestProxyServer proxy;
    if (manager == null) {
      manager = new BMPCDefaultManager(config.server(), config.port());
    }
    proxy = new RestProxyServer(getBmpcProxy(), this);
    proxies.add(proxy);
    LOG.info("RestProxyServer created on server: " + getServer() + " ; port: " + proxy.getPort());
    return proxy;
  }

  private BMPCProxy getBmpcProxy() throws ProxyException {
    BMPCProxy bmpcProxy = null;
    int attempt = 0;
    while (bmpcProxy == null) {
      try {
        bmpcProxy = manager.createProxy();
      } catch (BMPCUnableToConnectException e) {
        if (++attempt >= config.maxAttempts()) {
          throw new ProxyException(
              String.format(CREATE_PROXY_EXCEPTION_FORMAT, config.maxAttempts()), e);
        }

        LOG.warn(
            String.format("Failed to create Proxy attempt %d/%d", attempt, config.maxAttempts()));
        try {
          Thread.sleep(config.attemptsInterval());
        } catch (InterruptedException e1) {
          Thread.currentThread().interrupt();
        }

      }
    }
    return bmpcProxy;
  }

  public void detach(RestProxyServer proxy) {
    proxies.remove(proxy);
  }

  public String getServer() {
    return config.server();
  }

  public int getPort() {
    return config.port();
  }

}
