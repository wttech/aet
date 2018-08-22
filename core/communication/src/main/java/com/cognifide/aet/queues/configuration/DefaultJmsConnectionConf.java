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
package com.cognifide.aet.queues.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET JMS Connection", description = "AET JMS Connection")
public @interface DefaultJmsConnectionConf {

  String DEFAULT_BROKER_URL_PARAM = "failover:tcp://localhost:61616";

  String DEFAULT_USERNAME_PARAM = "karaf";

  String DEFAULT_PASSWORD_PARAM = "karaf";

  @AttributeDefinition(
      name = "brokerUrl",
      description = "URL of the broker, no trailing '/', see http://activemq.apache.org/uri-protocols.html",
      type = AttributeType.STRING)
  String url() default DEFAULT_BROKER_URL_PARAM;

  @AttributeDefinition(
      name = "username",
      description = "ActiveMQ username",
      type = AttributeType.STRING)
  String username() default DEFAULT_USERNAME_PARAM;

  @AttributeDefinition(
      name = "password",
      description = "ActiveMQ password",
      type = AttributeType.STRING)
  String password() default DEFAULT_PASSWORD_PARAM;

}
