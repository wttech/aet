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

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.job.ReporterResultData;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ReportResult;
import com.cognifide.aet.vs.VersionStorage;
import com.cognifide.aet.vs.VersionStorageException;
import org.apache.activemq.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


/**
 * SuiteAgentTest
 *
 * @Author: Maciej Laskowski
 * @Date: 09.04.15
 */
@RunWith(MockitoJUnitRunner.class)
public class SuiteAgentTest extends MessageObserverTest<SuiteAgent> {

	@Mock
	ProcessingErrorMessageObserver processingErrorMessageObserver;
	@Mock
	private TestSuiteRun testSuiteRun;
	@Mock
	private VersionStorage versionStorage;

	@Override
	protected Object getMessage() {
		return ReporterResultData.createErrorResult(ProcessingError.collectingError("description"));
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		tested = new SuiteAgent(versionStorage, jmsConnection, testSuiteRun, processingErrorMessageObserver,
				destination, 100L);
	}

	@Test
	public void sendFailedMessage_expectFailMessageSent() throws Exception {
		TestRun testRun = Mockito.mock(TestRun.class);
		ExtendedUrl mockedUrl = Mockito.mock(ExtendedUrl.class);
		when(mockedUrl.getName()).thenReturn("");
		when(testRun.getUrls()).thenReturn(Collections.singletonList(mockedUrl));
		when(testSuiteRun.getTestRunMap()).thenReturn(Collections.singletonMap("urlName", testRun));

		tested.sendFailMessage(Collections.singletonList("Error"));

		verify(resultsProducer, times(1)).send(Matchers.<Message>any());
	}

	@Test
	public void update_whenReportGenerationForced_expectMessageSent() throws Exception {
		tested.setReportGenerationForced();
		tested.update(null, ReporterResultData.createErrorResult(ProcessingError.comparingError("test failure")));
		verify(resultsProducer, times(1)).send(Matchers.<Message>any());
	}

	@Test
	public void update_whenVersionStorageExceptionOccurs_expectErrorObserverNotified() throws Exception {
		NodeMetadata mockedReport = Mockito.mock(NodeMetadata.class);
		when(versionStorage.getReportNode(mockedReport)).thenThrow(VersionStorageException.class);
		tested.update(null, ReporterResultData.createSuccessResult(mockedReport));
		verify(processingErrorMessageObserver, times(1)).update(Matchers.<Observable>any(), Matchers.any());
	}

	@Test
	public void update_whenSuccessfulReportGeneration_expectMessageSent() throws Exception {
		NodeMetadata mockedReport = Mockito.mock(NodeMetadata.class);
		ReportResult result = Mockito.mock(ReportResult.class);
		Node resultNode = Mockito.mock(Node.class);
		when(versionStorage.getReportNode(mockedReport)).thenReturn(resultNode);
		when(resultNode.getResult(ReportResult.class)).thenReturn(result);
		when(result.getFullReportUrl()).thenReturn("http://report.url");
		tested.update(null, ReporterResultData.createSuccessResult(mockedReport));
		verify(processingErrorMessageObserver, times(0)).update(Matchers.<Observable>any(), Matchers.any());
		verify(resultsProducer, times(1)).send(Matchers.<Message>any());
	}

	@Test
	public void addProcessingErrorMessagesObservable_expectErrorMessageObserverAdded() throws Exception {
		Observable mockedObservable = Mockito.mock(Observable.class);
		tested.addProcessingErrorMessagesObservable(mockedObservable);
		verify(mockedObservable, times(1)).addObserver(processingErrorMessageObserver);
	}
}
