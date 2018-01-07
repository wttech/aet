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
package com.cognifide.aet.cleaner.processors.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

import com.cognifide.aet.cleaner.context.CleanerContext;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(ZohhakRunner.class)
@Configure(separator = ";")
public class SuiteRemoveConditionTest {

  private static final SuitesListCoercer SUITES_LIST_COERCER = new SuitesListCoercer();

  private static final long ONE_HOUR = 3600000L;

  @TestWith({
      //1 suite, remove all but last version in all cases
      "A-1 ; null ; 1 ; A-1 ; 10",
      "A-1 ; null ; null ; A-1 ; 10",
      "A-1 ; 100 ; 1 ; A-1; 10",
      "A-1 ; 100 ; null ; A-1 ; 10"
  })
  public void evaluate_whenOnlyOneSuite_expectFalseNoMatterConditions(String allSuitesVersions,
      Long removeOlderThan,
      Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo) throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertFalse(remove);
  }

  @TestWith({
      //8 suites, remove all but last version
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; A-1 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; A-2 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; B-3 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; B-4 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; C-5 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; C-6 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; D-7 ; 10",
      //8 suites, remove all but last 4 versions
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; A-1 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; A-2 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; B-3 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; B-4 ; 10",
      //8 suites, remove all but last 5 versions
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; A-1 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; A-2 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; B-3 ; 10"
  })
  public void evaluate_when8SuitesVersionsRemoveOldVersions_expectTrue(String allSuitesVersions,
      Long
          removeOlderThan, Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo)
      throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertTrue(remove);
  }

  @TestWith({
      //8 suites, keep last version
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 1 ; E-8 ; 10",
      //8 suites, keep last 4 versions
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; C-5 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; C-6 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; D-7 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 4 ; E-8 ; 10",
      //8 suites, keep last 5 versions
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; B-4 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; C-5 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; C-6 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; D-7 ; 10",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; null ; 5 ; E-8 ; 10"
  })
  public void evaluate_when8SuitesVersionsKeepLastVersions_expectFalse(String allSuitesVersions,
      Long
          removeOlderThan, Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo)
      throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertFalse(remove);
  }

  @TestWith({
      //8 suites, remove older than 4 days
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; A-1 ; 8",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; A-2 ; 7",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; B-3 ; 6",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; B-4 ; 5",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; C-5 ; 4",
      //8 suites, remove older than 1 day
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; A-1 ; 8",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; A-2 ; 7",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; B-3 ; 6",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; B-4 ; 5",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; C-5 ; 4",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; C-6 ; 3",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; D-7 ; 2"
  })
  public void evaluate_when8SuitesRemoveOlderThanButAlwaysKeepLatestVersion_expectTrue(
      String allSuitesVersions, Long
      removeOlderThan, Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo)
      throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertTrue(remove);
  }

  @TestWith({
      //8 suites, keep newer than 4 days
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; C-6 ; 3",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; D-7 ; 2",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; null ; E-8 ; 1",
      //8 suites, remove older than 1 day but keep at least one version
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 1 ; null ; E-8 ; 1"
  })
  public void evaluate_when8SuitesKeepNewerThan_expectFalse(String allSuitesVersions, Long
      removeOlderThan, Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo)
      throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertFalse(remove);
  }

  @TestWith({
      //8 suites, keep newer than 4 days but at least 4 last versions
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; A-1 ; 8",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; A-2 ; 7",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; B-3 ; 6",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; B-4 ; 5"
  })
  public void evaluate_when8SuitesKeepNewerThanAndAtLeastXVersions_expectTrue(
      String allSuitesVersions, Long
      removeOlderThan, Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo)
      throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertTrue(remove);
  }

  @TestWith({
      //8 suites, keep newer than 4 days but at least 4 last versions
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; C-5 ; 4",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; C-6 ; 3",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; D-7 ; 2",
      "A-1,A-2,B-3,B-4,C-5,C-6,D-7,E-8 ; 4 ; 4 ; E-8 ; 1"
  })
  public void evaluate_when8SuitesKeepNewerThanAndAtLeastXVersions_expectFalse(
      String allSuitesVersions,
      Long removeOlderThan, Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo)
      throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertFalse(remove);
  }

  @TestWith({
      //1 suite, remove all but last version in all cases
      "A-1,A-2,B-2 ; null ; 1 ; A-1 ; 10"
  })
  public void evaluate_whenDuplicatedVersion_expectRemoveOnlyOldVersions(String allSuitesVersions,
      Long removeOlderThan,
      Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo) throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertTrue(remove);
  }

  @TestWith({
      //1 suite, remove all but last version in all cases
      "A-1,A-2,B-2 ; null ; 1 ; A-2 ; 10",
      "A-1,A-2,B-2 ; null ; 1 ; B-2 ; 10"
  })
  public void evaluate_whenDuplicatedVersion_expectKeepNewestVersion(String allSuitesVersions,
      Long removeOlderThan,
      Long keepNVersions, String evaluatedSuite, Integer createdDaysAgo) throws Exception {
    final List<Suite> suites = SUITES_LIST_COERCER.toList(allSuitesVersions);

    SuiteRemoveCondition condition = new SuiteRemoveCondition(suites,
        mockRemoveConditions(removeOlderThan, keepNVersions));
    final boolean remove = condition
        .evaluate(mockSuiteVersionAndCreatedDate(evaluatedSuite, createdDaysAgo));
    assertFalse(remove);
  }

  private CleanerContext mockRemoveConditions(Long removeOlderThan, Long keepNVersions) {
    CleanerContext cleanerContext = Mockito.mock(CleanerContext.class);
    when(cleanerContext.getRemoveOlderThan()).thenReturn(removeOlderThan);
    when(cleanerContext.getKeepNVersions()).thenReturn(keepNVersions);
    return cleanerContext;
  }

  /**
   * @param input should consist of correlationId and Version separated with "-"
   */
  private static Suite mockSuiteVersion(String input) {
    final String[] suiteString = input.split("-");
    Suite suite = Mockito.mock(Suite.class);
    when(suite.getVersion()).thenReturn(Long.valueOf(suiteString[1]));
    when(suite.getCorrelationId()).thenReturn(suiteString[0]);
    return suite;
  }

  private Suite mockSuiteVersionAndCreatedDate(String input, int createdDaysAgo) {
    Suite suite = mockSuiteVersion(input);
    Suite.Timestamp timestamp = Mockito.mock(Suite.Timestamp.class);
    //substract one hour for test purposes accuracy
    when(timestamp.get())
        .thenReturn(new DateTime().minusDays(createdDaysAgo).getMillis() - ONE_HOUR);
    when(suite.getRunTimestamp()).thenReturn(timestamp);
    return suite;
  }

  private static class SuitesListCoercer {

    List<Suite> toList(String input) {
      final String[] split = input.split(",");
      return FluentIterable.from(Arrays.asList(split)).transform(new Function<String, Suite>() {
        @Override
        public Suite apply(String input) {
          return mockSuiteVersion(input);
        }
      }).toList();
    }
  }
}
