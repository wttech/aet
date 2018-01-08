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
package com.cognifide.aet.executor.xmlparser.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.cognifide.aet.executor.model.TestSuiteRun;
import com.cognifide.aet.executor.xmlparser.api.TestSuiteParser;
import java.io.File;
import java.net.URL;
import org.junit.Test;

public class XmlTestSuiteParserTest {

  @Test
  public void testParse() throws Exception {
    URL resourceURL = getClass().getResource("/testSuite.xml");
    File testSuitFile = new File(resourceURL.toURI());

    TestSuiteParser testSuiteParser = new XmlTestSuiteParser();

    TestSuiteRun testSuite = testSuiteParser.parse(testSuitFile);
    assertThat(testSuite.getName(), is("ts1"));
  }

}
