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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.runner.configuration.MessagesManagerConf;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessagesManagerTest {

  @Mock
  private MessagesManagerConf config;

  @Test(expected = IllegalArgumentException.class)
  public void createFullQueueName_whenNameIsNull_expectException() throws Exception {
    MessagesManager.createFullQueueName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createFullQueueName_whenNameIsEmpty_expectException() throws Exception {
    MessagesManager.createFullQueueName("");
  }

  @Test
  public void createFullQueueName_expectFullName() throws Exception {
    String fullQueueName = MessagesManager.createFullQueueName("test");
    assertThat(fullQueueName, is("AET.test"));
  }

  @Test
  public void remove_ExpectRemovingInvoked() throws Exception {
    MessagesManager messagesManager = new MessagesManager();
    messagesManager = Mockito.spy(messagesManager);

    MBeanServerConnection mockedConnection = Mockito.mock(MBeanServerConnection.class);
    JMXConnector mockedJmxConnector = Mockito.mock(JMXConnector.class);
    when(mockedJmxConnector.getMBeanServerConnection()).thenReturn(mockedConnection);

    doReturn(mockedJmxConnector).when(messagesManager).getJmxConnection(Mockito.anyString());

    ObjectName objectName = Mockito.mock(ObjectName.class);
    when(objectName.getKeyProperty(MessagesManager.DESTINATION_NAME_PROPERTY))
        .thenReturn("AET.queueName");

    when(mockedConnection.getAttribute(Matchers.<ObjectName>any(), Matchers.anyString()))
        .thenReturn(new ObjectName[]{objectName});

    when(mockedConnection.invoke(org.mockito.Matchers.<ObjectName>any(), anyString(),
        org.mockito.Matchers.<Object[]>any(), org.mockito.Matchers.<String[]>any()))
        .thenReturn(10);

    messagesManager.activate(config);
    messagesManager.remove("correlation-company-correlation-12345");

    verify(mockedConnection, times(1)).invoke(objectName, "removeMatchingMessages",
        new Object[]{"JMSCorrelationID='correlation-company-correlation-12345'"},
        new String[]{"java.lang.String"});
  }

  @Test
  public void remove_exceptionThrown_expectAETExceptionThrown() throws Exception {
    MessagesManager messagesManager = new MessagesManager();
    messagesManager = Mockito.spy(messagesManager);
    Mockito.doThrow(NullPointerException.class).when(messagesManager).getJmxConnection(null);
    Mockito.doThrow(AETException.class).when(messagesManager).remove(null);
  }
}
