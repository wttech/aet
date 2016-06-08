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

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.config.ReporterStep;
import com.cognifide.aet.communication.api.job.ReporterResultData;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.runner.distribution.progress.ProgressLog;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.jms.JMSException;
import java.util.Collections;
import java.util.Observable;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author maciej.laskowski
 *         Created: 2015-04-29
 */
public class ResultsDispatcherTest extends StepManagerTest {

	@Override
	protected StepManager createTested() throws JMSException {
		when(testSuiteRun.getReporterSteps()).thenReturn(Collections.singletonList(Mockito.mock(ReporterStep.class)));
		return new ResultsDispatcher(timeoutWatch, connection, testSuiteRun, 100l);
	}

	@Test
	public void onMessage_whenSuccess_expectFinishedObserversNotified() throws Exception {
		ReporterResultData reporterResultData = ReporterResultData.createSuccessResult(Mockito.mock(NodeMetadata
				.class));
		when(mockedMessage.getObject()).thenReturn(reporterResultData);

		ProgressLog progress = tested.getProgress();
		assertThat(progress.toString(), is("REPORTS: [success: 0, total: 1]"));

		tested.onMessage(mockedMessage);
		verify(observer, Mockito.times(1)).update(Matchers.<Observable>any(), any());

		progress = tested.getProgress();
		assertThat(progress.toString(), is("REPORTS: [success: 1, total: 1]"));

		assertTrue(((ResultsDispatcher) tested).isFinished());
	}

	@Test
	public void onMessage_whenError_expectFinishedObserversNotified() throws Exception {
		ReporterResultData reporterResultData = ReporterResultData.createErrorResult(ProcessingError.reportingError("testError"));
		when(mockedMessage.getObject()).thenReturn(reporterResultData);

		ProgressLog progress = tested.getProgress();
		assertThat(progress.toString(), is("REPORTS: [success: 0, total: 1]"));

		tested.onMessage(mockedMessage);
		verify(observer, Mockito.times(2)).update(Matchers.<Observable>any(), any());

		progress = tested.getProgress();
		assertThat(progress.toString(), is("REPORTS: [success: 0, failed: 1, total: 1]"));

		assertTrue(((ResultsDispatcher) tested).isFinished());
	}

	@Test
	public void isFinished_whenAborted_expectTrue() throws Exception {
		((ResultsDispatcher) tested).abort();
		assertTrue(((ResultsDispatcher) tested).isFinished());
	}

	@Override
	@Test
	public void closeConnections() throws Exception {
		tested.closeConnections();
		verify(session, times(1)).close();
		verify(consumer, times(1)).close();
	}

	@Test
	public void getProgress_whenNoProgress() throws Exception {
		ProgressLog progress = tested.getProgress();
		assertThat(progress.toString(), is("REPORTS: [success: 0, total: 1]"));
	}

	@Override
	@Test
	public void getQueueInName() throws Exception {
		assertThat(tested.getQueueInName(), is("AET.reporterResults"));
	}

	@Override
	@Test
	public void getQueueOutName() throws Exception {
		assertNull(tested.getQueueOutName());
	}

	@Override
	@Test
	public void getStepName() throws Exception {
		assertThat(tested.getStepName(), is("REPORTS"));
	}
}