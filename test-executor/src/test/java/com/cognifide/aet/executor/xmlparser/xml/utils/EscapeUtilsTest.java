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
package com.cognifide.aet.executor.xmlparser.xml.utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class EscapeUtilsTest {

  @Test
  public void escapeUrls_expectEscapedResults() throws IOException {
    String xmlString = readContentFromFile("/testSuite_escapedUrls.xml");
    String escapedXmlString = EscapeUtils.escapeUrls(xmlString);
    String resultString = readContentFromFile("/testSuite_escapedUrls_result.xml");
    assertThat(escapedXmlString, is(resultString));
  }

  private String readContentFromFile(String path) throws IOException {
    URL resourceURL = getClass().getResource(path);
    return IOUtils.toString(new FileInputStream(resourceURL.getPath()), "UTF-8");
  }

}
