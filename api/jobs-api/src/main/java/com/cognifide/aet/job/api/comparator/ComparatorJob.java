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
package com.cognifide.aet.job.api.comparator;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;

public interface ComparatorJob {

  /**
   * Method executed during compare phase.
   *
   * @return comparison result with details.
   */
  ComparatorStepResult compare() throws ProcessingException;

  /**
   * Setup all parameters necessary to perform comparison. Method is invoked before compare method.
   *
   * @param params - map that contains all parameters defined in test suite xml file.
   */
  void setParameters(Map<String, String> params) throws ParametersException;

}
