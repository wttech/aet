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

import com.cognifide.aet.report.api.ReportResource;
import com.cognifide.aet.report.api.ReportResourceRegistry;

import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ReportResourceTemplateLoader implements TemplateLoader {

	private final ReportResourceRegistry reportResourceRegistry;

	public ReportResourceTemplateLoader(ReportResourceRegistry reportResourceRegistry) {
		this.reportResourceRegistry = reportResourceRegistry;
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		InputStream inputStream = null;
		ReportResource resource = reportResourceRegistry.getResource(name);
		if (resource != null) {
			inputStream = resource.getUrl().openStream();
		}
		return inputStream;
	}

	@Override
	public long getLastModified(Object templateSource) {
		//Not used. Returns the time of last modification of the specified template source.
		return -1;
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return new InputStreamReader((InputStream) templateSource, encoding);
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource != null) {
			((InputStream) templateSource).close();
		}
	}
}
