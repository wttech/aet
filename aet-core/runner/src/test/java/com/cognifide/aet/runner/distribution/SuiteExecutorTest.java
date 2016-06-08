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
package com.cognifide.aet.runner.distribution;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Collections;
import java.util.Observable;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import com.cognifide.aet.comments.api.manager.CommentsManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.distribution.dispatch.CollectDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.CompareDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.ReportsDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.ResultsDispatcher;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;
import com.cognifide.aet.vs.VersionStorage;

/**
 * SuiteExecutorTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 09.04.15
 */
@RunWith(MockitoJUnitRunner.class)
public class SuiteExecutorTest {

	@Mock
	private TestSuiteRun testSuiteRun;

	@Mock
	private SuiteExecutor suiteExecutor;

	@Mock
	private Destination destination;

	@Mock
	private JmsConnection jmsConnection;

	@Mock
	protected MessageProducer resultsProducer;

	@Mock
	protected ObjectMessage messageObject;

	@Mock
	private CollectDispatcher collectDispatcher;

	@Mock
	private CompareDispatcher compareDispatcher;

	@Mock
	private ReportsDispatcher reportsDispatcher;

	@Mock
	private ResultsDispatcher resultsDispatcher;

	@Mock
	private SuiteExecutionSettings suiteExecutionSettings;

	@Mock
	private VersionStorage versionStorage;

	@Mock
	private CommentsManager commentsManager;

	@Mock
	private TimeoutWatch timeoutWatch;

	@Mock
	private SuiteAgent suiteAgent;

	@Mock
	private ProgressMessageObserver progressMessageObserver;

	private SuiteExecutor tested;

	@Before
	public void setUp() throws Exception {
		when(versionStorage.getLastTestSuiteVersion(Matchers.<NodeMetadata> any())).thenReturn(5L);
		tested = new SuiteExecutor(suiteExecutionSettings, versionStorage, commentsManager, timeoutWatch, testSuiteRun,
				collectDispatcher, compareDispatcher, reportsDispatcher, resultsDispatcher, suiteAgent,
				progressMessageObserver, 100L);
	}

	@Test
	public void constructor_expectAddedObservers() throws Exception {
		verify(compareDispatcher, times(1)).addChangeObserver(reportsDispatcher);
		verify(suiteAgent, times(1)).addProcessingErrorMessagesObservable(compareDispatcher);
		verify(suiteAgent, times(1)).addProcessingErrorMessagesObservable(reportsDispatcher);
		verify(suiteAgent, times(1)).addProcessingErrorMessagesObservable(resultsDispatcher);
		verify(resultsDispatcher, times(1)).addObserver(suiteAgent);
	}

	@Test
	public void execute_whenShouldExecute_expectSuiteVersionIncremented() throws Exception {
		when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
		TestRun testRun = Mockito.mock(TestRun.class);
		ExtendedUrl mockedUrl = Mockito.mock(ExtendedUrl.class);
		when(mockedUrl.getName()).thenReturn("");
		when(testRun.getUrls()).thenReturn(Collections.singletonList(mockedUrl));
		when(testSuiteRun.getTestRunMap()).thenReturn(Collections.singletonMap("urlName", testRun));
		tested.execute();
		verify(testSuiteRun, times(1)).setVersion(6L);
	}

	@Test
	public void execute_validationFails_failMessageSent() throws Exception {
		when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
		TestRun testRun = Mockito.mock(TestRun.class);
		ExtendedUrl mockedUrl = Mockito.mock(ExtendedUrl.class);
		when(mockedUrl.getName()).thenReturn("");
		when(testRun.getUrls()).thenReturn(Collections.singletonList(mockedUrl));
		when(testSuiteRun.getTestRunMap()).thenReturn(Collections.singletonMap("urlName", testRun));
		tested.execute();
		verify(suiteAgent, times(1)).sendFailMessage(anyList());
	}

	@Test
	public void execute_whenTimedOut_ExpectForcedReportGeneration() throws Exception {
		TestRun testRun = Mockito.mock(TestRun.class);
		ExtendedUrl mockedUrl = Mockito.mock(ExtendedUrl.class);
		when(mockedUrl.getName()).thenReturn("");
		when(testRun.getUrls()).thenReturn(Collections.singletonList(mockedUrl));
		when(testSuiteRun.getTestRunMap()).thenReturn(Collections.<String, TestRun> emptyMap());
		when(resultsDispatcher.isFinished()).thenReturn(false);
		when(timeoutWatch.isTimedOut(anyLong())).thenReturn(true);
		when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
		tested.execute();
		verify(reportsDispatcher, times(1)).scheduleReportsRequest();
	}

	@Test
	public void execute_shouldNotExecute_failMessageSent() throws Exception {
		when(suiteExecutionSettings.shouldExecute()).thenReturn(false);
		tested.execute();
		verify(suiteAgent, times(1)).sendFailMessage(anyList());
	}

	@Test
	public void abort_whenNotProcessed_expectNoDispatchersAborted() throws Exception {
		tested.abort();
		verify(resultsDispatcher, times(0)).abort();
		verify(collectDispatcher, times(0)).cancel(anyString());
	}

	@Test
	public void execute_whenMaintenanceMode_expectProgressObserverNotified() throws Exception {
		when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
		when(suiteExecutionSettings.isMaintenanceMessage()).thenReturn(true);
		when(resultsDispatcher.isFinished()).thenReturn(false);
		when(timeoutWatch.isTimedOut(anyLong())).thenReturn(true);
		tested.execute();
		verify(progressMessageObserver, times(1)).update(Matchers.<Observable>any(), any());
	}

	@Test
	public void abort_whenProcessed_expectDispatchersAborted() throws Exception {
		when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
		when(suiteExecutionSettings.isMaintenanceMessage()).thenReturn(false);
		when(resultsDispatcher.isFinished()).thenReturn(false);
		when(timeoutWatch.isTimedOut(anyLong())).thenReturn(true);
		tested.execute();
		tested.abort();
		verify(resultsDispatcher, times(1)).abort();
		verify(collectDispatcher, times(2)).cancel(anyString());
	}
}
