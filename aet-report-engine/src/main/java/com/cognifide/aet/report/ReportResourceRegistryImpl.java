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
import com.cognifide.aet.report.api.ReportResourceRegistry;
import com.cognifide.aet.report.api.ResourceProvider;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Component(immediate = true, description = "AET Report Resource Registry", label = "AET Report Resource Registry")
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd") })
public class ReportResourceRegistryImpl implements ReportResourceRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportResourceRegistryImpl.class);

	@Reference(referenceInterface = ResourceProvider.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, bind = "bindResourceProvider", unbind = "unbindResourceProvider")
	private Map<String, ResourceProvider> resourceProviders = new ConcurrentHashMap<String, ResourceProvider>();

	@Override
	public ResourceProvider getResourceProvider(String name) {
		return resourceProviders.get(name);
	}

	@Override
	public Map<String, ResourceProvider> getResourceProviders() {
		return resourceProviders;
	}

	@Override
	public List<ReportResource> getResources() {
		List<ReportResource> resources = Lists.newArrayList();
		for (Map.Entry<String, ResourceProvider> entry : resourceProviders.entrySet()) {
			resources.addAll(entry.getValue().getResources());
		}
		return resources;
	}

	@Override
	public ReportResource getResource(final String name) {
		return Iterables.find(getResources(),new Predicate<ReportResource>() {
			@Override
			public boolean apply(ReportResource reportResource) {
				if(reportResource != null && reportResource.getName().equals(name)){
					return true;
				}
				return false;
			}
		},null);
	}

	// ######## Binding related methods
	protected void bindResourceProvider(ResourceProvider resourceLoaderFactory){
		LOGGER.info("Binding collector: {}", resourceLoaderFactory.getName());
		resourceProviders.put(resourceLoaderFactory.getName(), resourceLoaderFactory);
	}

	protected void unbindResourceProvider(ResourceProvider resourceLoaderFactory){
		LOGGER.info("Unbinding collector: {}", resourceLoaderFactory.getName());
		resourceProviders.remove(resourceLoaderFactory.getName());
	}
}
