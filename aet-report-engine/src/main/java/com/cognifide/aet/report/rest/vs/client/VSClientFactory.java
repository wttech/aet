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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

@Component(metatype = true, immediate=true, policy=ConfigurationPolicy.REQUIRE, label = "Version Storage Client factory", description = "Produces HTTP client to reach versions rest api")
@Service(VSClientFactory.class)
public class VSClientFactory {
	private static final String P_ENDPOINT_LOCATION = "endpointLocation";
	private static final String DEFAULT_ENDPOINT_LOCATION = "http://localhost:8181/cxf/aet";
	
	@Property(name = P_ENDPOINT_LOCATION, label = "Endpoint Location", value = DEFAULT_ENDPOINT_LOCATION, description = "AET Version API endpoint location")
	private String endpointLocation;
	
	@Activate
	protected void activate(ComponentContext ctx) {
		endpointLocation = PropertiesUtil.toString(ctx.getProperties().get(P_ENDPOINT_LOCATION), DEFAULT_ENDPOINT_LOCATION);
	}

	public ReportResultGetClient createReportClient() {
		return new ReportResultGetClient(endpointLocation);
	}
}
