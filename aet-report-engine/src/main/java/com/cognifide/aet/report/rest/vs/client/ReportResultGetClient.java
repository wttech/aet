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
package com.cognifide.aet.report.rest.vs.client;

import org.apache.http.entity.ContentType;

import com.cognifide.aet.report.html.HtmlReportResultMetadata;
import com.cognifide.aet.report.xml.models.Testsuites;
import com.google.gson.Gson;

public class ReportResultGetClient {
	private final String endpointLocation;
	
	private final Gson gson = new Gson();

	public ReportResultGetClient(String endpointLocation) {
		super();
		this.endpointLocation = endpointLocation;
	}

	public HtmlReportResultMetadata getHtmlReportMetaData(String company, String project, String testSuite, String environment, String reporterModule, String correlationId) throws RestCallException{
		String resorucePath = getResourcePath(company, project, testSuite, environment, reporterModule, correlationId);
		
		return gson.fromJson(GetClient.create(endpointLocation, resorucePath).accept(ContentType.APPLICATION_JSON).get(), HtmlReportResultMetadata.class);
	}
	
	public Testsuites getXmlReportMetaData(String company, String project, String testSuite, String environment, String reporterModule, String correlationId) throws RestCallException{
		String resorucePath = getResourcePath(company, project, testSuite, environment, reporterModule, correlationId);
		
		return gson.fromJson(GetClient.create(endpointLocation, resorucePath).accept(ContentType.APPLICATION_JSON).get(), Testsuites.class);
	}
	
	
	private String getResourcePath(String company, String project, String testSuite, String environment, String reporterModule, String correlationId){
		StringBuilder pathBuilder = new StringBuilder();
		final String separator = "/";
		pathBuilder.append(separator).append(company);
		pathBuilder.append(separator).append(project);
		pathBuilder.append(separator).append(testSuite);
		pathBuilder.append(separator).append(environment);
		pathBuilder.append(separator).append("reports");
		pathBuilder.append(separator).append(reporterModule);
		pathBuilder.append(separator).append(correlationId);
		pathBuilder.append(separator).append("result.json");
		return pathBuilder.toString();
	}

	public static ReportResultGetClient create(String endpointLocation){
		return new ReportResultGetClient(endpointLocation);
	}
}
