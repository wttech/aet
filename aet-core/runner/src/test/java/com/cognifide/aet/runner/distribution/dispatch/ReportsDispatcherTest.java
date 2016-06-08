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
import com.cognifide.aet.communication.api.job.ComparatorResultData;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.runner.distribution.progress.ProgressLog;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Collections;
import java.util.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author maciej.laskowski
 *         Created: 2015-04-29
 */
public class ReportsDispatcherTest extends StepManagerTest {

	@Override
	protected StepManager createTested() throws JMSException {
		return new ReportsDispatcher(timeoutWatch, connection, testSuiteRun, 100l);
	}

	@Test
	public void scheduleReportsRequestIfFinished_whenCollectingNotFinished_expectNoActions() throws Exception {
		((ReportsDispatcher) tested).scheduleReportsRequestIfFinished();
		verify(sender, times(0)).send(Matchers.<Message>any());
	}

	@Test
	public void informChangesCompleted_whenCollectingFinished_expectMessageSent() throws Exception {
		when(testSuiteRun.getReporterSteps()).thenReturn(Collections.singletonList(Mockito.mock(ReporterStep.class)));
		((ReportsDispatcher) tested).informChangesCompleted();
		verify(sender, times(1)).send(Matchers.<Message>any());
	}

	@Test
	public void onMessage_whenSuccess_expectResultAddedToMetadataAndCountersUpdated() throws Exception {
		ComparatorResultData comparatorResultData = ComparatorResultData.createSuccessResult(Mockito.mock(NodeMetadata
				.class));
		when(mockedMessage.getObject()).thenReturn(comparatorResultData);
		((ReportsDispatcher) tested).updateAmountToReceive(1);
		ProgressLog progress = tested.getProgress();
		assertThat(progress.toString(), is("COMPARED: [success: 0, total: 1]"));
		tested.onMessage(mockedMessage);
		progress = tested.getProgress();
		assertThat(progress.toString(), is("COMPARED: [success: 1, total: 1]"));
	}

	@Test
	public void onMessage_whenError_expectObserversNotifiedAndCountersUpdated() throws Exception {
		ComparatorResultData comparatorResultData = ComparatorResultData.createErrorResult(ProcessingError
				.reportingError("testError"));
		when(mockedMessage.getObject()).thenReturn(comparatorResultData);
		((ReportsDispatcher) tested).updateAmountToReceive(1);
		ProgressLog progress = tested.getProgress();
		assertThat(progress.toString(), is("COMPARED: [success: 0, total: 1]"));
		tested.onMessage(mockedMessage);
		verify(observer, times(1)).update(Matchers.<Observable>any(), any());
		progress = tested.getProgress();
		assertThat(progress.toString(), is("COMPARED: [success: 0, failed: 1, total: 1]"));
	}

	@Override
	@Test
	public void getQueueInName() throws Exception {
		assertThat(tested.getQueueInName(), is("AET.comparatorResults"));
	}

	@Override
	@Test
	public void getQueueOutName() throws Exception {
		assertThat(tested.getQueueOutName(), is("AET.reporterJobs"));
	}

	@Override
	@Test
	public void getStepName() throws Exception {
		assertThat(tested.getStepName(), is("COMPARED"));
	}
}