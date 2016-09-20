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
package com.cognifide.aet.runner.util;

import com.cognifide.aet.communication.api.exceptions.AETException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import static com.google.common.testing.GuavaAsserts.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * MessagesHelperTest
 *
 * @Author: Maciej Laskowski
 * @Date: 13.03.15
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JMXConnectorFactory.class)
public class MessagesHelperTest {

  @Test
  public void setupConnection_JMXConnectionCreated() throws Exception {
    PowerMockito.mockStatic(JMXConnectorFactory.class);
    JMXConnector jmxConnection = Mockito.mock(JMXConnector.class);
    BDDMockito.given(JMXConnectorFactory.connect(Mockito.<JMXServiceURL>any()))
            .willReturn(jmxConnection);

    MessagesHelper.setupConnection("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");

    PowerMockito.verifyStatic(times(1));
    JMXConnectorFactory.connect(Mockito.<JMXServiceURL>any());
  }

  @Test
  public void getAetQueuesObjects_expectFilteredAetDestinations() throws Exception {
    MBeanServerConnection connection = Mockito.mock(MBeanServerConnection.class);
    ObjectName aetQueue1 = mockedObjectName("AET.testQueue1");
    ObjectName aetQueue2 = mockedObjectName("AET.testQueue2");
    ObjectName[] queues = new ObjectName[]{mockedObjectName("PPP.queue1"), aetQueue1, aetQueue2};
    when(connection.getAttribute(Mockito.<ObjectName>any(), anyString())).thenReturn(queues);
    Set<ObjectName> aetQueuesObjects = MessagesHelper.getAetQueuesObjects(connection);
    assertThat(aetQueuesObjects, hasSize(2));
    assertTrue(aetQueuesObjects.contains(aetQueue1));
    assertTrue(aetQueuesObjects.contains(aetQueue2));
  }

  @Test(expected = AETException.class)
  public void getAetQueuesObjects_exceptionThrown_expectAETExceptionThrown() throws Exception {
    MBeanServerConnection connection = Mockito.mock(MBeanServerConnection.class);
    when(connection.getAttribute(Mockito.<ObjectName>any(), anyString())).thenThrow(
            NullPointerException.class);
    MessagesHelper.getAetQueuesObjects(connection);
  }

  private ObjectName mockedObjectName(String destinationName) {
    ObjectName mock = Mockito.mock(ObjectName.class);
    when(mock.getKeyProperty("destinationName")).thenReturn(destinationName);
    return mock;
  }
}
