/*
 * Cognifide AET :: Data Storage API
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
package com.cognifide.aet.vs;

import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * @author lukasz.wieczorek
 */
public class ComparatorProperties implements Serializable {

	private static final long serialVersionUID = -5618612473686293340L;

	private final String comparatorTitle;

	private final String testName;

	private final String url;

	private final String urlName;

	private final String description;

	private final String collectorModule;

	private final String collectorModuleName;

	private final String comparatorModule;

	private final String comparatorModuleName;

	private final String domain;

	public ComparatorProperties(CompareMetadata nodeMetadata) {
		this.comparatorTitle = getComparatorTitle(nodeMetadata);
		this.testName = nodeMetadata.getTestName();
		this.domain = nodeMetadata.getDomain();
		this.url = nodeMetadata.getUrl();
		this.urlName = nodeMetadata.getUrlName();
		this.description = nodeMetadata.getDescription();
		this.collectorModule = nodeMetadata.getCollectorModule();
		this.collectorModuleName = nodeMetadata.getCollectorModuleName();
		this.comparatorModule = nodeMetadata.getComparatorModule();
		this.comparatorModuleName = nodeMetadata.getComparatorModuleName();
	}

	public String getDomain() {
		return domain;
	}

	public String getComparatorTitle() {
		return comparatorTitle;
	}

	public String getTestName() {
		return testName;
	}

	public String getUrl() {
		return url;
	}

	public String getUrlName() {
		return urlName;
	}

	public String getDescription() {
		return description;
	}

	public String getCollectorModule() {
		return collectorModule;
	}

	public String getCollectorModuleName() {
		return collectorModuleName;
	}

	public String getComparatorModule() {
		return comparatorModule;
	}

	public String getComparatorModuleName() {
		return comparatorModuleName;
	}

	private static String getComparatorTitle(CompareMetadata comparatorNodeMetadata) {
		String result;
		String collector = Objects.firstNonNull(comparatorNodeMetadata.getCollectorModuleName(),
				comparatorNodeMetadata.getCollectorModule());
		String comparator = Objects.firstNonNull(comparatorNodeMetadata.getComparatorModuleName(),
				comparatorNodeMetadata.getComparatorModule());
		if (collector.equals(comparator)) {
			result = collector;
		} else {
			result = String.format("%s for %s", comparator, collector);
		}
		return result;
	}

}
