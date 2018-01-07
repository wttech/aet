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
package com.cognifide.aet.job.common.comparators;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.common.ArtifactDAOMock;
import java.io.IOException;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public abstract class AbstractComparatorTest {

  protected ArtifactDAOMock artifactDaoMock;

  protected ComparatorProperties comparatorProperties;

  protected static final String TEST_COMPANY = "cognifide";

  protected static final String TEST_PROJECT = "aet";

  protected ComparatorStepResult result;

  protected String getExpectedString(String filename) throws IOException {
    return artifactDaoMock.getArtifactAsUTF8String(null, filename);
  }

  /**
   * @return first saved result
   */
  protected byte[] getActual() {
    return artifactDaoMock.getFirstSavedArtifactData();
  }

  protected void assertEqualsToSavedArtifact(String expectedMockFileName)
      throws IOException, JSONException {
    String expected = getExpectedString(expectedMockFileName);
    String actual = new String(getActual());
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }
}
