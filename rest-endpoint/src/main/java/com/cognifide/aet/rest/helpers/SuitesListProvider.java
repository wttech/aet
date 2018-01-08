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
package com.cognifide.aet.rest.helpers;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableListMultimap;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuitesListProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuitesListProvider.class);

  private static final Function<Suite, String> SUITES_INDEX_FUNCTION = new Function<Suite, String>() {
    @Override
    public String apply(Suite input) {
      return input.getName();
    }
  };

  private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS";

  private final MetadataDAO metadataDAO;

  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

  private final String reportDomain;

  public SuitesListProvider(MetadataDAO metadataDAO, String reportDomain) {
    this.metadataDAO = metadataDAO;
    this.reportDomain = reportDomain;
  }

  public String listSuites() {
    final StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><HTML><HEAD></HEAD><BODY>");
    try {
      final Collection<DBKey> projects = metadataDAO.getProjects(null);
      sb.append("<h1>Total ").append(projects.size())
          .append(" projects are present in database.</h1><hr>");
      for (DBKey dbKey : projects) {
        sb.append("Project: <b>").append(dbKey.getProject());
        sb.append("</b> in company: <b>").append(dbKey.getCompany()).append("</b>");

        final List<Suite> suites = metadataDAO.listSuites(dbKey);
        final ImmutableListMultimap<String, Suite> uniqueSuites =
            FluentIterable.from(suites).index(SUITES_INDEX_FUNCTION);
        sb.append(" has total <b>").append(uniqueSuites.keySet().size())
            .append("</b> unique suites.");

        for (Map.Entry<String, Collection<Suite>> suiteEntries : uniqueSuites.asMap().entrySet()) {
          sb.append("</br>Suite <i>").append(suiteEntries.getKey()).append("</i>");
          sb.append("<ul>");
          for (Suite suite : suiteEntries.getValue()) {
            sb.append("<li> ");
            sb.append("<a href=\"").append(reportDomain).append("/report.html")
                .append("?company=").append(suite.getCompany())
                .append("&project=").append(suite.getProject())
                .append("&correlationId=").append(suite.getCorrelationId())
                .append("\"> report for suite '")
                .append(suite.getName()).append("' in version ").append(suite.getVersion())
                .append("</a>");
            sb.append(" [correlationId: ").append(suite.getCorrelationId()).append("]");
            sb.append(" runned at ")
                .append(simpleDateFormat.format(new Date(suite.getRunTimestamp().get())));
            sb.append("</br>&nbsp;&nbsp;&nbsp; ")
                .append("  <a href=\"/api/metadata")
                .append("?company=").append(suite.getCompany())
                .append("&project=").append(suite.getProject())
                .append("&correlationId=").append(suite.getCorrelationId())
                .append("\">report metadata</a>");
            sb.append("</li>");
          }
          sb.append("</ul> ");
        }

        sb.append("<hr>");
      }
      sb.append("</BODY></HTML>");

    } catch (StorageException e) {
      LOGGER.debug("Exception while obtaining projects", e);
      return e.getMessage();
    }
    return sb.toString();
  }

}
