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
package com.cognifide.aet.runner.processing.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.TimeoutWatch;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.cognifide.aet.runner.scheduler.CollectorJobSchedulerService;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class StepManagerTest {

  protected static final String CORRELATION_ID = "0000001111111";

  @Mock
  protected JmsConnection connection;

  @Mock
  protected RunnerConfiguration runnerConfiguration;

  @Mock
  protected TimeoutWatch timeoutWatch;

  @Mock
  protected Session session;

  @Mock
  protected CollectorJobSchedulerService scheduler;

  @Mock
  protected SuiteIndexWrapper suiteIndexWrapper;

  @Mock
  protected Suite suite;

  @Mock
  protected MessageConsumer consumer;

  @Mock
  protected MessageProducer sender;

  @Mock
  protected ObjectMessage mockedMessage;

  @Mock
  protected Observer observer;

  protected StepManager tested;

  @Before
  public void setUp() throws Exception {
    when(connection.getJmsSession()).thenReturn(session);
    when(session.createQueue(anyString())).thenReturn(Mockito.mock(Queue.class));
    when(session.createConsumer(Matchers.<Destination>any(), anyString())).thenReturn(consumer);
    when(session.createProducer(Matchers.<Destination>any())).thenReturn(sender);
    when(session.createObjectMessage(Matchers.<Serializable>any())).thenReturn(mockedMessage);
    when(suiteIndexWrapper.get()).thenReturn(suite);
    when(suite.getCorrelationId()).thenReturn(CORRELATION_ID);
    when(runnerConfiguration.getMttl()).thenReturn(100L);
    when(runnerConfiguration.getUrlPackageSize()).thenReturn(2);
    tested = createTested();
    tested.addObserver(observer);
  }

  protected abstract StepManager createTested() throws JMSException;

  @Test
  public void closeConnections() throws Exception {
    tested.closeConnections();
    verify(session, times(1)).close();
    verify(consumer, times(1)).close();
    verify(sender, times(1)).close();
  }

  @Test
  public void onError_verifyObserversNotified() throws Exception {
    tested.onError(ProcessingError.collectingError("test"));
    verify(observer, times(1)).update(Matchers.<Observable>any(), any());
  }


  @Test
  public void onMessage_whenExceptionOccurs_expectObserversNotified() throws Exception {
    when(mockedMessage.getObject()).thenThrow(JMSException.class);
    tested.onMessage(mockedMessage);
    verify(observer, Mockito.times(1)).update(Matchers.<Observable>any(), any());
  }

  @Test
  public abstract void getQueueInName() throws Exception;

  @Test
  public abstract void getQueueOutName() throws Exception;

  @Test
  public abstract void getStepName() throws Exception;


}
