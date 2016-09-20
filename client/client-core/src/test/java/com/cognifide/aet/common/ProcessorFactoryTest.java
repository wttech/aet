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

import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage;
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.messages.ProgressMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ProcessorFactoryTest {

  private static final String REPORT_URL = "http://cognifide.com";

  private static final String BUILD_DIR = "/";

  @Mock
  private Message plainMessage;

  @Mock
  private ObjectMessage message;

  @Mock
  private RunnerTerminator runnerTerminator;

  @Mock
  private ProcessingErrorMessage processingErrorMessage;

  @Mock
  private FinishedSuiteProcessingMessage reportMessage;

  @Mock
  private ProgressMessage progressMessage;

  @Before
  public void setUp() {

  }

  @Test
  public void processTest_messageIsNull() throws JMSException {
    MessageProcessor processor = ProcessorFactory.produce(null, REPORT_URL, null, runnerTerminator);

    assertThat(processor, is(nullValue()));
  }

  @Test
  public void processTest_messageIdNotAnObject() throws JMSException {
    MessageProcessor processor = ProcessorFactory.produce(plainMessage, REPORT_URL, null, runnerTerminator);

    assertThat(processor, is(nullValue()));
  }

  @Test
  public void processTest_messageIsUnknownObject() throws JMSException {
    when(message.getObject()).thenReturn(Object.class);

    MessageProcessor processor = ProcessorFactory.produce(message, REPORT_URL, null, runnerTerminator);

    assertThat(processor, is(nullValue()));
  }

  @Test
  public void processTest_processingErrorMessage() throws JMSException {
    when(message.getObject()).thenReturn(processingErrorMessage);

    MessageProcessor processor = ProcessorFactory.produce(message, REPORT_URL, null, runnerTerminator);

    assertThat(processor, instanceOf(ProcessingErrorMessageProcessor.class));
  }

  @Test
  public void processTest_reportMessage() throws JMSException {
    when(message.getObject()).thenReturn(reportMessage);

    MessageProcessor processor = ProcessorFactory.produce(message, REPORT_URL, null, runnerTerminator);

    assertThat(processor, instanceOf(SuiteFinishedProcessor.class));
  }

  @Test
  public void processTest_progressMessage() throws JMSException {
    when(message.getObject()).thenReturn(progressMessage);

    MessageProcessor processor = ProcessorFactory.produce(message, REPORT_URL, null, runnerTerminator);

    assertThat(processor, instanceOf(ProgressMessageProcessor.class));
  }

}
