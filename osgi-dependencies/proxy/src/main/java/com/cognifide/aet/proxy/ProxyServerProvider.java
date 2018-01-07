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
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

@Service(ProxyServerProvider.class)
@Component(immediate = true, label = "AET Proxy Server Provider", description = "AET Proxy Server Provider")
@Properties({@Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd")})
public class ProxyServerProvider {

  @Reference(referenceInterface = ProxyManager.class, policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, bind = "bindProxyManager",
      unbind = "unbindProxyManager")
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

  protected void bindProxyManager(ProxyManager proxyManager) {
    this.collectorManagers.put(proxyManager.getName(), proxyManager);
  }

  protected void unbindProxyManager(ProxyManager proxyManager) {
    this.collectorManagers.remove(proxyManager.getName());
  }

}
