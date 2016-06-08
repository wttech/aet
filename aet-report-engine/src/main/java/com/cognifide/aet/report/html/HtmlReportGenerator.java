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
package com.cognifide.aet.report.html;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;

import com.cognifide.aet.report.api.ReportGenerateException;
import com.cognifide.aet.report.api.ReportGenerator;
import com.cognifide.aet.report.api.ReportResourceRegistry;


public class HtmlReportGenerator implements ReportGenerator{
	public static final String NAME = "html-report";

	private static final String DEFAULT_TEMPLATE_NAME = "default";

	private static final String REDIRECT_TEMPLATE_NAME = "redirect";

	private static final String ONLINE_MODE = "online";

	private final FreemarkerRenderer renderer;

	private String template;

	private String mode;
	
	private HtmlReportResultMetadata reportData;
	

	public HtmlReportGenerator(ReportResourceRegistry reportResourceRegistry, String mode, String template, HtmlReportResultMetadata result) {
		renderer = FreemarkerRenderer.getDefaultInstance(reportResourceRegistry);
		this.mode = mode;
		this.template = template;
		reportData = result;
	}

	@Override
	public InputStream generate() throws ReportGenerateException {
		InputStream stream;
		try {
			if (ONLINE_MODE.equals(mode)) {
				stream = IOUtils.toInputStream(renderer.renderFromTemplate(REDIRECT_TEMPLATE_NAME + ".ftl", reportData), "UTF-8");
			}else {
				String templateToRender = template != null ? template : DEFAULT_TEMPLATE_NAME;
				stream = IOUtils.toInputStream(renderer.renderFromTemplate(templateToRender + ".ftl", reportData), "UTF-8");
			}
		} catch (FreemarkerRendererException | IOException | ClassCastException e) {
			throw new ReportGenerateException(e.getLocalizedMessage(), e);
		}
		
		return stream;
	}

	@Override
	public ContentType getContentType() {
		return ContentType.TEXT_HTML;
	}

}