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
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET Workers Listener Service")
public @interface WorkersListenersServiceConfig {

  String COLLECTORS_NO_ENV = "COLLECTORS_NO";
  String COMPARATORS_NO_ENV = "COMPARATORS_NO";

  String COLLECTOR_PREFETCH_SIZE_LABEL = "Collectors prefetch size";
  String COLLECTOR_PREFETCH_SIZE_DESC = "http://activemq.apache.org/what-is-the-prefetch-limit-for.html";
  String COLLECTOR_PREFETCH_SIZE_DEFAULT_VALUE = "1";

  String COLLECTOR_INSTANCES_NO_LABEL = "Number of collector instances";
  String COLLECTOR_INSTANCES_NO_DESC = "Might be overwritten by env variable" + COLLECTORS_NO_ENV;
  int COLLECTOR_INSTANCES_NO_DEFAULT_VALUE = 5;

  String COMPARATOR_PREFETCH_SIZE_LABEL = "Comparators prefetch size";
  String COMPARATOR_PREFETCH_SIZE_DESC = "http://activemq.apache.org/what-is-the-prefetch-limit-for.html";
  String COMPARATOR_PREFETCH_SIZE_DEFAULT_VALUE = "1";

  String COMPARATOR_INSTANCES_NO_LABEL = "Number of comparator instances";
  String COMPARATOR_INSTANCES_NO_DESC = "Might be overwritten by env variable" + COMPARATORS_NO_ENV;
  int COMPARATOR_INSTANCES_NO_DEFAULT_VALUE = 5;

  @AttributeDefinition(
      name = COLLECTOR_PREFETCH_SIZE_LABEL,
      description = COLLECTOR_PREFETCH_SIZE_DESC)
  String collectorPrefetchSize() default COLLECTOR_PREFETCH_SIZE_DEFAULT_VALUE;

  @AttributeDefinition(name = COLLECTOR_INSTANCES_NO_LABEL,
      description = COLLECTOR_INSTANCES_NO_DESC,
      type = AttributeType.INTEGER)
  int collectorInstancesNo() default COLLECTOR_INSTANCES_NO_DEFAULT_VALUE;

  @AttributeDefinition(
      name = COMPARATOR_PREFETCH_SIZE_LABEL,
      description = COMPARATOR_PREFETCH_SIZE_DESC)
  String comparatorPrefetchSize() default COMPARATOR_PREFETCH_SIZE_DEFAULT_VALUE;

  @AttributeDefinition(name = COMPARATOR_INSTANCES_NO_LABEL,
      description = COMPARATOR_INSTANCES_NO_DESC,
      type = AttributeType.INTEGER)
  int comparatorInstancesNo() default COMPARATOR_INSTANCES_NO_DEFAULT_VALUE;

}
