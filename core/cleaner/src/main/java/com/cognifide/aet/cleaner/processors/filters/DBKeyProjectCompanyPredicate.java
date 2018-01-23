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

import com.cognifide.aet.vs.DBKey;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import org.apache.commons.lang3.StringUtils;

public class DBKeyProjectCompanyPredicate implements Predicate<DBKey> {

  private final String companyFilter;
  private final String projectFilter;

  public DBKeyProjectCompanyPredicate(String companyFilter, String projectFilter) {
    this.companyFilter = companyFilter;
    this.projectFilter = projectFilter;
  }

  @Override
  public boolean apply(DBKey dbKey) {
    return companyMatches(dbKey) && projectMatches(dbKey);
  }

  private boolean projectMatches(DBKey dbKey) {
    return StringUtils.isBlank(projectFilter) || dbKey.getProject().equals(projectFilter);
  }

  private boolean companyMatches(DBKey dbKey) {
    return StringUtils.isBlank(companyFilter) || dbKey.getCompany().equals(companyFilter);
  }


  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("companyFilter", companyFilter)
        .add("projectFilter", projectFilter)
        .toString();
  }
}
