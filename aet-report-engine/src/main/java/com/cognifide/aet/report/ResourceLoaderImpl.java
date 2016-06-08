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

import com.cognifide.aet.report.api.ReportResource;
import com.cognifide.aet.report.api.ResourceLoader;
import com.google.common.collect.Lists;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Enumeration;
import java.util.List;

@Service
@Component
public class ResourceLoaderImpl implements ResourceLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoaderImpl.class);

	@Override
	public List<ReportResource> getResources(BundleContext context, String resourceDirectory, String filePattern) {
		List<ReportResource> reportResources = Lists.newArrayList();
		Enumeration entries = context.getBundle()
				.findEntries(resourceDirectory, filePattern, true);
		while (entries.hasMoreElements()) {
			URL item = (URL) entries.nextElement();
			reportResources.add(new ReportResource(item, resourceDirectory));
			LOGGER.trace("Resource '{}' has been loaded", item);
		}
		return reportResources;
	}
}
