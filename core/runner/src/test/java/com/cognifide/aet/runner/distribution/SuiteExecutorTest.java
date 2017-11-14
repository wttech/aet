/**
 * AET
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
package com.cognifide.aet.runner.distribution;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import com.cognifide.aet.runner.distribution.dispatch.CollectDispatcher;
import com.cognifide.aet.runner.distribution.dispatch.CollectionResultsRouter;
import com.cognifide.aet.runner.distribution.dispatch.ComparisonResultsRouter;
import com.cognifide.aet.runner.distribution.dispatch.MetadataPersister;
import com.cognifide.aet.runner.distribution.watch.TimeoutWatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Observable;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class SuiteExecutorTest {

  @Mock
  private SuiteIndexWrapper suiteIndexWrapper;

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
  private CollectionResultsRouter collectionResultsRouter;

  @Mock
  private ComparisonResultsRouter comparisonResultsRouter;

  @Mock
  private MetadataPersister metadataPersister;

  @Mock
  private SuiteExecutionSettings suiteExecutionSettings;

  @Mock
  private TimeoutWatch timeoutWatch;

  @Mock
  private SuiteAgent suiteAgent;

  @Mock
  private ProgressMessageObserver progressMessageObserver;

  @Mock
  private Suite suite;

  private SuiteExecutor tested;

  @Before
  public void setUp() throws Exception {
    tested = new SuiteExecutor(suiteExecutionSettings, timeoutWatch, suiteIndexWrapper,
            collectDispatcher, collectionResultsRouter, comparisonResultsRouter, suiteAgent,
            progressMessageObserver, metadataPersister, 100L);
    when(suiteIndexWrapper.get()).thenReturn(suite);
  }

  @Test
  public void constructor_expectAddedObservers() throws Exception {
    verify(collectionResultsRouter, times(1)).addChangeObserver(comparisonResultsRouter);
    verify(suiteAgent, times(1)).addProcessingErrorMessagesObservable(collectionResultsRouter);
    verify(suiteAgent, times(1)).addProcessingErrorMessagesObservable(comparisonResultsRouter);
    verify(comparisonResultsRouter, times(1)).setMetadataPersister(metadataPersister);
    verify(metadataPersister, times(1)).addObserver(suiteAgent);
  }

  @Test
  public void execute_whenTimedOut_ExpectForcedFinishSuite() throws Exception {
    when(timeoutWatch.isTimedOut(anyLong())).thenReturn(true);
    when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
    tested.execute();
    verify(metadataPersister, times(1)).persistMetadata();
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
    verify(comparisonResultsRouter, times(0)).abort();
    verify(collectDispatcher, times(0)).cancel(anyString());
  }

  @Test
  public void execute_whenMaintenanceMode_expectProgressObserverNotified() throws Exception {
    when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
    when(suiteExecutionSettings.isMaintenanceMessage()).thenReturn(true);
    when(comparisonResultsRouter.isFinished()).thenReturn(false);
    when(timeoutWatch.isTimedOut(anyLong())).thenReturn(true);
    tested.execute();
    verify(progressMessageObserver, times(1)).update(Matchers.<Observable>any(), any());
  }

  @Test
  public void abort_whenProcessed_expectDispatchersAborted() throws Exception {
    when(suiteExecutionSettings.shouldExecute()).thenReturn(true);
    when(suiteExecutionSettings.isMaintenanceMessage()).thenReturn(false);
    when(comparisonResultsRouter.isFinished()).thenReturn(false);
    when(timeoutWatch.isTimedOut(anyLong())).thenReturn(true);
    tested.execute();
    tested.abort();
    verify(comparisonResultsRouter, times(1)).abort();
    verify(collectDispatcher, times(2)).cancel(anyString());
  }
}
