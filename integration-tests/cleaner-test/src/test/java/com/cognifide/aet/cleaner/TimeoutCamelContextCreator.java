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
package com.cognifide.aet.cleaner;

import com.cognifide.aet.cleaner.camel.CamelContextCreator;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class TimeoutCamelContextCreator implements CamelContextCreator {

  private final long timeout;

  TimeoutCamelContextCreator(long timeoutMillis) {
    this.timeout = timeoutMillis;
  }

  @Override
  public CamelContext create() {
    CamelContext context = new DefaultCamelContext();
    context.getShutdownStrategy().setTimeUnit(TimeUnit.MILLISECONDS);
    context.getShutdownStrategy().setTimeout(timeout);
    context.getShutdownStrategy().setShutdownNowOnTimeout(true);
    return context;
  }
}
