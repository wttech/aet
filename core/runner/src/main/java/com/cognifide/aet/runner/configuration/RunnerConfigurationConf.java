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

@ObjectClassDefinition(name = "AET Runner configuration", description = "AET Runner configuration")
public @interface RunnerConfigurationConf {

  int DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS = 120;

  String PARAM_FAILURE_TIMEOUT = "Failure timeout";

  String PARAM_MESSAGE_TTL = "Message TTL";

  String PARAM_URL_PACKAGE_SIZE = "Url package size";

  String PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE = "Max messages in collector queue";

  String PARAM_MAX_CONCURRENT_SUITES_PROCESSED_COUNT = "Max concurrent suites count";

  int DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT = 5;

  int DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS = 300;

  int DEFAULT_URL_PACKAGE_SIZE = 5;

  int DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE = 20;

  @AttributeDefinition(name = PARAM_FAILURE_TIMEOUT, description =
      "Time in seconds, after which suite processing will be interrupted if no notification was received in duration of this parameter. Default: "
          + DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS + " sec", type = AttributeType.LONG)
  long ft() default DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS;

  @AttributeDefinition(name = PARAM_MESSAGE_TTL, description =
      "Time in seconds after which messages will be thrown out of queues, default: "
          + DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS
          + " sec", type = AttributeType.LONG)
  long mttl() default DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS;


  @AttributeDefinition(name = PARAM_URL_PACKAGE_SIZE, description =
      "Defines how many links are being sent in one message. Each message is being processed by single CollectorListener. Default: "
          + DEFAULT_URL_PACKAGE_SIZE + " items", type = AttributeType.INTEGER)
  int urlPackageSize() default DEFAULT_URL_PACKAGE_SIZE;

  @AttributeDefinition(name = PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE, description =
      "Defines the maximum amount of messages in the collector queue. Default: "
          + DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE
          + " messages", type = AttributeType.INTEGER)
  int maxMessagesInCollectorQueue() default DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE;

  @AttributeDefinition(name = PARAM_MAX_CONCURRENT_SUITES_PROCESSED_COUNT, description =
      "Defines the maximum number of suites processed concurrently byt the Runner. Default: "
          + DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT
          + " messages", type = AttributeType.INTEGER)
  int maxConcurrentSuitesCount() default DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT;

}
