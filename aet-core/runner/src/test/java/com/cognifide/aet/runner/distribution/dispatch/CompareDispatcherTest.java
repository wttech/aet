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
import com.cognifide.aet.communication.api.config.ComparatorStep;
import com.cognifide.aet.communication.api.config.TestRun;
import com.cognifide.aet.communication.api.job.CollectorResultData;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author maciej.laskowski
 *         Created: 2015-04-28
 */
@RunWith(MockitoJUnitRunner.class)
public class CompareDispatcherTest extends StepManagerTest {

	@Mock
	private ChangeObserver changeObserver;

	@Override
	protected StepManager createTested() throws JMSException {
		CompareDispatcher compareDispatcher = new CompareDispatcher(timeoutWatch, connection, testSuiteRun, 100l,
				scheduler);
		compareDispatcher.addChangeObserver(changeObserver);
		return compareDispatcher;
	}

	@Test
	public void onMessage_whenSuccess_expectChangeListenersNotifiedAndMessageSent() throws Exception {
		ObjectMessage message = Mockito.mock(ObjectMessage.class);
		CollectMetadata collectMetadata1 = mockCollectMetadata("collectorA");
		CollectMetadata collectMetadata2 = mockCollectMetadata("collectorB");
		List<CollectMetadata> collectorsList = Arrays.asList(collectMetadata1, collectMetadata2);
		CollectorResultData collectorResultData = CollectorResultData.createSuccessResult(collectorsList, "jmsID001",
				"testNameExample");
		when(message.getObject()).thenReturn(collectorResultData);
		when(message.getJMSCorrelationID()).thenReturn(CORRELATION_ID);
		when(message.getJMSMessageID()).thenReturn("jmsID0001");

		TestRun testRun = Mockito.mock(TestRun.class);
		when(testSuiteRun.getTestRunMap()).thenReturn(Collections.singletonMap("testNameExample", testRun));
		ComparatorStep comparatorStep = Mockito.mock(ComparatorStep.class);
		when(comparatorStep.getCollectorName()).thenReturn("collectorAName");
		when(testRun.getComparatorSteps()).thenReturn(Collections.singletonMap("collectorA", Collections.singletonList
				(comparatorStep)));

		tested.onMessage(message);

		//onSuccess
		verify(changeObserver, times(1)).updateAmountToReceive(1);
		verify(sender, times(1)).send(Matchers.<Message>any());
		verify(mockedMessage, times(1)).setJMSCorrelationID(CORRELATION_ID);

		//finishTask
		verify(changeObserver, times(1)).informChangesCompleted();
		verify(consumer, times(1)).close();
	}


	@Test
	public void onMessage_whenError_expectObserversNotified() throws Exception {
		ObjectMessage message = Mockito.mock(ObjectMessage.class);
		CollectorResultData collectorResultData = CollectorResultData.createErrorResult(ProcessingError
						.collectingError("error"), "jmsID001",
				"testNameExample");
		when(message.getObject()).thenReturn(collectorResultData);
		when(message.getJMSCorrelationID()).thenReturn(CORRELATION_ID);
		when(message.getJMSMessageID()).thenReturn("jmsID0001");

		TestRun testRun = Mockito.mock(TestRun.class);
		when(testSuiteRun.getTestRunMap()).thenReturn(Collections.singletonMap("testNameExample", testRun));
		ComparatorStep comparatorStep = Mockito.mock(ComparatorStep.class);
		when(comparatorStep.getCollectorName()).thenReturn("collectorAName");
		when(testRun.getComparatorSteps()).thenReturn(Collections.singletonMap("collectorA", Collections.singletonList
				(comparatorStep)));

		tested.onMessage(message);

		//onError
		verify(observer, times(1)).update(Matchers.<Observable>any(), any());

		//finishTask
		verify(changeObserver, times(1)).informChangesCompleted();
		verify(consumer, times(1)).close();
	}

	private CollectMetadata mockCollectMetadata(String collectorName) {
		CollectMetadata collectMetadata1 = Mockito.mock(CollectMetadata.class);
		when(collectMetadata1.getCollectorModule()).thenReturn(collectorName);
		when(collectMetadata1.getCollectorModuleName()).thenReturn(collectorName + "Name");
		when(collectMetadata1.getUrl()).thenReturn("http://exampleurl.com/" + collectorName);
		when(collectMetadata1.getUrlName()).thenReturn("exampleurl1_name_" + collectorName);
		return collectMetadata1;
	}

	@Override
	@Test
	public void getQueueInName() throws Exception {
		assertThat(tested.getQueueInName(), is("AET.collectorResults"));
	}

	@Override
	@Test
	public void getQueueOutName() throws Exception {
		assertThat(tested.getQueueOutName(), is("AET.comparatorJobs"));
	}

	@Override
	@Test
	public void getStepName() throws Exception {
		assertThat(tested.getStepName(), is("COLLECTED"));
	}
}