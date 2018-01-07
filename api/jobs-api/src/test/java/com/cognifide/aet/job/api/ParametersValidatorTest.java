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
package com.cognifide.aet.job.api;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import org.junit.Test;

public class ParametersValidatorTest {

  @Test(expected = ParametersException.class)
  public void testCheckRange_lessThanMin() throws Exception {
    ParametersValidator.checkRange(-1, 0, 100, "Error");
  }

  @Test(expected = ParametersException.class)
  public void testCheckRange_greaterThanMax() throws Exception {
    ParametersValidator.checkRange(101, 0, 100, "Error");
  }

  @Test
  public void testCheckRange_valid() throws Exception {
    ParametersValidator.checkRange(0, 0, 100, "Error");
    ParametersValidator.checkRange(50, 0, 100, "Error");
    ParametersValidator.checkRange(100, 0, 100, "Error");
  }

  @Test
  public void testCheckNotBlank_valid() throws Exception {
    ParametersValidator.checkNotBlank("not blank", "Error");
  }

  @Test(expected = ParametersException.class)
  public void testCheckNotBlank_forNull() throws Exception {
    ParametersValidator.checkNotBlank(null, "Error");
  }

  @Test(expected = ParametersException.class)
  public void testCheckNotBlank_forEmpty() throws Exception {
    ParametersValidator.checkNotBlank(" ", "Error");
  }

  @Test
  public void testCheckParameter_valid() throws Exception {
    ParametersValidator.checkParameter(true, "Error");
  }

  @Test(expected = ParametersException.class)
  public void testCheckParameter_inValid() throws Exception {
    ParametersValidator.checkParameter(false, "Error");
  }

  @Test
  public void testCheckAtLeastOneNotBlank_whenAtLeastOneInputNotEmpty_validationPassed()
      throws Exception {
    ParametersValidator.checkAtLeastOneNotBlank("Error", "param-a");
    ParametersValidator.checkAtLeastOneNotBlank("Error", "", "param-b");
    ParametersValidator.checkAtLeastOneNotBlank("Error", "", "", "param-c");
    ParametersValidator.checkAtLeastOneNotBlank("Error", "", "param-b", "");
    ParametersValidator.checkAtLeastOneNotBlank("Error", "param-a", "", "");
    ParametersValidator.checkAtLeastOneNotBlank("Error", "param-a", "param-b", "param-c");
  }

  @Test(expected = ParametersException.class)
  public void testCheckAtLeastOneNotBlank_whenEmptyParameterArePassed_expectException()
      throws Exception {
    ParametersValidator.checkAtLeastOneNotBlank("Error", "", "");
  }


  @Test(expected = ParametersException.class)
  public void testCheckAtLeastOneNotBlank_whenNullParameterArePassed_expectException()
      throws Exception {
    ParametersValidator.checkAtLeastOneNotBlank("Error", null, "");
  }
}
