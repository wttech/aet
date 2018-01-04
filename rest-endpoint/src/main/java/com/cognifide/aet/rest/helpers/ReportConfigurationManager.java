/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.rest.helpers;

import java.util.Map;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;

@Service(ReportConfigurationManager.class)
@Component(metatype = true, description = "AET Report Application Configuration",
    label = "AET Report Application Configuration")
public class ReportConfigurationManager {

  private static final String REPORT_DOMAIN_PROPERTY_NAME = "report-domain";

  private static final String DEFAULT_REPORT_DOMAIN = "http://aet-vagrant";

  @Property(name = REPORT_DOMAIN_PROPERTY_NAME, label = "Report application domain",
      description = "Report application domain", value = DEFAULT_REPORT_DOMAIN)
  private String reportDomain;

  @Activate
  public void activate(Map<String, String> properties) {
    reportDomain = PropertiesUtil.toString(properties.get(REPORT_DOMAIN_PROPERTY_NAME), DEFAULT_REPORT_DOMAIN);
  }

  public String getReportDomain() {
    return reportDomain;
  }
}
