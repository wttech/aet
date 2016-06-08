/*
 * Cognifide AET :: Runner
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
package com.cognifide.aet.runner.validation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.google.common.collect.Lists;

public final class ExtendedUrlsValidator {

	private static final String ERROR_NAME_INVALID = "URL name can't be blank!";

	private ExtendedUrlsValidator() {
		// private util constructor
	}

	public static List<String> validate(List<ExtendedUrl> extendedUrls) {
		List<String> errors = Lists.newArrayList();
		for (ExtendedUrl extendedUrl : extendedUrls) {
			String name = extendedUrl.getName();
			if (StringUtils.isBlank(name)) {
				errors.add(ERROR_NAME_INVALID);
			}
		}
		return errors;
	}

}
