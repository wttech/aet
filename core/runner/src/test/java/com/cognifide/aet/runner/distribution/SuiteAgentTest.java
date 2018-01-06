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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import java.util.Collections;
import java.util.Observable;
import org.apache.activemq.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SuiteAgentTest extends MessageObserverTest<SuiteAgent> {

  @Mock
  ProcessingErrorMessageObserver processingErrorMessageObserver;

  @Mock
  private SuiteIndexWrapper suiteIndexWrapper;

  @Mock
  private Suite suite;

  @Override
  protected Object getMessage() {
    return null;
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    tested = new SuiteAgent(jmsConnection, suiteIndexWrapper, processingErrorMessageObserver,
            destination, 100L);
    when(suiteIndexWrapper.get()).thenReturn(suite);
  }

  @Test
  public void sendFailedMessage_expectFailMessageSent() throws Exception {
    when(suiteIndexWrapper.get()).thenReturn(Mockito.mock(Suite.class));
    tested.sendFailMessage(Collections.singletonList("Error"));
    verify(resultsProducer, times(1)).send(Matchers.<Message>any());
  }

  @Test
  public void update_whenSuiteFinishForced_expectMessageSent() throws Exception {
    when(suiteIndexWrapper.get()).thenReturn(Mockito.mock(Suite.class));
    tested.enforceSuiteFinish();
    tested.update(null, null);
    verify(resultsProducer, times(1)).send(Matchers.<Message>any());
  }

  @Test
  public void addProcessingErrorMessagesObservable_expectErrorMessageObserverAdded() throws Exception {
    Observable mockedObservable = Mockito.mock(Observable.class);
    tested.addProcessingErrorMessagesObservable(mockedObservable);
    verify(mockedObservable, times(1)).addObserver(processingErrorMessageObserver);
  }
}
