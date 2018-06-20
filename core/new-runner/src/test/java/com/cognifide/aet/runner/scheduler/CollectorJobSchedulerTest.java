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
package com.cognifide.aet.runner.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.MessagesManager;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CollectorJobSchedulerTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private MessagesManager messagesManager;

  @Mock
  private MessageProducer producer;

  @Mock
  private Session session;

  private CollectorJobScheduler tested;

  private ExecutorService executorService;

  @Before
  public void setUp() throws Exception {
    JmsConnection mockedConnection = Mockito.mock(JmsConnection.class);
    when(mockedConnection.getJmsSession()).thenReturn(session);
    when(session.createProducer(null)).thenReturn(producer);
    tested = new CollectorJobScheduler(mockedConnection, 3, messagesManager);

    executorService = Executors.newSingleThreadExecutor();
    executorService.submit(tested);
  }

  @After
  public void tearDown() throws Exception {
    tested.quit();
    executorService.shutdownNow();
  }

  @Test(timeout = 2000l)
  public void add_expectMessageSent() throws Exception {
    Message mockedMessage = Mockito.mock(Message.class);
    Destination destination = Mockito.mock(Destination.class);
    String correlationID = "01234567890";

    Queue<MessageWithDestination> messagesQueue = mockMessagesQueue(mockedMessage, destination,
        correlationID);
    tested.add(messagesQueue, correlationID);
    //wait until message will be added to messages counter and sent
    Thread.sleep(500);
    verify(producer, times(1)).send(Matchers.<Destination>any(), Matchers.<Message>any());
  }


  @Test
  public void add_whenMessageQueueWithSameId_expectIllegalStateException() throws Exception {
    String correlationID = "98765432100";

    Queue<MessageWithDestination> messagesQueue1 = mockMessagesQueue(Mockito.mock(Message.class),
        Mockito.mock
            (Destination.class), correlationID);
    Queue<MessageWithDestination> messagesQueue2 = mockMessagesQueue(Mockito.mock(Message.class),
        Mockito.mock
            (Destination.class), correlationID);

    // quits scheduler in order to stop processing in safeRun method
    // to actually observe the exception on linux
    // as otherwise the first message is consumed before the second is added
    tested.quit();
    tested.add(messagesQueue1, correlationID);
    thrown.expect(IllegalStateException.class);
    tested.add(messagesQueue2, correlationID);
  }

  @Test
  public void cleanup_expectMessageRemovedFromMessagesManager() throws Exception {
    Message mockedMessage = Mockito.mock(Message.class);
    Destination destination = Mockito.mock(Destination.class);
    String correlationID = "9996666330";

    Queue<MessageWithDestination> messagesQueue = mockMessagesQueue(mockedMessage, destination,
        correlationID);
    tested.add(messagesQueue, correlationID);
    //wait until message will be added to messages counter and sent
    Thread.sleep(500);
    tested.cleanup(correlationID);
    verify(messagesManager, times(1)).remove(correlationID);
  }

  @Test
  public void messageReceived_expectDecreasedReceivedMessagesCounter() throws Exception {
    Message mockedMessage = Mockito.mock(Message.class);
    Destination destination = Mockito.mock(Destination.class);
    String correlationID = "111111111";

    MessageWithDestination messageWithDestination = Mockito.mock(MessageWithDestination.class);
    ReceivedMessagesInfo messagesToReceived = Mockito.mock(ReceivedMessagesInfo.class);
    when(messagesToReceived.getCorrelationID()).thenReturn(correlationID);
    when(messagesToReceived.getMessagesAmount()).thenReturn(2).thenReturn(1).thenReturn(0);
    when(mockedMessage.getJMSCorrelationID()).thenReturn(correlationID);
    when(mockedMessage.getJMSMessageID()).thenReturn("000000000");
    when(messageWithDestination.getMessage()).thenReturn(mockedMessage);
    when(messageWithDestination.getDestination()).thenReturn(destination);
    when(messageWithDestination.getMessagesToReceived()).thenReturn(messagesToReceived);
    Queue<MessageWithDestination> messagesQueue = new ArrayDeque<>();
    messagesQueue.add(messageWithDestination);

    tested.add(messagesQueue, correlationID);
    //wait until message will be added to messages counter and sent
    Thread.sleep(500);
    tested.messageReceived("000000000", correlationID);
    verify(messagesToReceived, times(1)).getMessagesAmount();
    tested.messageReceived("000000000", correlationID);
    verify(messagesToReceived, times(1)).getMessagesAmount();
    tested.messageReceived("000000000", correlationID);
  }

  private Queue<MessageWithDestination> mockMessagesQueue(Message mockedMessage,
      Destination destination, String
      correlationID) throws JMSException {
    MessageWithDestination messageWithDestination = Mockito.mock(MessageWithDestination.class);
    ReceivedMessagesInfo messagesToReceived = new ReceivedMessagesInfo(2, correlationID);
    when(mockedMessage.getJMSCorrelationID()).thenReturn(correlationID);
    when(mockedMessage.getJMSMessageID()).thenReturn("000000000");
    when(messageWithDestination.getMessage()).thenReturn(mockedMessage);
    when(messageWithDestination.getDestination()).thenReturn(destination);
    when(messageWithDestination.getMessagesToReceived()).thenReturn(messagesToReceived);
    Queue<MessageWithDestination> messagesQueue = new ArrayDeque<>();
    messagesQueue.add(messageWithDestination);
    return messagesQueue;
  }
}
