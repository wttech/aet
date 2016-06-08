/*
 * Cognifide AET :: Maven Plugin
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
package com.cognifide.aet.maven.plugins;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.ReportMessage;
import com.cognifide.aet.maven.plugins.ReportMessageProcessor;
import com.cognifide.aet.maven.plugins.ReportWriter;
import com.cognifide.aet.maven.plugins.RunnerTerminator;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportMessageProcessorTest {

	private static final String BUILD_DIRECTORY = "directory";

	private static final String FULL_REPORT_URL = "http://example.com";

	private ReportMessageProcessor tested;

	@Mock
	private ReportMessage reportMessage;

	@Mock
	private RunnerTerminator runnerTerminator;

	@Mock
	private ReportWriter reportWriter;

	@Before
	public void setUp() {
		when(reportMessage.getStatus()).thenReturn(ReportMessage.Status.OK);
		when(reportMessage.getFullReportUrl()).thenReturn(FULL_REPORT_URL);

		tested = new ReportMessageProcessor(reportMessage, BUILD_DIRECTORY, runnerTerminator, reportWriter);
	}

	@Test
	public void processTest() throws AETException, IOException {
		tested.process();

		verify(reportWriter, times(1)).write(BUILD_DIRECTORY, reportMessage.getFullReportUrl(), reportMessage.getSaveAsFileName());
		verify(runnerTerminator, times(1)).setChanged();
	}

	@Test(expected = AETException.class)
	public void processTest_error() throws AETException {
		when(reportMessage.getStatus()).thenReturn(ReportMessage.Status.FAILED);
		when(reportMessage.getErrors()).thenReturn(Lists.newArrayList("Error1", "Error2", "Error3"));

		tested.process();

		verify(reportMessage, times(1)).getErrors();
		verify(runnerTerminator, times(1)).setChanged();
	}
}
