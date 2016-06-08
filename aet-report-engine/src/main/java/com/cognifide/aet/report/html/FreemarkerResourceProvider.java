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
import com.cognifide.aet.report.api.ResourceLoader;
import com.cognifide.aet.report.api.ResourceProvider;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;

import java.util.List;

@Service
@Component
public class FreemarkerResourceProvider implements ResourceProvider {

	private static final String NAME = "aet-core-freemarker-resource-loader";

	private static final String TEMPLATES_DIR = "templates/freemarker";

	private static final String FILE_PATTERN = "*.ftl";

	private BundleContext context;

	@Reference
	private ResourceLoader resourceLoader;

	@Activate
	public void activate(BundleContext context) {
		this.context = context;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<ReportResource> getResources() {
		return resourceLoader.getResources(context, TEMPLATES_DIR, FILE_PATTERN);
	}

}
