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
package com.cognifide.aet.worker.listeners;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET Comparator Message Listener")
public @interface ComparatorMessageListenerImplConfig {

  String LISTENER_NAME_LABEL = "Comparator name";
  String LISTENER_NAME_DESC = "Name of comparator. Used in logs only";
  String LISTENER_NAME_DEFAULT_VALUE = "Comparator";

  String CONSUMER_QUEUE_NAME_LABEL = "Consumer queue name";
  String CONSUMER_QUEUE_NAME_DEFAULT_VALUE = "AET.comparatorJobs";

  String PRODUCER_QUEUE_NAME_LABEL = "Producer queue name";
  String PRODUCER_QUEUE_NAME_DEFAULT_VALUE = "AET.comparatorResults";

  String PREFETCH_SIZE_LABEL = "Prefetch size";
  String PREFETCH_SIZE_DESC = "http://activemq.apache.org/what-is-the-prefetch-limit-for.html";
  String PREFETCH_SIZE_DEFAULT_VALUE = "1";

  @AttributeDefinition(
      name = LISTENER_NAME_LABEL,
      description = LISTENER_NAME_DESC)
  String name() default LISTENER_NAME_DEFAULT_VALUE;

  @AttributeDefinition(
      name = CONSUMER_QUEUE_NAME_LABEL)
  String consumerQueueName() default CONSUMER_QUEUE_NAME_DEFAULT_VALUE;

  @AttributeDefinition(
      name = PRODUCER_QUEUE_NAME_LABEL)
  String producerQueueName() default PRODUCER_QUEUE_NAME_DEFAULT_VALUE;

  @AttributeDefinition(
      name = PREFETCH_SIZE_LABEL,
      description = PREFETCH_SIZE_DESC)
  String pf() default PREFETCH_SIZE_DEFAULT_VALUE;
}
