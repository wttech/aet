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
package com.cognifide.aet.job.common.collectors.source.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET Source Collector Factory", description = "AET Source Collector Factory")
public @interface SourceCollectorFactoryConf {

  String SOURCE_COLLECTOR_TIMEOUT_VALUE = "sourceCollectorTimeoutValue";

  int DEFAULT_TIMEOUT = 20000;

  @AttributeDefinition(
      name = SOURCE_COLLECTOR_TIMEOUT_VALUE,
      description =  "Timeout value for source collector [ms]",
      type = AttributeType.INTEGER)
  int sourceCollectorTimeoutValue() default  DEFAULT_TIMEOUT;

}
