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
package com.cognifide.aet.job.common.comparators.source;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class CodeFormatterTest {

  private CodeFormatter tested;

  @Before
  public void setUp() {
    // initialized with nulls as tested method is stateless
    tested = new CodeFormatter();
  }

  @DataPoints
  public static String[][] getSampleData() {
    return new String[][]{
        {"\n\n\n\n", ""},
        {"lineA", "lineA"},
        {"lineB\n", "lineB"},
        {"\nlineC\n", "lineC"},
        {"\n\n\nlineD\n", "lineD"},
        {"firstA\n\n\nlast", "firstA\nlast"},
        {"firstB\n\n\nsecond\n\n\n\n\n\nlast", "firstB\nsecond\nlast"},
        {"firstC\n\n\nlast\n\n", "firstC\nlast"},
        {"   \t   indented \t ", "   \t   indented \t "},
        {"first line\r\n\r\n  third line  ", "first line\n  third line  "},
        {" one \n two \n\n three \n\n\n    ", " one \n two \n three "},
        // CRLF
        {"Crlf lineA\r\n\r\nsecond line", "Crlf lineA\nsecond line"},
        {"Crlf lineB\r\n\r\nsecond line\r\n\r\n", "Crlf lineB\nsecond line"},
        // mixed
        {"Crlf lineC\n\n\nsecond line\r\n\r\n", "Crlf lineC\nsecond line"},
        // if we only have single blank line, we should also remove it
        {" ", ""},
        {" \t", ""},
        {" \t   ", ""},
        {"  \t\t  ", ""}
    };
  }

  @Theory
  public void removeEmptyLines(String[] dataPoint) {
    String input = dataPoint[0];
    String expected = dataPoint[1];

    String actual = tested.removeEmptyLines(input);
    assertEquals("error while removing empty lines", expected, actual);
  }

}
