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

import com.cognifide.aet.communication.api.config.ReporterStep;
import com.cognifide.aet.communication.api.config.TestSuiteRun;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RunnerTerminatorTest {

	private RunnerTerminator tested;

	@Mock
	private TestSuiteRun testSuiteRun;

	@Mock
	private List<ReporterStep> reporterSteps;

	@Test
	public void isActive() {
		when(testSuiteRun.getReporterSteps()).thenReturn(reporterSteps);
		when(reporterSteps.size()).thenReturn(3);
		tested = new RunnerTerminator(testSuiteRun);

		assertThat(tested.isActive(), is(true));
		tested.setChanged();
		assertThat(tested.isActive(), is(true));
		tested.setChanged();
		assertThat(tested.isActive(), is(true));
		tested.setChanged();
		assertThat(tested.isActive(), is(false));
		tested.setChanged();
		assertThat(tested.isActive(), is(false));
	}


}
