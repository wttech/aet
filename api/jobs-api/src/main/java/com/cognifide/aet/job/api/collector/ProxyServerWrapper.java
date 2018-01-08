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
package com.cognifide.aet.job.api.collector;

import com.cognifide.aet.job.api.exceptions.ProxyException;
import java.net.UnknownHostException;
import org.browsermob.core.har.Har;
import org.openqa.selenium.Proxy;

public interface ProxyServerWrapper {

  Har getHar();

  Har newHar(String initialPageRef);

  Proxy seleniumProxy() throws UnknownHostException;

  int getPort();

  void setCaptureHeaders(boolean captureHeaders);

  void setCaptureContent(boolean captureContent);

  void addHeader(String name, String value);

  void stop() throws ProxyException;

}
