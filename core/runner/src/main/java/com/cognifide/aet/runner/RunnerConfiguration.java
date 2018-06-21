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
package com.cognifide.aet.runner;

import java.util.Map;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runner is an entry point for whole application, it main goal is to coordinate JMS communication
 * between workers. This class contains all necessary configuration for the runner bundle.
 */
@Service(RunnerConfiguration.class)
@Component(metatype = true, description = "AET Runner Configuration", label = "AET Runner Configuration")
public class RunnerConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerConfiguration.class);

  private static final int DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS = 120;

  private static final String PARAM_FAILURE_TIMEOUT = "ft";

  private static final String PARAM_MESSAGE_TTL = "mttl";

  private static final String PARAM_URL_PACKAGE_SIZE = "urlPackageSize";

  private static final String PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE = "maxMessagesInCollectorQueue";

  private static final String PARAM_MAX_CONCURRENT_SUITES_PROCESSED_COUNT = "maxConcurrentSuitesCount";

  private static final int DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT = 5;

  @Property(name = PARAM_FAILURE_TIMEOUT, label = "Failure timeout", description =
      "Time in seconds, after which suite processing will be interrupted if no notification was received in duration of this parameter. Default: "
          + DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS
          + " sec", longValue = DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS)
  private long ft = DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS;

  private static final int DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS = 300;

  @Property(name = PARAM_MESSAGE_TTL, label = "Message ttl", description =
      "Time in seconds after which messages will be thrown out of queues, default: "
          + DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS
          + " sec", longValue = DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS)
  private long mttl = DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS;

  private static final int DEFAULT_URL_PACKAGE_SIZE = 5;

  @Property(name = PARAM_URL_PACKAGE_SIZE, label = "URL Package Size", description =
      "Defines how many links are being sent in one message. Each message is being processed by single CollectorListener. Default: "
          + DEFAULT_URL_PACKAGE_SIZE + " items", intValue = DEFAULT_URL_PACKAGE_SIZE)
  private int urlPackageSize = DEFAULT_URL_PACKAGE_SIZE;

  private static final int DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE = 20;

  @Property(name = PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE, label = "Max Messages in Collector Queue", description =
      "Defines the maximum amount of messages in the collector queue. Default: "
          + DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE
          + " messages", intValue = DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE)
  private int maxMessagesInCollectorQueue = DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE;

  @Property(name = PARAM_MAX_CONCURRENT_SUITES_PROCESSED_COUNT, label = "Max Concurrent Suites Count", description =
      "Defines the maximum number of suites processed concurrently byt the Runner. Default: "
          + DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT
          + " messages", intValue = DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT)
  private int maxConcurrentSuitesCount = DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT;

  @Activate
  public void activate(Map<String, String> properties) {
    ft = PropertiesUtil.toLong(properties.get(PARAM_FAILURE_TIMEOUT),
        DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS);
    mttl = PropertiesUtil
        .toLong(properties.get(PARAM_MESSAGE_TTL), DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS) * 1000;
    urlPackageSize = PropertiesUtil.toInteger(properties.get(PARAM_URL_PACKAGE_SIZE),
        DEFAULT_URL_PACKAGE_SIZE);
    maxMessagesInCollectorQueue = PropertiesUtil.toInteger(
        properties.get(PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE),
        DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE);
    maxConcurrentSuitesCount = PropertiesUtil.toInteger(
        properties.get(PARAM_MAX_CONCURRENT_SUITES_PROCESSED_COUNT),
        DEFAULT_MAX_CONCURRENT_SUITES_PROCESSED_COUNT);

    LOGGER.info(
        "Runner configured with parameters: [ft: {} sec ; mttl: {} ; urlPackageSize: {} ; maxMessagesInCollectorQueue: {}; maxConcurrentSuitesCount: {}.]",
        ft, mttl, urlPackageSize, maxMessagesInCollectorQueue, maxConcurrentSuitesCount);
  }


  /**
   * @return time in seconds, test run will be interrupted if no response was received in duration
   * of this parameter.
   */
  public long getFt() {
    return ft;
  }


  /**
   * @return time in seconds after which messages will be thrown out of queues.
   */
  public long getMttl() {
    return mttl;
  }


  /**
   * @return the maximum amount of messages in the collector queue.
   */
  public int getMaxMessagesInCollectorQueue() {
    return maxMessagesInCollectorQueue;
  }

  /**
   * @return how many links are being sent in one message. Each message is being processed by single
   * CollectorListener.
   */
  public int getUrlPackageSize() {
    return urlPackageSize;
  }

  /**
   * @return how many suites can be processed concurrently byt the Runner.
   */
  public int getMaxConcurrentSuitesCount() {
    return maxConcurrentSuitesCount;
  }
}
