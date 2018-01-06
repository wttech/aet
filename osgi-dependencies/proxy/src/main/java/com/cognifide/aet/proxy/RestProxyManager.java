/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.proxy;

import com.cognifide.aet.job.api.exceptions.ProxyException;
import com.github.detro.browsermobproxyclient.BMPCProxy;
import com.github.detro.browsermobproxyclient.exceptions.BMPCUnableToConnectException;
import com.github.detro.browsermobproxyclient.manager.BMPCDefaultManager;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Component(immediate = true, metatype = true, description = "AET REST Proxy Manager", label = "AET REST Proxy Manager")
public class RestProxyManager implements ProxyManager {

  public static final String CREATE_PROXY_EXCEPTION_FORMAT = "Unable to create proxy! Reached max attempts: %d";

  private static final String NAME = "rest";

  private static final String SERVER_PROPERTY_NAME = "server";

  private static final String PORT_PROPERTY_NAME = "port";

  private static final String MAX_ATTEMPTS_PROPERTY_NAME = "maxAttempts";

  private static final String ATTEMPTS_INTERVAL_PROPERTY_NAME = "attemptsInterval";

  private static final String DEFAULT_SERVER = "localhost";

  private static final int DEFAULT_PORT = 9272;

  private static final int DEFAULT_MAX_ATTEMPTS = 3;

  private static final int DEFAULT_ATTEMPTS_INTERVAL = 50;

  private static final Logger LOG = LoggerFactory.getLogger(RestProxyManager.class);

  private final Set<RestProxyServer> proxies = Sets.newHashSet();

  @Property(name = SERVER_PROPERTY_NAME, label = "Server", description = "BrowserMob server address", value = DEFAULT_SERVER)
  private String server;

  @Property(name = PORT_PROPERTY_NAME, label = "Port", description = "BrowserMob API port", intValue = 8080)
  private int port;

  @Property(name = MAX_ATTEMPTS_PROPERTY_NAME, label = "Max attempts", description = "Maximum number of attempts that will be performed to create single proxy", intValue = 3)
  private int maxAttempts;

  @Property(name = ATTEMPTS_INTERVAL_PROPERTY_NAME, label = "Attempts interval", description = "Wait interval for failed Attempts", intValue = 50)
  private int attemptsInterval;

  private BMPCDefaultManager manager;

  @Override
  public String getName() {
    return NAME;
  }

  @Activate
  public void activate(Map<String, ?> properties) {
    port = PropertiesUtil.toInteger(properties.get(PORT_PROPERTY_NAME), DEFAULT_PORT);
    server = PropertiesUtil.toString(properties.get(SERVER_PROPERTY_NAME), DEFAULT_SERVER);
    maxAttempts = PropertiesUtil
            .toInteger(properties.get(MAX_ATTEMPTS_PROPERTY_NAME), DEFAULT_MAX_ATTEMPTS);
    attemptsInterval = PropertiesUtil
            .toInteger(properties.get(ATTEMPTS_INTERVAL_PROPERTY_NAME), DEFAULT_ATTEMPTS_INTERVAL);
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
      manager = new BMPCDefaultManager(server, port);
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
        if (++attempt >= maxAttempts) {
          throw new ProxyException(String.format(CREATE_PROXY_EXCEPTION_FORMAT, maxAttempts), e);
        }

        LOG.warn(String.format("Failed to create Proxy attempt %d/%d", attempt, maxAttempts));
        try {
          Thread.sleep(attemptsInterval);
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
    return server;
  }

  public int getPort() {
    return port;
  }

}
