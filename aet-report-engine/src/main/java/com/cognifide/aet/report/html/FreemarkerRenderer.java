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

import com.cognifide.aet.report.api.ReportResourceRegistry;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public final class FreemarkerRenderer {

	private static final String DEFAULT_TEMPLATES_DIR = "/templates/freemarker";

	private static ReportResourceRegistry reportResourceRegistry;

	private final Configuration cfg;

	private FreemarkerRenderer() {
		cfg = new Configuration();
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setDefaultEncoding("UTF-8");
		cfg.setNumberFormat("0.##");
		cfg.setTemplateExceptionHandler(new ReportTemplateExceptionHandler());

		TemplateLoader[] loaders = { new ClassTemplateLoader(this.getClass(), DEFAULT_TEMPLATES_DIR),
				new ReportResourceTemplateLoader(reportResourceRegistry), };

		cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
	}

	public static FreemarkerRenderer getDefaultInstance(ReportResourceRegistry reportResourceRegistry) {
		FreemarkerRenderer.reportResourceRegistry = reportResourceRegistry;
		return new FreemarkerRenderer();
	}

	public static FreemarkerRenderer getInstanceFromFile(ReportResourceRegistry reportResourceRegistry,
			File templatesPath) throws IOException {
		FreemarkerRenderer.reportResourceRegistry = reportResourceRegistry;
		return new FreemarkerRenderer();
	}

	public String renderFromTemplate(String templateName, HtmlReportResultMetadata model)
			throws FreemarkerRendererException {
		Writer out = new StringWriter();
		try {
			Template template = cfg.getTemplate(templateName);
			template.process(model, out);
		} catch (IOException e) {
			throw new FreemarkerRendererException("Failed to load template: " + templateName, e);
		} catch (TemplateException e) {
			throw new FreemarkerRendererException("Failed to process template: " + templateName, e);
		}
		return out.toString();
	}

}
