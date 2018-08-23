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
package com.cognifide.aet.proxy.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(description = "AET REST Proxy Manager", name = "AET REST Proxy Manager")
public @interface RestProxyManagerConf {
  String SERVER_PROPERTY_NAME = "server";

  String PORT_PROPERTY_NAME = "port";

  String MAX_ATTEMPTS_PROPERTY_NAME = "maxAttempts";

  String ATTEMPTS_INTERVAL_PROPERTY_NAME = "attemptsInterval";

  String DEFAULT_SERVER = "localhost";

  @AttributeDefinition(name = SERVER_PROPERTY_NAME, description = "BrowserMob server address", type = AttributeType.STRING)
  String server() default DEFAULT_SERVER;

  @AttributeDefinition(name = PORT_PROPERTY_NAME, description = "BrowserMob API port", type = AttributeType.INTEGER)
  int port() default 8080;

  @AttributeDefinition(name = MAX_ATTEMPTS_PROPERTY_NAME, description = "Maximum number of attempts that will be performed to create single proxy", type = AttributeType.INTEGER)
  int maxAttempts() default 3;

  @AttributeDefinition(name = ATTEMPTS_INTERVAL_PROPERTY_NAME, description = "Wait interval for failed Attempts", type = AttributeType.INTEGER)
  int attemptsInterval() default 50;
}
