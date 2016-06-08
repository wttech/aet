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
package com.cognifide.aet.report.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.cognifide.aet.report.ReportGeneratorFactory;
import com.cognifide.aet.report.api.ReportGenerateException;
import com.cognifide.aet.report.api.ReportGenerator;
import com.cognifide.aet.report.rest.vs.client.RestCallException;

@Component(metatype=false, label = "AET Report Generating Servlet")
public class ReportGeneratingServlet extends HttpServlet {
	private static final long serialVersionUID = -5237406304947779708L;
	
	public static final String PATH = "/report/generate";

	@Reference
	private HttpService httpService;
	
	@Reference
	private ReportGeneratorFactory reportGeneratorFactory;
	
	@Activate
	protected void activate(ComponentContext ctx) throws ServletException, NamespaceException {
		httpService.registerServlet(PATH, this, null, null);
	}
	
	@Deactivate
	protected void deactivate(ComponentContext ctx) {
		httpService.unregister(PATH);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String mode = request.getParameter("mode");
		String template = request.getParameter("template");
		
		String company = request.getParameter("company");
		Validate.notBlank(company, "Company not specified");
		
		String project = request.getParameter("project");
		Validate.notBlank(project, "Project not specified");
		
		String reporterModule = request.getParameter("reporterModule");
		Validate.notBlank(reporterModule, "Reporter module not specified");
		
		String testSuite = request.getParameter("testSuite");
		Validate.notBlank(testSuite, "Test suite not specified");
		
		String environment = request.getParameter("environment");
		Validate.notBlank(environment, "Environment not specified");
		
		String correlationId = request.getParameter("correlationId");
		Validate.notBlank(correlationId, "CorrelationId not specified");
		
		InputStream is = null;
		
		try {
			ReportGenerator reportGenerator = reportGeneratorFactory.createGenerator(mode, template, company, project, reporterModule, testSuite, environment, correlationId);
			is = reportGenerator.generate();
			response.getWriter().write(IOUtils.toString(is));
			response.setContentType(reportGenerator.getContentType().getMimeType());
		} catch (ReportGenerateException | RestCallException | IOException e) {
			throw new ServletException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}

	}

	
	
}
