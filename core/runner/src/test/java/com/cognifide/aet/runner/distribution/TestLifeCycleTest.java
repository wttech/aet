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
package com.cognifide.aet.runner.distribution;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.RemoveInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestLifeCycleTest {

  @Mock
  protected MessageProducer resultsProducer;
  @Mock
  protected ObjectMessage messageObject;
  @Mock
  private SuiteExecutor suiteExecutor;
  @Mock
  private Destination destination;
  @Mock
  private JmsConnection jmsConnection;
  @Mock
  private SuiteIndexWrapper suiteIndexWrapper;

  private TestLifeCycle tested;

  @Before
  public void setUp() throws Exception {
    tested = new TestLifeCycle(jmsConnection, destination, suiteExecutor, suiteIndexWrapper);
  }

  @Test
  public void constructor_withActiveMQDestination_expectAdvisoryConsumerCreated() throws Exception {
    ActiveMQDestination activeMQDestination = Mockito.mock(ActiveMQDestination.class);
    when(activeMQDestination.getPhysicalName()).thenReturn("");
    MessageConsumer messageConsumer = Mockito.mock(MessageConsumer.class);
    Session session = Mockito.mock(Session.class);
    when(jmsConnection.getJmsSession()).thenReturn(session);
    when(session.createConsumer(Matchers.<ActiveMQTopic>any())).thenReturn(messageConsumer);

    new TestLifeCycle(jmsConnection, activeMQDestination, suiteExecutor, suiteIndexWrapper);

    verify(session, times(1)).createConsumer(Matchers.<ActiveMQTopic>any());
  }

  @Test
  public void run_expectSuiteExecutorExecuted() throws Exception {
    tested.run();
    verify(suiteExecutor, times(1)).execute();
  }

  @Test
  public void run_expectSuiteExecutorCleanedUp() throws Exception {
    tested.run();
    verify(suiteExecutor, times(1)).cleanup();
  }

  @Test
  public void run_exceptionThrown_expectSuiteExecutorCleanedUp() throws Exception {
    doThrow(new NullPointerException("Fake exception")).when(suiteExecutor).execute();
    tested.run();
    verify(suiteExecutor, times(1)).cleanup();
  }

  @Test
  public void onMessage_withNotRemovedConsumer_expectNotAbortedRun() throws Exception {
    ActiveMQMessage message = Mockito.mock(ActiveMQMessage.class);
    RemoveInfo removeInfo = Mockito.mock(RemoveInfo.class);
    when(message.getDataStructure()).thenReturn(removeInfo);
    when(removeInfo.isConsumerRemove()).thenReturn(false);
    tested.onMessage(message);
    verify(suiteExecutor, times(0)).abort();
  }

  @Test
  public void onMessage_withRemovedConsumer_expectAbortedRun() throws Exception {
    ActiveMQMessage message = Mockito.mock(ActiveMQMessage.class);
    RemoveInfo removeInfo = Mockito.mock(RemoveInfo.class);
    when(message.getDataStructure()).thenReturn(removeInfo);
    when(removeInfo.isConsumerRemove()).thenReturn(true);
    tested.run();
    tested.onMessage(message);
    verify(suiteExecutor, times(1)).abort();
  }

}
