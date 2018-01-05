/**
 * AET
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
package com.cognifide.aet.runner.modules;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import com.cognifide.aet.runner.distribution.RunnerMode;
import com.cognifide.aet.runner.distribution.SuiteExecutionSettings;
import com.cognifide.aet.runner.distribution.TestSuiteTask;
import com.cognifide.aet.runner.distribution.TestSuiteTaskFactory;
import com.cognifide.aet.runner.testsuitescope.TestSuiteScope;
import com.cognifide.aet.runner.testsuitescope.TestSuiteScoped;
import com.cognifide.aet.runner.util.MessagesManager;
import com.cognifide.aet.vs.MetadataDAO;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import javax.jms.Destination;

public class SimpleRunnerModule extends AbstractModule {

  private static final String API_QUEUE_IN = MessagesManager.createFullQueueName("runner-in");

  private static final String MAINTENANCE_QUEUE_IN = MessagesManager.createFullQueueName("maintenance-in");

  private final int maxMessagesInCollectorQueue;

  private final JmsConnection jmsConnection;

  private final MessagesManager messagesManager;

  private final RunnerMode runnerMode;

  private final MetadataDAO metadataDAO;

  private final long failureTimeout;

  private final long messageTimeToLive;

  private final int urlPackageSize;

  public SimpleRunnerModule(long failureTimeout, long messageTimeToLive, int urlPackageSize,
                            int maxMessagesInCollectorQueue, JmsConnection jmsConnection,
                            MessagesManager messagesManager, RunnerMode runnerMode,
                            MetadataDAO metadataDAO) {
    this.failureTimeout = failureTimeout;
    this.messageTimeToLive = messageTimeToLive;
    this.urlPackageSize = urlPackageSize;
    this.maxMessagesInCollectorQueue = maxMessagesInCollectorQueue;
    this.jmsConnection = jmsConnection;
    this.messagesManager = messagesManager;
    this.runnerMode = runnerMode;
    this.metadataDAO = metadataDAO;

  }

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("API in")).toInstance(API_QUEUE_IN);
    bind(String.class).annotatedWith(Names.named("MAINTENANCE in")).toInstance(MAINTENANCE_QUEUE_IN);

    bind(Long.class).annotatedWith(Names.named("failureTimeout")).toInstance(failureTimeout);
    bind(Long.class).annotatedWith(Names.named("messageTimeToLive")).toInstance(messageTimeToLive);
    bind(Integer.class).annotatedWith(Names.named("urlPackageSize")).toInstance(urlPackageSize);
    bind(Integer.class).annotatedWith(Names.named("maxMessagesInCollectorQueue")).toInstance(
            maxMessagesInCollectorQueue);

    bind(JmsConnection.class).toInstance(jmsConnection);

    bind(RunnerMode.class).toInstance(runnerMode);

    bind(MessagesManager.class).toInstance(messagesManager);

    bind(MetadataDAO.class).toInstance(metadataDAO);

    install(new FactoryModuleBuilder().implement(TestSuiteTask.class, TestSuiteTask.class).build(
            TestSuiteTaskFactory.class));

    TestSuiteScope testSuiteScope = new TestSuiteScope();
    bindScope(TestSuiteScoped.class, testSuiteScope);
    bind(TestSuiteScope.class).annotatedWith(Names.named("testSuiteScope")).toInstance(testSuiteScope);

    bind(Destination.class).toProvider(TestSuiteScope.<Destination>seededKeyProvider()).in(
            TestSuiteScoped.class);
    bind(SuiteExecutionSettings.class).toProvider(
            TestSuiteScope.<SuiteExecutionSettings>seededKeyProvider()).in(TestSuiteScoped.class);
    bind(SuiteIndexWrapper.class).toProvider(TestSuiteScope.<SuiteIndexWrapper>seededKeyProvider()).in(TestSuiteScoped.class);
  }

}
