/*
 * Cognifide AET :: Runner
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
package com.cognifide.aet.runner.distribution;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.inject.Provider;
import javax.jms.Destination;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.runner.testsuitescope.TestSuiteScope;
import com.google.inject.Key;

/**
 * TestSuiteTaskTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 12.03.15
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSuiteTaskTest {

	private TestSuiteTask tested;

	@Mock
	private Provider<TestLifeCycle> testLifeCycleProvider;

	@Mock
	private TestSuiteScope scope;

	@Mock
	private TestSuiteRun testSuiteRun;

	@Mock
	private Destination destination;

	@Before
	public void setUp() throws Exception {
		tested = new TestSuiteTask(testLifeCycleProvider, scope, RunnerMode.ONLINE, testSuiteRun, destination, false);
	}

	@Test
	public void run_expectTestSuiteRunAndDestinationSeededInScope() throws Exception {
		tested.run();
		verify(scope, times(1)).enter();
		verify(scope, times(1)).seed(Matchers.<Key<TestSuiteRun>> any(), eq(testSuiteRun));
		verify(scope, times(1)).seed(Matchers.<Key<Destination>> any(), eq(destination));
		verify(scope, times(1)).exit();
	}
}
