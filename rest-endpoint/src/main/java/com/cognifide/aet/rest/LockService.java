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


import com.cognifide.aet.rest.helpers.LockType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component(service = LockService.class, immediate = true)
public class LockService implements Serializable {

  private static final long serialVersionUID = -2029917823742862618L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LockService.class);

  private static final int LOCK_CACHE_TIMEOUT = 30000;

  private static final int AVAILABLE_SLOTS = 100;

  private transient Semaphore semaphore;

  private transient Cache<String, String> lockSet;

  private boolean globalLock;

  @Activate
  public void start() {
    LOGGER.debug("Starting lock service");
    lockSet = CacheBuilder.newBuilder().expireAfterWrite(LOCK_CACHE_TIMEOUT, TimeUnit.MILLISECONDS)
        .build();
    semaphore = new Semaphore(AVAILABLE_SLOTS);
  }

  @Deactivate
  public void stop() {
    LOGGER.debug("Stopping lock service");
  }

  public void setLock(String key, String value) {
    lockSet.put(key, value);
  }

  public void setGlobalLock() {
    globalLock = true;
  }

  public void unsetGlobalLock() {
    globalLock = false;
  }

  public boolean isLockPresent(String key) {
    return globalLock || null != lockSet.getIfPresent(key);
  }

  public synchronized LockType trySetLock(String key, String value) {
    if (null == lockSet.getIfPresent(key)) {
      if (!globalLock) {
        if (semaphore.tryAcquire()) {
          lockSet.put(key, value);
          return LockType.LOCK;
        } else {
          return LockType.TOO_MANY_TESTS;
        }
      } else {
        return LockType.DATABASE_LOCK;
      }
    }
    return LockType.SUITE_LOCK;
  }

  public synchronized void acquireSlot() throws InterruptedException {
    this.semaphore.acquire();
  }


  public synchronized void releaseSlot() {
    this.semaphore.release();
  }

  public void acquireUninterruptiblyAllSlots() {
    this.semaphore.acquireUninterruptibly(AVAILABLE_SLOTS);
  }

  public void releaseAllSlots() {
    this.semaphore.release(AVAILABLE_SLOTS);
  }

  public Map<String, String> getAllLocks() {
    return ImmutableMap.copyOf(lockSet.asMap());
  }

  // for tests
  public void clearCacheAndChangeCacheTimeout(long timeout) {
    this.lockSet = CacheBuilder.newBuilder()
            .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
            .build();
  }
}

