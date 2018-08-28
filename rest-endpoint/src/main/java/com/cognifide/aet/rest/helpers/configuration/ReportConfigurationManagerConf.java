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
package com.cognifide.aet.rest.helpers.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET Report Application Configuration", description = "AET Report Application Configuration")
public @interface ReportConfigurationManagerConf {

  String REPORT_DOMAIN_PROPERTY_NAME = "report-domain";

  String DEFAULT_REPORT_DOMAIN = "http://aet-vagrant";

  @AttributeDefinition(
      name = REPORT_DOMAIN_PROPERTY_NAME,
      description = "Report application domain that is printed at the end of processing suite. "
          + "If not provided here, env variable REPORT_DOMAIN will be used instead or value will "
          + "fallback to the default: " + DEFAULT_REPORT_DOMAIN,
      type = AttributeType.STRING)
  String reportDomain() default "";

}
