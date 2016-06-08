/*
 * Cognifide AET :: Worker
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.worker.impl.reports;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.client.utils.URIBuilder;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.cognifide.aet.communication.api.config.ReporterStep;

@Component(metatype=true, immediate=true, label = "AET Reporting Service URL Provider")
@Service(ReportingServiceURLProvider.class)
public class ReportingServiceURLProvider {
	private static final String SERVICE_URL_PROPERTY_NAME = "serviceUrl";
	private static final String SERVICE_URL_DEFAULT_VALUE = "http://localhost:8181/report/generate";
	
	@Property(name = SERVICE_URL_PROPERTY_NAME, label="Report service url", description = "Report generator service url", value = SERVICE_URL_DEFAULT_VALUE)
	private String serviceUrl;
	
	@Activate
	@Modified
	protected void activate(ComponentContext ctx){
		serviceUrl = PropertiesUtil.toString(ctx.getProperties().get(SERVICE_URL_PROPERTY_NAME), SERVICE_URL_DEFAULT_VALUE);
	}
	
	public String produceServiceUrl(String company, String project, String testSuite, String environment, String correlationId, String reporterModule, ReporterStep reporterStepData) throws URISyntaxException{
		URIBuilder builder = new URIBuilder(serviceUrl);
		Validate.notBlank(company, "Company is mandatory");
		builder.addParameter("company", company);
		Validate.notBlank(project);
		builder.addParameter("project", project);
		Validate.notBlank(reporterModule);
		builder.addParameter("reporterModule", reporterModule);
		Validate.notBlank(testSuite);
		builder.addParameter("testSuite", testSuite);
		Validate.notBlank(environment);
		builder.addParameter("environment", environment);
		Validate.notBlank(correlationId);
		builder.addParameter("correlationId", correlationId);
		
		String template = reporterStepData.getParameters().get("template");
		
		String mode = reporterStepData.getParameters().get("mode");
		
		if (StringUtils.isNotBlank(mode)) {
			builder.addParameter("mode", mode);
		}
		
		if (StringUtils.isNotBlank(template)) {
			builder.addParameter("template", template);
		}
		
		return builder.build().toString();
	}
}
