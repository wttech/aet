/*
 * Cognifide AET :: Runner
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.runner.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cognifide.aet.communication.api.exceptions.AETException;

/**
 * QueuesUtilTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 01.12.14
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MessagesHelper.class)
public class MessagesManagerTest {

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
	public void activate_whenUrlNotSet_expectDefaultJmxUrlSetup() throws Exception {
		MessagesManager messagesManager = new MessagesManager();
		messagesManager.activate(Collections.emptyMap());
		assertThat(messagesManager.getJmxUrl(), is("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"));
	}

	@Test
	public void activate_whenUrlSet_expectProvidedJmxUrlSetup() throws Exception {
		MessagesManager messagesManager = new MessagesManager();
		messagesManager.activate(Collections.singletonMap("jxm-url", "localhost:111999"));
		assertThat(messagesManager.getJmxUrl(), is("localhost:111999"));
	}

	@Test
	public void remove_ExpectRemovingInvoked() throws Exception {
		PowerMockito.mockStatic(MessagesHelper.class);
		MBeanServerConnection mockedConnection = Mockito.mock(MBeanServerConnection.class);
		BDDMockito.given(
				MessagesHelper.setupConnection("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"))
				.willReturn(mockedConnection);

		ObjectName objectName = Mockito.mock(ObjectName.class);
		when(objectName.getKeyProperty(MessagesHelper.DESTINATION_NAME_PROPERTY)).thenReturn("queueName");
		BDDMockito.given(MessagesHelper.getAetQueuesObjects(mockedConnection)).willReturn(
				Collections.singleton(objectName));

		when(
				mockedConnection.invoke(org.mockito.Matchers.<ObjectName> any(), anyString(),
						org.mockito.Matchers.<Object[]> any(), org.mockito.Matchers.<String[]> any()))
				.thenReturn(10);

		MessagesManager messagesManager = new MessagesManager();
		messagesManager.activate(Collections.emptyMap());
		messagesManager.remove("correlation-company-correlation-12345");

		verify(mockedConnection, times(1)).invoke(objectName, "removeMatchingMessages",
				new Object[] { "JMSCorrelationID='correlation-company-correlation-12345'" },
				new String[] { "java.lang.String" });
	}

	@Test(expected = AETException.class)
	public void remove_exceptionThrown_expectAETExceptionThrown() throws Exception {
		PowerMockito.mockStatic(MessagesHelper.class);
		BDDMockito.given(MessagesHelper.setupConnection(null)).willThrow(NullPointerException.class);

		MessagesManager messagesManager = new MessagesManager();
		messagesManager.remove(null);
	}
}
