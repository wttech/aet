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
package com.cognifide.aet.rest;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = LockService.class, immediate = true)
public class LockService implements Serializable {

  private static final long serialVersionUID = -2029917823742862618L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LockService.class);

  private static final int LOCK_CACHE_TIMEOUT = 20000;

  private transient Cache<String, String> lockSet;

  @Activate
  public void start() {
    LOGGER.debug("Starting lock service");
    lockSet = CacheBuilder.newBuilder().expireAfterWrite(LOCK_CACHE_TIMEOUT, TimeUnit.MILLISECONDS)
        .build();
  }

  @Deactivate
  public void stop() {
    LOGGER.debug("Stopping lock service");
  }

  public void setLock(String key, String value) {
    lockSet.put(key, value);
  }

  public boolean isLockPresent(String key) {
    return null != lockSet.getIfPresent(key);
  }

  public synchronized boolean trySetLock(String key, String value) {
    if (null == lockSet.getIfPresent(key)) {
      lockSet.put(key, value);
      return true;
    }
    return false;
  }

  public Map<String, String> getAllLocks() {
    return ImmutableMap.copyOf(lockSet.asMap());
  }


}

