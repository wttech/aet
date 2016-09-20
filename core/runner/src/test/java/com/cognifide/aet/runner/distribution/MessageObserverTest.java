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
package com.cognifide.aet.runner.distribution;

import com.cognifide.aet.communication.api.queues.JmsConnection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.Serializable;
import java.util.Observer;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class MessageObserverTest<T extends Observer> {

  @Mock
  protected Destination destination;

  @Mock
  protected JmsConnection jmsConnection;

  @Mock
  protected MessageProducer resultsProducer;

  @Mock
  protected ObjectMessage messageObject;

  protected T tested;

  @Before
  public void setUp() throws Exception {
    Session session = Mockito.mock(Session.class);
    when(session.createProducer(destination)).thenReturn(resultsProducer);
    when(session.createObjectMessage(Matchers.<Serializable>any())).thenReturn(messageObject);
    when(jmsConnection.getJmsSession()).thenReturn(session);
  }

  @Test
  public void update_whenDataIsSupportedMessage_expectMessageSent() throws Exception {
    tested.update(null, getMessage());
    verify(resultsProducer, times(1)).send(Matchers.<Message>any());
  }

  protected abstract Object getMessage();
}
