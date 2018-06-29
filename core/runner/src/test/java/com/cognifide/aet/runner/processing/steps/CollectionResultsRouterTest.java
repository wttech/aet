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
package com.cognifide.aet.runner.processing.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.job.CollectorResultData;
import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Url;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author maciej.laskowski Created: 2015-04-28
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionResultsRouterTest extends StepManagerTest {

  @Mock
  private ChangeObserver changeObserver;

  @Override
  protected StepManager createTested() throws JMSException {
    final com.cognifide.aet.communication.api.metadata.Test mockedTest = Mockito
        .mock(com.cognifide.aet.communication.api
            .metadata.Test.class);
    when(mockedTest.getUrls()).thenReturn(Collections.singleton(Mockito.mock(Url.class)));
    when(suite.getTests()).thenReturn(Collections.singletonList(mockedTest));
    when(suiteIndexWrapper.getTest(anyString())).thenReturn(mockedTest);
    CollectionResultsRouter collectionResultsRouter = new CollectionResultsRouter(timeoutWatch,
        connection, runnerConfiguration,
        scheduler, suiteIndexWrapper);
    collectionResultsRouter.addChangeObserver(changeObserver);
    return collectionResultsRouter;
  }

  @Test
  public void onMessage_whenSuccess_expectChangeListenersNotifiedAndMessageSent() throws Exception {
    ObjectMessage message = Mockito.mock(ObjectMessage.class);
    Step stepA = mockCollectionStep("collectorA", Collections.<Comparator>emptySet());
    Comparator mockedComparator = Mockito.mock(Comparator.class);
    Step stepB = mockCollectionStep("collectorB", Collections.singleton(mockedComparator));
    List<Step> steps = Arrays.asList(stepA, stepB);
    Url url = Mockito.mock(Url.class);
    when(url.getSteps()).thenReturn(steps);
    CollectorResultData collectorResultData = CollectorResultData
        .createSuccessResult(url, "jmsID001",
            "testNameExample");
    when(message.getObject()).thenReturn(collectorResultData);
    when(message.getJMSCorrelationID()).thenReturn(CORRELATION_ID);
    when(message.getJMSMessageID()).thenReturn("jmsID0001");

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
    Url url = Mockito.mock(Url.class);
    CollectorResultData collectorResultData = CollectorResultData
        .createErrorResult(url, ProcessingError
            .collectingError("error"), "jmsID001", "testNameExample");
    when(message.getObject()).thenReturn(collectorResultData);
    when(message.getJMSCorrelationID()).thenReturn(CORRELATION_ID);
    when(message.getJMSMessageID()).thenReturn("jmsID0001");

    tested.onMessage(message);

    //onError
    verify(observer, times(1)).update(Matchers.<Observable>any(), any());

    //finishTask
    verify(changeObserver, times(1)).informChangesCompleted();
    verify(consumer, times(1)).close();
  }

  private Step mockCollectionStep(String collectorName, Set<Comparator> comparators) {
    Step step = Mockito.mock(Step.class);
    when(step.getName()).thenReturn(collectorName);

    CollectorStepResult stepResult = Mockito.mock(CollectorStepResult.class);
    when(stepResult.getStatus()).thenReturn(CollectorStepResult.Status.COLLECTED);
    when(step.getStepResult()).thenReturn(stepResult);

    when(step.getComparators()).thenReturn(comparators);
    return step;
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
