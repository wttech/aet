/*
 * Cognifide AET :: Report Engine
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
package com.cognifide.aet.report;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.report.api.ReportGenerateException;
import com.cognifide.aet.report.api.ReportGenerator;
import com.cognifide.aet.report.api.ReportResourceRegistry;
import com.cognifide.aet.report.html.HtmlReportGenerator;
import com.cognifide.aet.report.html.HtmlReportResultMetadata;
import com.cognifide.aet.report.rest.vs.client.ReportResultGetClient;
import com.cognifide.aet.report.rest.vs.client.RestCallException;
import com.cognifide.aet.report.rest.vs.client.VSClientFactory;
import com.cognifide.aet.report.xml.XmlReportGenerator;
import com.cognifide.aet.report.xml.models.Testsuites;

@Component
@Service(ReportGeneratorFactory.class)
public class ReportGeneratorFactory {
	
	@Reference
	private VSClientFactory vsClientFactory;
	
	@Reference
	private ReportResourceRegistry reportResourceRegistry;
	
	public ReportGenerator createGenerator(String mode, String template, String company, String project,
			String reporterModule, String testSuite, String environment, String correlationId)
			throws ReportGenerateException, RestCallException {
		
		ReportResultGetClient client = vsClientFactory.createReportClient();
		ReportGenerator generator = null;
		if (HtmlReportGenerator.NAME.equals(reporterModule)) {
			final HtmlReportResultMetadata htmlReportMetaData = client.getHtmlReportMetaData(company, project,
					testSuite, environment, reporterModule, correlationId);
			generator = new HtmlReportGenerator(reportResourceRegistry, mode, template, htmlReportMetaData);
		} else if (XmlReportGenerator.NAME.equals(reporterModule)) {
			final Testsuites xmlReportMetaData = client.getXmlReportMetaData(company, project, testSuite, environment,
					reporterModule, correlationId);
			generator = new XmlReportGenerator(xmlReportMetaData);
		}

		return generator;
	}
}
