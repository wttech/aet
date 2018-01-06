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

import com.cognifide.aet.cleaner.context.CleanerContext;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.joda.time.DateTime;

public class SuiteRemoveCondition {

  private static final Comparator<Suite> COMPARE_SUITES_BY_VERSION_ASC = new Comparator<Suite>() {
    @Override
    public int compare(Suite o1, Suite o2) {
      return o1.getVersion().compareTo(o2.getVersion());
    }
  };

  private static final Comparator<Suite> COMPARE_SUITES_BY_VERSION_DESC = new Comparator<Suite>() {
    @Override
    public int compare(Suite o1, Suite o2) {
      return o2.getVersion().compareTo(o1.getVersion());
    }
  };

  private final Long removeTimestamp;

  private final Long minKeepVersion;

  public SuiteRemoveCondition(final Collection<Suite> suiteRunVersions,
      final CleanerContext cleanerContext) {
    minKeepVersion = getMinKeepVersion(suiteRunVersions, cleanerContext);
    removeTimestamp = getRemoveTimestamp(cleanerContext);
  }

  public boolean evaluate(final Suite suite) {
    boolean remove = false;

    if (minKeepVersion != null) {
      remove = suite.getVersion() < minKeepVersion;
    }
    if (removeTimestamp != null) {
      final long suiteCreated = suite.getRunTimestamp().get();
      remove &= suiteCreated <= removeTimestamp;
    }

    return remove;
  }

  private int getKeepNVersionsOrSecureToLastVersion(CleanerContext cleanerContext) {
    return cleanerContext.getKeepNVersions() != null ? cleanerContext.getKeepNVersions().intValue()
        : 1;
  }

  private Long getMinKeepVersion(final Collection<Suite> suiteRunVersions,
      CleanerContext cleanerContext) {
    Long minVersionToKeep;
    int keepNVersions = getKeepNVersionsOrSecureToLastVersion(cleanerContext);

    if (suiteRunVersions.size() > keepNVersions) {
      final List<Suite> suites = Ordering.from(COMPARE_SUITES_BY_VERSION_DESC)
          .sortedCopy(suiteRunVersions);
      minVersionToKeep = suites.get(keepNVersions - 1).getVersion();
    } else {
      final List<Suite> suites = Ordering.from(COMPARE_SUITES_BY_VERSION_ASC)
          .leastOf(suiteRunVersions, 1);
      minVersionToKeep = suites.iterator().next().getVersion();
    }
    return minVersionToKeep;
  }

  private Long getRemoveTimestamp(CleanerContext cleanerContext) {
    Long removeBeforeTimestamp = null;
    if (cleanerContext.getRemoveOlderThan() != null) {
      DateTime dateTime = new DateTime().minusDays(cleanerContext.getRemoveOlderThan().intValue());
      removeBeforeTimestamp = dateTime.getMillis();
    }
    return removeBeforeTimestamp;
  }

}
