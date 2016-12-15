/**
 * Automated Exploratory Tests
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
package com.cognifide.aet.common;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.jcabi.log.Logger;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import java.io.IOException;

public class LockClient implements Runnable {

  public static final String LOCK_PATH = "/lock";
  public static final int BEAT_INTERVAL = 5000;
  public static final String VALUE_PARAM_NAME = "value";
  private final String key;
  private final String url;
  private final String correlationId;
  private final RunnerTerminator runnerTerminator;

  public LockClient(Suite suite, String url, RunnerTerminator runnerTerminator) {
    this.key = suite.getSuiteIdentifier();
    this.correlationId = suite.getCorrelationId();
    this.url = url;
    this.runnerTerminator = runnerTerminator;
  }

  public void tryToSetLock() throws AETException {
    try {
      Response response = Request.Post(url + LOCK_PATH + "/" + key).bodyForm(Form.form().add(VALUE_PARAM_NAME, correlationId).build()).execute();
      if (response.returnResponse().getStatusLine().getStatusCode() == 200) {
        startHeartBeat();
      } else {
        throw new AETException("\n***\n \nSuite is currently locked \n\n***");
      }
    } catch (IOException e) {
      throw new AETException("\n***\n\nUnable to check lock. \n\n ***", e);
    }
  }

  private void startHeartBeat() throws AETException {
    new Thread(this).start();
  }

  @Override
  public void run() {
    boolean running = true;
    while (running) {
      try {
        Thread.sleep(BEAT_INTERVAL);
        Request.Put(url + LOCK_PATH + "/" + key).bodyForm(Form.form().add(VALUE_PARAM_NAME, correlationId).build()).execute().discardContent();
      } catch (Exception e) {
        running = false;
        runnerTerminator.update();
        Logger.error(this, e.getMessage());
      }
    }
  }
}
