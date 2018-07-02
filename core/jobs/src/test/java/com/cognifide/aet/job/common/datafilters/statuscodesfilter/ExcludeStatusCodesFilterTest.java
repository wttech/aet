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
package com.cognifide.aet.job.common.datafilters.statuscodesfilter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.statuscodes.StatusCodesCollectorResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExcludeStatusCodesFilterTest extends StatusCodesFilterTestBase {

  @Test
  public void modifyData_ExcludeByUrl_OneExcludedResultIsReturned()
      throws ProcessingException, ParametersException {
    when(params.get(PARAM_URL)).thenReturn(PARAM_URL_VALUE);
    tested.setParameters(params);
    StatusCodesCollectorResult result = tested.modifyData(data);
    assertThat(result.getStatusCodes(), hasSize(3));
    assertTrue(result.getStatusCodes().get(0).isExcluded());
    assertFalse(result.getStatusCodes().get(1).isExcluded());
    assertFalse(result.getStatusCodes().get(2).isExcluded());
  }

  @Test
  public void modifyData_ExcludeByPattern_TwoExcludedResultsAreReturned()
      throws ProcessingException,
      ParametersException {
    when(params.get(PARAM_PATTERN)).thenReturn(PARAM_PATTERN_VALUE);
    tested.setParameters(params);
    StatusCodesCollectorResult result = tested.modifyData(data);
    assertThat(result.getStatusCodes(), hasSize(3));
    assertTrue(result.getStatusCodes().get(0).isExcluded());
    assertFalse(result.getStatusCodes().get(1).isExcluded());
    assertTrue(result.getStatusCodes().get(2).isExcluded());
  }

  @Test
  public void modifyData_ExcludeByUrlAndPattern_AllReturnedResultsAreExcluded()
      throws ProcessingException,
      ParametersException {
    when(params.get(PARAM_URL)).thenReturn(PARAM_URL_ANOTHER_VALUE);
    when(params.get(PARAM_PATTERN)).thenReturn(PARAM_PATTERN_VALUE);
    tested.setParameters(params);
    StatusCodesCollectorResult result = tested.modifyData(data);
    assertThat(result.getStatusCodes(), hasSize(3));
    assertTrue(result.getStatusCodes().get(0).isExcluded());
    assertTrue(result.getStatusCodes().get(1).isExcluded());
    assertTrue(result.getStatusCodes().get(2).isExcluded());
  }

  @Test
  public void modifyData_ExcludeByNotFullUrl_OneExcludedResultIsReturned() throws Exception {
    when(params.get(PARAM_URL)).thenReturn(NOT_FULL_URL_PARAM);
    tested.setParameters(params);
    StatusCodesCollectorResult result = tested.modifyData(data);
    assertThat(result.getStatusCodes(), hasSize(3));
    assertTrue(result.getStatusCodes().get(0).isExcluded());
    assertFalse(result.getStatusCodes().get(1).isExcluded());
    assertFalse(result.getStatusCodes().get(2).isExcluded());
  }


  @Override
  protected StatusCodesFilter getStatusCodeFilterInstance() {
    return new ExcludeStatusCodesFilter();
  }
}
