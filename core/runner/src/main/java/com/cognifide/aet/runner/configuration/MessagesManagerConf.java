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
package com.cognifide.aet.runner.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Messages Manager Configuration", description = "Service Configuration")
public @interface MessagesManagerConf {

  String DEFAULT_JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:11199/jmxrmi";

  @AttributeDefinition(name = "JMX_URL_PROPERTY_NAME", description = "ActiveMQ JMX endpoint URL",
      type = AttributeType.STRING)
  String jmxUrl() default DEFAULT_JMX_URL;

}
