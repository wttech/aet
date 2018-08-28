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

import com.cognifide.aet.rest.helpers.configuration.ReportConfigurationManagerConf;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = ReportConfigurationManager.class)
@Designate(ocd = ReportConfigurationManagerConf.class)
public class ReportConfigurationManager {

  private static final String REPORT_DOMAIN_ENV = "REPORT_DOMAIN";

  private String reportDomain;

  @Activate
  public void activate(ReportConfigurationManagerConf config) {
    reportDomain = Optional.ofNullable(config.reportDomain())
        .filter(StringUtils::isNotBlank)
        .orElseGet(() -> Optional.ofNullable(System.getenv(REPORT_DOMAIN_ENV))
            .orElse(ReportConfigurationManagerConf.DEFAULT_REPORT_DOMAIN));
  }

  public String getReportDomain() {
    return reportDomain;
  }
}
