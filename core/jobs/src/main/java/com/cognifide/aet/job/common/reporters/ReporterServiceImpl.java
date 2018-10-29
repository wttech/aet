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
package com.cognifide.aet.job.common.reporters;

import com.cognifide.aet.job.common.reporters.factory.ReportType;
import com.cognifide.aet.job.common.reporters.factory.ReporterFactory;
import com.cognifide.aet.vs.DBKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.osgi.service.component.annotations.Reference;

public class ReporterServiceImpl implements ReporterService {

  @Reference
  private ReporterFactory reporterFactory;

  @Override
  public List<ReportIssue> report(DBKey dbKey, String suiteCorrelationId,
      Set<ReportType> issuesToReport) {
    List<ReportIssue> reportIssues = new ArrayList<>();

    for (ReportType type : issuesToReport) {
      reportIssues.addAll(reporterFactory.get(type).report(dbKey, suiteCorrelationId));
    }

    return reportIssues;
  }
}
