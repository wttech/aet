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
package com.cognifide.aet.worker.drivers;

/*
 * Copyright 2007-2009 Selenium committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.internal.SocketLock;

/**
 * Custom implementation of org.openqa.selenium.internal.Lock that has configurable delay between
 * socket checks.
 */
class AetSocketLock extends SocketLock {

  private static final int DELAY_BETWEEN_SOCKET_CHECKS = 600;

  private static final int ADDITIONAL_TIMEOUT_IN_MILLIS = 4000;

  private static final Object syncObject = new Object();

  private static final InetSocketAddress LOCALHOST = new InetSocketAddress("localhost",
      DEFAULT_PORT - 1);

  private final Socket lockSocket;

  private final InetSocketAddress address;

  AetSocketLock() {
    this.lockSocket = new Socket();
    this.address = LOCALHOST;
  }

  /**
   * @inheritDoc
   */
  @Override
  public void lock(long timeoutInMillis) {
    synchronized (syncObject) {
      // Calculate the 'exit time' for our wait loop.
      long maxWait = System.currentTimeMillis() + timeoutInMillis + ADDITIONAL_TIMEOUT_IN_MILLIS;

      // Attempt to acquire the lock until something goes wrong or we run out of time.
      do {
        try {
          if (isLockOnInetSocketAddressFree(address)) {
            return;
          }
          // Randomness or retry! Something from my past (Paul H) :
          // http://www.wattystuff.net/amateur/packet/whatispacket.htm (search for random in page)
          Thread.sleep((long) (DELAY_BETWEEN_SOCKET_CHECKS * Math.random()));
        } catch (InterruptedException | IOException e) {
          throw new WebDriverException(e);
        }
      } while (System.currentTimeMillis() < maxWait);

      throw new WebDriverException(String.format("Unable to bind to locking port %d within %d ms",
          address.getPort(), timeoutInMillis + ADDITIONAL_TIMEOUT_IN_MILLIS));
    }
  }

  @Override
  public void unlock() {
    try {
      if (lockSocket.isBound()) {
        lockSocket.close();
      }
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
  }

  /**
   * Test to see if the lock is free. Returns instantaneously.
   *
   * @param address the address to attempt to bind to
   * @return true if the lock is locked; false if it is not
   * @throws IOException if something goes catastrophically wrong with the socket
   */
  private boolean isLockOnInetSocketAddressFree(InetSocketAddress address) throws IOException {
    try {
      lockSocket.bind(address);
      return true;
    } catch (SocketException e) {
      return false;
    }
  }
}
