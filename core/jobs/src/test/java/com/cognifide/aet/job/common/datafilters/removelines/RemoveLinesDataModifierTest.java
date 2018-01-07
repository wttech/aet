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
package com.cognifide.aet.job.common.datafilters.removelines;

import static com.googlecode.catchexception.CatchException.verifyException;
import static org.junit.Assert.assertEquals;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
@Configure(separator = "-")
public class RemoveLinesDataModifierTest {

  private RemoveLinesDataModifier tested = new RemoveLinesDataModifier();

  @TestWith({"2,3-4,5", "1,1;10,12-10,12;15,30;13,14", "-10,12", "50,55-"})
  public void testParameters(String dataRanges, String patternRanges) throws ParametersException {
    tested.validateParameters(dataRanges, patternRanges);
  }

  @Test
  public void testParametersNull() throws ParametersException {
    verifyException(tested, ParametersException.class).validateParameters(null, null);
  }

  @Test
  public void testParametersBlank() throws ParametersException {
    verifyException(tested, ParametersException.class).validateParameters("", " ");
  }

  @TestWith({"2-4,5", "1,1;10;12-10,12;15,30;13,14", "10;12-", "-50;55"})
  public void testBadParameters(String dataRanges, String patternRanges)
      throws ParametersException {
    verifyException(tested, ParametersException.class)
        .validateParameters(dataRanges, patternRanges);
  }

  @TestWith({"2,1-4,5", "1,1;10,12-10,12;15,14;13,14"})
  public void testBadRanges(String dataRanges, String patternRanges) throws ParametersException,
      ProcessingException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("dataRanges", dataRanges);
    params.put("patternRanges", patternRanges);
    verifyException(tested, ParametersException.class).setParameters(params);
  }

  @TestWith({"abcd\nefgh\n1234-02,2-abcd\n1234", "abcd\nefgh\n1234-4,10-abcd\nefgh\n1234"})
  public void testModify(String data, String dataRanges, String result) throws ParametersException,
      ProcessingException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("dataRanges", dataRanges);
    tested.setParameters(params);
    assertEquals(result, tested.modifyData(data));
  }

}
