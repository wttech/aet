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
package com.cognifide.aet.executor.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET Suite Executor Configuration" , description = "Executes received test suite")
public @interface SuiteExecutorConf {

  String MESSAGE_RECEIVE_TIMEOUT_PROPERTY_NAME = "messageReceiveTimeout";

  long DEFAULT_MESSAGE_RECEIVE_TIMEOUT = 300000L;

  @AttributeDefinition(name = MESSAGE_RECEIVE_TIMEOUT_PROPERTY_NAME, description = "ActiveMQ message receive timeout", type = AttributeType.LONG)
  long messageReceiveTimeout() default DEFAULT_MESSAGE_RECEIVE_TIMEOUT;

}
