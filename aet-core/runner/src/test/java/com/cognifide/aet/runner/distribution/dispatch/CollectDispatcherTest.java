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
package com.cognifide.aet.runner.distribution.dispatch;

import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.cognifide.aet.communication.api.config.TestRun;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import javax.jms.JMSException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static junit.framework.TestCase.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author maciej.laskowski
 *         Created: 2015-04-29
 */
public class CollectDispatcherTest extends StepManagerTest {

	@Override
	protected StepManager createTested() throws JMSException {
		return new CollectDispatcher(timeoutWatch, connection, 1000l, 2, testSuiteRun,
				scheduler);
	}

	@Override
	@Test
	public void closeConnections() throws Exception {
		tested.closeConnections();
		verify(session, VerificationModeFactory.times(1)).close();
		verify(sender, VerificationModeFactory.times(1)).close();
	}

	@Test
	public void cancel_expectCleanupOnCollectorJobSchedulerInvoked() throws Exception {
		((CollectDispatcher) tested).cancel("correlationId00010101");
		verify(scheduler, times(1)).cleanup("correlationId00010101");
	}

	@Test
	public void process_when5Urls_expect3SchedulerJobAdded() throws Exception {

		TestRun testRunA = mockTestRun("first", 3);
		TestRun testRunB = mockTestRun("second", 2);
		Map<String, TestRun> testRunMap = new HashMap<>();
		testRunMap.put("first", testRunA);
		testRunMap.put("second", testRunB);
		when(testSuiteRun.getTestRunMap()).thenReturn(testRunMap);
		when(testSuiteRun.getDomain()).thenReturn(null);
		when(testSuiteRun.getCompany()).thenReturn("testCompany");
		when(testSuiteRun.getProject()).thenReturn("testProject");
		when(testSuiteRun.getName()).thenReturn("suiteName");
		when(testSuiteRun.getEnvironment()).thenReturn("FF16win7");
		when(testSuiteRun.getCorrelationId()).thenReturn("correlationId00010101");
		when(testSuiteRun.getVersion()).thenReturn(1l);

		((CollectDispatcher) tested).process(testSuiteRun);
		verify(session, times(3)).createObjectMessage(Matchers.<Serializable>any());
		verify(scheduler, times(1)).add(Matchers.<Queue<MessageWithDestination>>any(), anyString());
	}

	@Test
	public void process_when2Urls_expect1SchedulerJobAdded() throws Exception {
		Map<String, TestRun> testRunMap = Collections.singletonMap("oneTest", mockTestRun("testRun", 2));
		when(testSuiteRun.getTestRunMap()).thenReturn(testRunMap);
		when(testSuiteRun.getDomain()).thenReturn("http://domain/");
		when(testSuiteRun.getCompany()).thenReturn("testCompany");
		when(testSuiteRun.getProject()).thenReturn("testProject");
		when(testSuiteRun.getName()).thenReturn("suiteName");
		when(testSuiteRun.getEnvironment()).thenReturn("FF16win7");
		when(testSuiteRun.getCorrelationId()).thenReturn("correlationId00010101");
		when(testSuiteRun.getVersion()).thenReturn(1l);

		((CollectDispatcher) tested).process(testSuiteRun);
		verify(session, times(1)).createObjectMessage(Matchers.<Serializable>any());
		verify(scheduler, times(1)).add(Matchers.<Queue<MessageWithDestination>>any(), anyString());
	}

	@Override
	@Test
	public void onMessage_whenExceptionOccurs_expectObserversNotified() throws Exception {
		// onMessage action does nothing in CollectDispatcher

	}

	@Override
	@Test
	public void getQueueInName() throws Exception {
		assertNull(tested.getQueueInName());
	}

	@Override
	@Test
	public void getQueueOutName() throws Exception {
		assertNull(tested.getQueueOutName());
	}

	@Override
	@Test
	public void getStepName() throws Exception {
		assertNull(tested.getStepName());
	}

	private TestRun mockTestRun(String name, int numberOfUrls) {
		TestRun testRun = Mockito.mock(TestRun.class);
		List<ExtendedUrl> urlsList = new LinkedList<>();
		for (int i = 0; i < numberOfUrls; ++i) {
			urlsList.add(new ExtendedUrl("/url/" + name + i, name + i + "_name", null));
		}
		when(testRun.getUrls()).thenReturn(urlsList);
		when(testRun.getName()).thenReturn("testName_" + name);
		return testRun;
	}

}