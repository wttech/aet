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

import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.exceptions.ProxyException;
import com.google.common.collect.Maps;
import java.util.Map;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    service = ProxyServerProvider.class,
    immediate = true,
    property = {Constants.SERVICE_VENDOR + "=Cognifide Ltd"}
)
public class ProxyServerProvider {

  private final Map<String, ProxyManager> collectorManagers = Maps.newConcurrentMap();

  public ProxyServerWrapper createProxy(String proxyName) throws ProxyException {
    ProxyManager proxyManager = collectorManagers.get(proxyName);
    if (proxyManager == null) {
      throw new ProxyException("Undefined ProxyManager with proxy name: " + proxyName);
    }
    try {
      return proxyManager.createProxy();
    } catch (ProxyException e) {
      String managerClass = proxyManager.getClass().getCanonicalName();
      throw new ProxyException("Unable to create ProxyServer with ProxyManager: " + managerClass,
          e);
    }
  }

  @Reference(
      service = ProxyManager.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE)
  protected void bindProxyManager(ProxyManager proxyManager) {
    this.collectorManagers.put(proxyManager.getName(), proxyManager);
  }

  protected void unbindProxyManager(ProxyManager proxyManager) {
    this.collectorManagers.remove(proxyManager.getName());
  }

}
