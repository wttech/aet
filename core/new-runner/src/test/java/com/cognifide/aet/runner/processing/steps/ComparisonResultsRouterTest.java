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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.job.ComparatorResultData;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite.Timestamp;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.runner.processing.ProgressLog;
import com.google.common.base.Optional;
import java.util.Collections;
import java.util.Observable;
import javax.jms.JMSException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author maciej.laskowski Created: 2015-04-29
 */
@RunWith(MockitoJUnitRunner.class)
public class ComparisonResultsRouterTest extends StepManagerTest {


  @Override
  protected StepManager createTested() throws JMSException {
    ComparisonResultsRouter tested = new ComparisonResultsRouter(timeoutWatch, connection, runnerConfiguration,
        suiteIndexWrapper);
    return tested;
  }

  @Test
  public void informChangesCompleted_whenCollectingFinished_expectMetadataPersisted()
      throws Exception {
    Timestamp mockedTimestamp = Mockito.mock(Timestamp.class);
    when(suiteIndexWrapper.get()).thenReturn(suite);
    when(suite.getRunTimestamp()).thenReturn(mockedTimestamp);
    when(suite.getFinishedTimestamp()).thenReturn(mockedTimestamp);
    ((ComparisonResultsRouter) tested).informChangesCompleted();
  }

  @Test
  public void onMessage_whenSuccess_expectResultAddedToMetadataAndCountersUpdated()
      throws Exception {
    ComparatorResultData comparatorResultData = Mockito.mock(ComparatorResultData.class);
    when(mockedMessage.getObject()).thenReturn(comparatorResultData);
    when(comparatorResultData.getStatus()).thenReturn(JobStatus.SUCCESS);
    when(comparatorResultData.getStepIndex()).thenReturn(0);
    Url mockedUrl = Mockito.mock(Url.class);
    when(mockedUrl.getSteps()).thenReturn(Collections.singletonList(Mockito.mock(Step.class)));
    when(suiteIndexWrapper.getTestUrl(anyString(), anyString())).thenReturn(Optional.of(mockedUrl));
    ((ComparisonResultsRouter) tested).updateAmountToReceive(1);

    ProgressLog progress = tested.getProgress();
    assertThat(progress.toString(), is("COMPARED: [success:   0, total:   1]"));
    tested.onMessage(mockedMessage);
    progress = tested.getProgress();
    assertThat(progress.toString(), is("COMPARED: [success:   1, total:   1]"));
  }

  @Test
  public void onMessage_whenError_expectObserversNotifiedAndCountersUpdated() throws Exception {
    ComparatorResultData comparatorResultData = Mockito.mock(ComparatorResultData.class);
    when(mockedMessage.getObject()).thenReturn(comparatorResultData);
    when(comparatorResultData.getStatus()).thenReturn(JobStatus.ERROR);
    when(comparatorResultData.getStepIndex()).thenReturn(0);
    Url mockedUrl = Mockito.mock(Url.class);
    when(mockedUrl.getSteps()).thenReturn(Collections.singletonList(Mockito.mock(Step.class)));
    when(suiteIndexWrapper.getTestUrl(anyString(), anyString())).thenReturn(Optional.of(mockedUrl));
    ((ComparisonResultsRouter) tested).updateAmountToReceive(1);

    ProgressLog progress = tested.getProgress();
    assertThat(progress.toString(), is("COMPARED: [success:   0, total:   1]"));
    tested.onMessage(mockedMessage);
    verify(observer, times(1)).update(Matchers.<Observable>any(), any());
    progress = tested.getProgress();
    assertThat(progress.toString(), is("COMPARED: [success:   0, failed:   1, total:   1]"));
  }

  @Override
  @Test
  public void getQueueInName() throws Exception {
    assertThat(tested.getQueueInName(), is("AET.comparatorResults"));
  }

  @Override
  @Test
  public void getQueueOutName() throws Exception {
    assertNull(tested.getQueueOutName());
  }

  @Override
  @Test
  public void getStepName() throws Exception {
    assertThat(tested.getStepName(), is("COMPARED"));
  }

  @Override
  @Test
  public void closeConnections() throws Exception {
    tested.closeConnections();
    verify(session, VerificationModeFactory.times(1)).close();
    verify(consumer, VerificationModeFactory.times(1)).close();
    verify(sender, VerificationModeFactory.times(0)).close();
  }
}
