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
package com.cognifide.aet.job.common.modifiers.executejavascript.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "External Snippet Http Client Configuration")
public @interface ExternalSnippetHttpClientConf {

  long DEFAULT_CONNECTION_TTL = 60L;

  int DEFAULT_MAX_CONCURRENT_CONNECTIONS = 50;

  @AttributeDefinition(
      name = "Connection TTL",
      description = "Time in seconds that defines maximum life span of persistent connections regardless of their expiration setting",
      type = AttributeType.LONG)
  long connectionTtl() default DEFAULT_CONNECTION_TTL;

  @AttributeDefinition(
      name = "Maximum Concurrent Connections",
      description = "The maximal number of concurrent connections this service can provide. Connections over this limit are rejected.",
      type = AttributeType.INTEGER)
  int maxConcurrentConnections() default DEFAULT_MAX_CONCURRENT_CONNECTIONS;


}
