/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.runner.main;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.distribution.RunnerMessageListener;
import com.cognifide.aet.runner.distribution.RunnerMode;
import com.cognifide.aet.runner.modules.SimpleRunnerModule;
import com.cognifide.aet.runner.util.MessagesManager;
import com.cognifide.aet.vs.MetadataDAO;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Runner is an entry point for whole application, it main goal is to coordinate JMS communication between
 * workers.
 */
@Component(immediate = true, metatype = true, description = "AET Runner", label = "AET Runner")
public class Runner {

  private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

  private static final int DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS = 120;

  private static final String PARAM_FAILURE_TIMEOUT = "ft";

  private static final String PARAM_MESSAGE_TTL = "mttl";

  private static final String PARAM_URL_PACKAGE_SIZE = "urlPackageSize";

  private static final String PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE = "maxMessagesInCollectorQueue";

  private static final String RUNNER_MODE = "runnerMode";

  @Property(name = PARAM_FAILURE_TIMEOUT, label = "Failure timeout", description = "Time in seconds, test run will be interrupted if no response was received in duration of this parameter. Default: "
          + DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS + " sec", longValue = DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS)
  private long ft = DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS;

  private static final int DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS = 300;

  @Property(name = PARAM_MESSAGE_TTL, label = "Message ttl", description = "Time in seconds after which messages will be thrown out of queues, default: "
          + DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS + " sec", longValue = DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS)
  private long mttl = DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS;

  private static final int DEFAULT_URL_PACKAGE_SIZE = 5;

  @Property(name = PARAM_URL_PACKAGE_SIZE, label = "URL Package Size", description = "Defines how many links are being sent in one message. Each message is being processed by single CollectorListener. Default: "
          + DEFAULT_URL_PACKAGE_SIZE + " items", intValue = DEFAULT_URL_PACKAGE_SIZE)
  private int urlPackageSize = DEFAULT_URL_PACKAGE_SIZE;

  private static final int DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE = 20;

  @Property(name = PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE, label = "Max Messages in Collector Queue", description = "Defines the maximum amount of messages in the collector queue. Default: "
          + DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE + " messages", intValue = DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE)
  private int maxMessagesInCollectorQueue = DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE;

  @Property(name = RUNNER_MODE, label = "Runner mode", options = {
          @PropertyOption(name = "online", value = "online"),
          @PropertyOption(name = "maintenance", value = "maintenance"),
          @PropertyOption(name = "offline", value = "offline")}, value = "online", description = "Runner mode: online - listening to AET.runner-in queue only, maintenance - listening to AET.runner-in and AET.maintenance-in queues (running only from AET.maintenance-in queue), offline - listening only to AET-maintenance-in queue.")
  private RunnerMode runnerMode;
  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private MessagesManager messagesManager;

  @Reference
  private MetadataDAO metadataDAO;

  private RunnerMessageListener messageListener;

  @Activate
  public void activate(Map properties) {
    ft = PropertiesUtil.toLong(properties.get(PARAM_FAILURE_TIMEOUT),
            DEFAULT_TASK_RUN_FAILURE_TIMEOUT_SECONDS);
    mttl = PropertiesUtil.toLong(properties.get(PARAM_MESSAGE_TTL), DEFAULT_MESSAGE_TIME_TO_LIVE_SECONDS) * 1000;
    urlPackageSize = PropertiesUtil.toInteger(properties.get(PARAM_URL_PACKAGE_SIZE),
            DEFAULT_URL_PACKAGE_SIZE);
    maxMessagesInCollectorQueue = PropertiesUtil.toInteger(
            properties.get(PARAM_MAX_MESSAGES_IN_COLLECTOR_QUEUE),
            DEFAULT_MAX_MESSAGES_IN_COLLECTOR_QUEUE);

    runnerMode = RunnerMode.valueOf(PropertiesUtil.toString(properties.get(RUNNER_MODE), "online")
            .toUpperCase());

    LOG.info(
            "Running with parameters: [ft: {} sec ; mttl: {} ; urlPackageSize: {} ; maxMessagesInCollectorQueue: {}; runnerMode: {}.]",
            ft, mttl, urlPackageSize, maxMessagesInCollectorQueue, runnerMode);

    Injector injector = Guice
            .createInjector(new SimpleRunnerModule(ft, mttl, urlPackageSize, maxMessagesInCollectorQueue,
                    jmsConnection, messagesManager, runnerMode, metadataDAO));
    messageListener = injector.getInstance(RunnerMessageListener.class);
  }

  @Deactivate
  public void deactivate() {
    if (messageListener != null) {
      messageListener.close();
    }
  }

}
