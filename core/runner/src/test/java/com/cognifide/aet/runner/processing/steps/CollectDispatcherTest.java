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

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.runner.scheduler.MessageWithDestination;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import javax.jms.JMSException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author maciej.laskowski Created: 2015-04-29
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectDispatcherTest extends StepManagerTest {

  @Override
  protected StepManager createTested() throws JMSException {
    return new CollectDispatcher(timeoutWatch, connection, runnerConfiguration, scheduler, suiteIndexWrapper);
  }

  @Override
  @org.junit.Test
  public void closeConnections() throws Exception {
    tested.closeConnections();
    verify(session, VerificationModeFactory.times(1)).close();
    verify(sender, VerificationModeFactory.times(1)).close();
  }

  @org.junit.Test
  public void cancel_expectCleanupOnCollectorJobSchedulerInvoked() throws Exception {
    ((CollectDispatcher) tested).cancel("correlationId00010101");
    verify(scheduler, times(1)).cancel("correlationId00010101");
  }

  @org.junit.Test
  public void process_when5Urls_expect3SchedulerJobAdded() throws Exception {
    Test testA = mockTest("first", 3);
    Test testB = mockTest("second", 2);
    when(suite.getTests()).thenReturn(ImmutableList.of(testA, testB));

    ((CollectDispatcher) tested).process();
    verify(session, times(3)).createObjectMessage(Matchers.<Serializable>any());
    verify(scheduler, times(1)).add(Matchers.<Queue<MessageWithDestination>>any(), anyString());
  }

  @org.junit.Test
  public void process_when2Urls_expect1SchedulerJobAdded() throws Exception {
    Test test = mockTest("testWith2Url", 2);
    when(suite.getTests()).thenReturn(ImmutableList.of(test));

    ((CollectDispatcher) tested).process();
    verify(session, times(1)).createObjectMessage(Matchers.<Serializable>any());
    verify(scheduler, times(1)).add(Matchers.<Queue<MessageWithDestination>>any(), anyString());
  }

  @Override
  @org.junit.Test
  public void onMessage_whenExceptionOccurs_expectObserversNotified() throws Exception {
    // onMessage action does nothing in CollectDispatcher
  }

  @Override
  @org.junit.Test
  public void getQueueInName() throws Exception {
    assertNull(tested.getQueueInName());
  }

  @Override
  @org.junit.Test
  public void getQueueOutName() throws Exception {
    assertThat(tested.getQueueOutName(), is("AET.collectorJobs"));
  }

  @Override
  @org.junit.Test
  public void getStepName() throws Exception {
    assertNull(tested.getStepName());
  }

  private Test mockTest(String name,
      int numberOfUrls) {
    final Test test = Mockito.mock(Test.class);
    Set<Url> urlsList = new HashSet<>(numberOfUrls);
    for (int i = 0; i < numberOfUrls; ++i) {
      urlsList.add(new Url("/url/" + name + i, name + i + "_name", null));
    }
    when(test.getUrls()).thenReturn(urlsList);
    when(test.getName()).thenReturn("testName_" + name);
    return test;
  }

}
