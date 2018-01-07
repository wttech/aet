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
package com.cognifide.aet.queues;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.junit.Test;
import org.mockito.Mockito;

public class JmsUtilsTest {

  @Test
  public void closeQuietly_whenProducerIsNull_expectNoAction() throws Exception {
    JmsUtils.closeQuietly((MessageProducer) null);
  }

  @Test
  public void closeQuietly_whenProducerIsNotNull_expectClose() throws Exception {
    MessageProducer mockedProducer = Mockito.mock(MessageProducer.class);
    JmsUtils.closeQuietly(mockedProducer);
    verify(mockedProducer).close();
  }

  @Test
  public void closeQuietly_whenConsumerIsNull_expectNoAction() throws Exception {
    JmsUtils.closeQuietly((MessageConsumer) null);
  }

  @Test
  public void closeQuietly_whenConsumerIsNotNull_expectClose() throws Exception {
    MessageConsumer mockedConsumer = Mockito.mock(MessageConsumer.class);
    JmsUtils.closeQuietly(mockedConsumer);
    verify(mockedConsumer).close();
  }

  @Test
  public void closeQuietly_whenSessionIsNull_expectNoAction() throws Exception {
    JmsUtils.closeQuietly((Session) null);
  }

  @Test
  public void closeQuietly_whenSessionIsNotNull_expectClose() throws Exception {
    Session mockedSession = Mockito.mock(Session.class);
    JmsUtils.closeQuietly(mockedSession);
    verify(mockedSession).close();
  }

  @Test
  public void closeQuietly_whenConnectionIsNull_expectNoAction() throws Exception {
    JmsUtils.closeQuietly((Connection) null);
  }

  @Test
  public void closeQuietly_whenConnectionIsNotNull_expectClose() throws Exception {
    Connection mockedConnection = Mockito.mock(Connection.class);
    JmsUtils.closeQuietly(mockedConnection);
    verify(mockedConnection).close();
  }

  @Test
  public void getJMSCorrelationID_expectCorrelationIdFromMessage() throws Exception {
    Message message = Mockito.mock(Message.class);
    final String correlationId = "testCorrelationId";
    when(message.getJMSCorrelationID()).thenReturn(correlationId);
    final String jmsCorrelationID = JmsUtils.getJMSCorrelationID(message);
    assertThat(jmsCorrelationID, is(correlationId));
  }

  @Test
  public void getFromMessage_whenStringWrapped_expectString() throws Exception {
    ObjectMessage message = Mockito.mock(ObjectMessage.class);
    final String messageText = "This is message";
    when(message.getObject()).thenReturn(messageText);

    final String text = JmsUtils.getFromMessage(message, String.class);
    assertThat(text, is(messageText));
  }

  @Test
  public void getFromMessage_whenLongWrappedAndStringExpected_expectNull() throws Exception {
    ObjectMessage message = Mockito.mock(ObjectMessage.class);
    when(message.getObject()).thenReturn(100L);

    final String text = JmsUtils.getFromMessage(message, String.class);
    assertNull(text);
  }

  @Test
  public void getJMSCorrelationId_expectJMSCorrelationId() throws Exception {
    Message message = Mockito.mock(Message.class);
    final String id = "1000000";
    when(message.getJMSCorrelationID()).thenReturn(id);
    final String jmsCorrelationID = JmsUtils.getJMSCorrelationID(message);
    assertThat(jmsCorrelationID, is(id));
  }

  @Test
  public void getJMSMessageId_expectJMSMessageId() throws Exception {
    Message message = Mockito.mock(Message.class);
    final String id = "1000000";
    when(message.getJMSMessageID()).thenReturn(id);
    final String messageId = JmsUtils.getJMSMessageID(message);
    assertThat(messageId, is(id));
  }
}
