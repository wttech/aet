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

import com.cognifide.aet.runner.configuration.RunnerConfigurationConf;
import java.util.concurrent.TimeUnit;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runner is an entry point for whole application, it main goal is to coordinate JMS communication
 * between workers. This class contains all necessary configuration for the runner bundle.
 */
@Component(service = RunnerConfiguration.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = RunnerConfigurationConf.class)
public class RunnerConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerConfiguration.class);

  private RunnerConfigurationConf config;

  @Activate
  public void activate(RunnerConfigurationConf config) {
    this.config = config;
    LOGGER.info(
        "Runner configured with parameters: [ft: {} sec ; mttl: {} sec ; urlPackageSize: {} ; maxMessagesInCollectorQueue: {}; maxConcurrentSuitesCount: {}.]",
        config.ft(), config.mttl(), config.urlPackageSize(), config.maxMessagesInCollectorQueue(),
        config.maxConcurrentSuitesCount());

  }

  /**
   * @return time in seconds, test run will be interrupted if no response was received in duration
   * of this parameter.
   */
  public long getFt() {
    return config.ft();
  }


  /**
   * @return time in milliseconds after which messages will be thrown out of queues.
   */
  public long getMttl() {
    return TimeUnit.SECONDS.toMillis(config.mttl());
  }


  /**
   * @return the maximum amount of messages in the collector queue.
   */
  public int getMaxMessagesInCollectorQueue() {
    return config.maxMessagesInCollectorQueue();
  }

  /**
   * @return how many links are being sent in one message. Each message is being processed by single
   * CollectorListener.
   */
  public int getUrlPackageSize() {
    return config.urlPackageSize();
  }

  /**
   * @return how many suites can be processed concurrently byt the Runner.
   */
  public int getMaxConcurrentSuitesCount() {
    return config.maxConcurrentSuitesCount();
  }
}
