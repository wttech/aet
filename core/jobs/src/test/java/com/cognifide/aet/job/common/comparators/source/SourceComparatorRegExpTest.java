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
public class SourceComparatorRegExpTest {

  private SourceComparator tested;

  @Before
  public void setUp() {
    // initialized with nulls as tested method is stateless
    tested = new SourceComparator(null, null, null, null);
  }

  @DataPoints
  public static String[][] getSampleData() {
    return new String[][]{
        {"lineA", "lineA"},
        {"lineB\n", "lineB\n"},
        {"\nlineC\n", "lineC\n"},
        {"\n\n\nlineD\n", "lineD\n"},
        {"firstA\n\n\nlast", "firstA\nlast"},
        {"firstB\n\n\nsecond\n\n\n\n\n\nlast", "firstB\nsecond\nlast"},
        {"firstC\n\n\nlast\n\n", "firstC\nlast\n"},
        // CRLF
        {"Crlf lineA\r\n\r\nsecond line", "Crlf lineA\r\nsecond line"},
        {"Crlf lineB\r\n\r\nsecond line\r\n\r\n", "Crlf lineB\r\nsecond line\r\n"},
        // mixed
        {"Crlf lineC\n\n\nsecond line\r\n\r\n", "Crlf lineC\nsecond line\r\n"},
    };
  }

  @Theory
  public void removeEmptyLines(String[] dataPoint) throws Exception {
    String input = dataPoint[0];
    String expected = dataPoint[1];

    String actual = tested.removeEmptyLines(input);
    assertEquals("error while removing empty lines", expected, actual);
  }

}
