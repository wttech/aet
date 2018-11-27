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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.MessagesSender;
import com.cognifide.aet.runner.processing.SuiteExecutionFactory;
import com.cognifide.aet.runner.processing.SuiteProcessor;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.StorageException;
import java.util.Optional;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class TestExecutionProcessorStrategyTest {

  private TestExecutionProcessorStrategy testExecutionProcessorStrategy;

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

  private Optional<com.cognifide.aet.communication.api.metadata.Test> test;

  @Before
  public void setUp() throws JMSException, StorageException {
    test = Optional
        .of(new com.cognifide.aet.communication.api.metadata.Test("testName", "proxy", "chrome"));
    testExecutionProcessorStrategy = new TestExecutionProcessorStrategy();
    testExecutionProcessorStrategy
        .setParameters(objectToRunWrapper, jmsReplyTo, suiteDataService, runnerConfiguration,
            suiteExecutionFactory);
    testExecutionProcessorStrategy.setLogger(logger);
    when(objectToRunWrapper.getObjectToRun()).thenReturn(test.get());
    when(objectToRunWrapper.getRealSuite()).thenReturn(suite);
    when(suite.toString()).thenReturn("Suite to string");
    when(suiteExecutionFactory.newMessagesSender(any(Destination.class))).thenReturn(messageSender);
    testExecutionProcessorStrategy.setSuiteProcessor(suiteProcessor);
    when(suite.getTest(any(String.class))).thenReturn(test);
    when(suiteDataService.enrichWithPatterns(any(Suite.class))).thenReturn(suite);
  }

  @Test
  public void prepareSuiteWrapper() throws StorageException {
    testExecutionProcessorStrategy.prepareSuiteWrapper();
    verify(suiteDataService, times(1)).enrichWithPatterns(any(Suite.class));
    verify(objectToRunWrapper, times(1)).setObjectToRun(any(Object.class));
    verify(objectToRunWrapper, times(1)).setRealSuite(any(Suite.class));
  }

  @Test
  public void save_checkIfMethodCallsCorrectFunctions()
      throws JMSException, ValidatorException, StorageException {
    testExecutionProcessorStrategy.init();
    testExecutionProcessorStrategy.save();
    verify(suiteDataService, times(1)).getSuite(any(DBKey.class), any(String.class));
    verify(suiteDataService, times(1)).replaceSuite(any(Suite.class), any(Suite.class));
  }

  @Test
  public void getObjectToRun_checkIfMethodReturnCorrectObject_expectTest() {
    assertEquals(test.get(), testExecutionProcessorStrategy.getObjectToRun());
  }
}