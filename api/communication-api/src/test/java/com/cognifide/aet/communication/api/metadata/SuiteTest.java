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
package com.cognifide.aet.communication.api.metadata;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.junit.runner.RunWith;


@RunWith(ZohhakRunner.class)
@Configure(separator = ";")
public class SuiteTest {

  @TestWith({"metadata/suite-with-two-sleeps.json"})
  public void validate_whenSuiteWithTwoSleepsAndNoFieldsIgnored_expectNoException(
      String suiteResource) throws Exception {
    final Suite suite = SuiteReaderHelper
        .readSuiteFromFile(suiteResource, getClass().getClassLoader());
    suite.validate(null);

    assertThat(suite.getCorrelationId(), is("unittest-project-0"));
  }

}
