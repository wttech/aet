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
/*
 * Copyright [2016] [http://bmp.lightbody.net/]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.browsermob.core.har;

public class HarTimings {

  private long blocked;
  private long dns;
  private long connect;
  private long send;
  private long wait;
  private long receive;

  public HarTimings() {
  }

  public HarTimings(long blocked, long dns, long connect, long send, long wait, long receive) {
    this.blocked = blocked;
    this.dns = dns;
    this.connect = connect;
    this.send = send;
    this.wait = wait;
    this.receive = receive;
  }

  public Long getBlocked() {
    return blocked;
  }

  public void setBlocked(Long blocked) {
    this.blocked = blocked;
  }

  public Long getDns() {
    return dns;
  }

  public void setDns(Long dns) {
    this.dns = dns;
  }

  public Long getConnect() {
    return connect;
  }

  public void setConnect(Long connect) {
    this.connect = connect;
  }

  public long getSend() {
    return send;
  }

  public void setSend(long send) {
    this.send = send;
  }

  public long getWait() {
    return wait;
  }

  public void setWait(long wait) {
    this.wait = wait;
  }

  public long getReceive() {
    return receive;
  }

  public void setReceive(long receive) {
    this.receive = receive;
  }
}
