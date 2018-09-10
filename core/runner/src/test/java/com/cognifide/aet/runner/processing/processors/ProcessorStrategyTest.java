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
package com.cognifide.aet.runner.processing.processors;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.MessagesSender;
import com.cognifide.aet.runner.processing.SuiteExecutionFactory;
import com.cognifide.aet.runner.processing.SuiteProcessor;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorStrategyTest {

  private ProcessorStrategyMock processorStrategy;

  @Mock
  protected Run objectToRunWrapper;

  @Mock
  protected Destination jmsReplyTo;

  @Mock
  protected SuiteDataService suiteDataService;

  @Mock
  protected RunnerConfiguration runnerConfiguration;

  @Mock
  protected SuiteExecutionFactory suiteExecutionFactory;

  @Mock
  protected Logger logger;

  @Mock
  protected Suite suite;

  @Mock
  protected MessagesSender messageSender;

  @Mock
  protected SuiteProcessor suiteProcessor;

  @Before
  public void setUp() throws Exception {
    processorStrategy = new ProcessorStrategyMock();
    processorStrategy
        .setParameters(objectToRunWrapper, jmsReplyTo, suiteDataService, runnerConfiguration,
            suiteExecutionFactory);
    processorStrategy.setLogger(logger);
    when(objectToRunWrapper.getObjectToRun()).thenReturn(suite);
    when(suite.toString()).thenReturn("Suite to string");
    when(suiteExecutionFactory.newMessagesSender(any(Destination.class))).thenReturn(messageSender);
    processorStrategy.setSuiteProcessor(suiteProcessor);
  }

  @Test
  public void call_whenAllIsCorrect_expectAllMethodsWereCalled() throws JMSException {
    processorStrategy.call();

    assertNotNull(processorStrategy.getObjectToRun()); //for check if prepareSuiteWrapper was called
    assertNotNull(processorStrategy.messagesSender); //for check if init was called
    verify(suiteProcessor, times(1)).startProcessing();
    verify(suiteProcessor, times(1)).cleanup();
  }

  @Test
  public void call_whenRaiseError_expectNullPointerException() throws JMSException {
    doThrow(JMSException.class).when(suiteProcessor).startProcessing();

    processorStrategy.call();

    assertNotNull(processorStrategy.getObjectToRun()); //for check if prepareSuiteWrapper was called
    assertNotNull(processorStrategy.messagesSender); //for check if init was called
    verify(suiteProcessor, times(1)).startProcessing();
    verify(suiteProcessor, times(1)).cleanup();
  }

}