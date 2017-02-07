/**
 * Automated Exploratory Tests
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
package com.cognifide.aet.common;

import com.cognifide.aet.communication.api.suiteexecution.ProcessingStatus;
import com.cognifide.aet.communication.api.suiteexecution.SuiteStatusResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.JMSException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;


@RunWith(MockitoJUnitRunner.class)
public class ProcessorFactoryTest {

  private static final String REPORT_URL = "http://cognifide.com";

  @Mock
  private RunnerTerminator runnerTerminator;

  @Before
  public void setUp() {

  }

  @Test
  public void processTest_statusIsNull() throws JMSException {
    StatusProcessor processor = ProcessorFactory.produce(null, REPORT_URL, null, runnerTerminator);

    assertThat(processor, is(nullValue()));
  }

  @Test
  public void processTest_statusIsUnknown() {
    StatusProcessor processor = ProcessorFactory.produce(new SuiteStatusResult(ProcessingStatus.UNKNOWN),
        REPORT_URL, null, runnerTerminator);

    assertThat(processor, is(nullValue()));
  }

  @Test
  public void processTest_processingErrorStatus() {
    StatusProcessor processor = ProcessorFactory.produce(new SuiteStatusResult(ProcessingStatus.ERROR),
        REPORT_URL, null, runnerTerminator);

    assertThat(processor, instanceOf(ProcessingErrorStatusProcessor.class));
  }

  @Test
  public void processTest_finishedStatus() {
    StatusProcessor processor = ProcessorFactory.produce(new SuiteStatusResult(ProcessingStatus.FINISHED),
        REPORT_URL, null, runnerTerminator);

    assertThat(processor, instanceOf(SuiteFinishedProcessor.class));
  }

  @Test
  public void processTest_progressStatus() {
    StatusProcessor processor = ProcessorFactory.produce(new SuiteStatusResult(ProcessingStatus.PROGRESS),
        REPORT_URL, null, runnerTerminator);

    assertThat(processor, instanceOf(ProgressStatusProcessor.class));
  }

}
