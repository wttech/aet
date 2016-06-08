/*
 * Cognifide AET :: Job Common
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
package com.cognifide.aet.job.common.collectors.analytics;

import com.cognifide.aet.vs.CollectorResult;
import net.lightbody.bmp.core.har.HarEntry;

import java.util.Collection;
import java.util.HashSet;

public class AnalyticsCollectorResult extends CollectorResult {

	private static final long serialVersionUID = 2625790517002752650L;

	private final HashSet<HarEntry> entries;

	public AnalyticsCollectorResult(String url, Collection<HarEntry> siteCatalystEntries) {
		super(url);
		this.entries = new HashSet<>(siteCatalystEntries);
	}

	public Collection<HarEntry> getEntries() {
		return entries;
	}
}
