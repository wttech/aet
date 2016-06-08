/*
 * Cognifide AET :: Data Storage GridFs Implementation
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
package com.cognifide.aet.vs.gridfs;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.google.common.collect.Lists;

public final class RebasePatternsHelper {

	private RebasePatternsHelper() {
	}

	public static Map<String, String> getRebasePatternParameters(String serverUrl,
			PatternMetadata nodeMetadata) {
		Map<String, String> params = new HashMap<>();
		params.put("company", nodeMetadata.getCompany());
		params.put("project", nodeMetadata.getProject());
		params.put("testSuiteName", nodeMetadata.getTestSuiteName());
		params.put("environment", nodeMetadata.getEnvironment());
		params.put("servletUrl", serverUrl);
		params.put("rebasePath", StringUtils.join(
				Lists.newArrayList(nodeMetadata.getCompany(), nodeMetadata.getProject(),
						nodeMetadata.getTestSuiteName(), nodeMetadata.getEnvironment()),
				GridFsHelper.PATH_SEPARATOR));
		params.put("rebaseCorrelationId", nodeMetadata.getRebaseCorrelationId());
		return params;
	}

}
